package com.example.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    fun exportHistoryToPdf(context: Context, records: List<BmiRecord>): File? {
        val document = PdfDocument()
        
        // A4 page dimensions: 595 x 842 points (1 point = 1/72 inch)
        val pageWidth = 595
        val pageHeight = 842
        
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        
        val paint = Paint()
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }
        
        val titlePaint = Paint().apply {
            color = Color.rgb(16, 124, 65) // Modern Emerald Green
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        
        val subTitlePaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = Color.rgb(230, 245, 235) // Modern light sage mint background
            isAntiAlias = true
        }
        
        val headerTextPaint = Paint().apply {
            color = Color.rgb(16, 124, 65)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        // 1. Draw Header Banner
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 110f, Paint().apply { color = Color.rgb(240, 250, 243) })
        canvas.drawText("BMI Fit Tracker - Health Summary", 40f, 55f, titlePaint)
        
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date())
        canvas.drawText("Report Generated on: $dateString", 40f, 80f, subTitlePaint)
        
        // 2. Draw Summary Box
        var startY = 130f
        canvas.drawRoundRect(40f, startY, pageWidth - 40f, startY + 75f, 6f, 6f, Paint().apply {
            color = Color.rgb(248, 249, 250)
            style = Paint.Style.FILL
        })
        canvas.drawRoundRect(40f, startY, pageWidth - 40f, startY + 75f, 6f, 6f, Paint().apply {
            color = Color.rgb(220, 224, 230)
            style = Paint.Style.STROKE
            strokeWidth = 1f
        })
        
        val avgBmi = if (records.isNotEmpty()) records.map { it.bmi }.average().toFloat() else 0f
        val latestRecord = records.firstOrNull()
        
        canvas.drawText("Total Recorded Log entries: ${records.size}", 60f, startY + 28f, textPaint.apply { typeface = Typeface.DEFAULT_BOLD })
        canvas.drawText("Overall Average BMI: ${"%.1f".format(avgBmi)}", 60f, startY + 52f, textPaint)
        
        val statusString = latestRecord?.let { "Latest BMI: ${"%.1f".format(it.bmi)} (${it.category})" } ?: "Latest BMI: Not available"
        canvas.drawText(statusString, pageWidth / 2f + 20f, startY + 28f, textPaint)
        
        // 3. Draw Table Headers
        startY += 105f
        val colPositions = floatArrayOf(40f, 160f, 250f, 340f, 420f)
        
        canvas.drawRect(40f, startY, pageWidth - 40f, startY + 24f, headerPaint)
        val headers = arrayOf("Date/Time", "Weight (kg)", "Height (cm)", "BMI", "Status")
        for (i in headers.indices) {
            canvas.drawText(headers[i], colPositions[i] + 10f, startY + 16f, headerTextPaint)
        }
        
        // 4. Draw Rows
        var rowY = startY + 24f
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        
        val maxRows = 18
        val listToDraw = records.take(maxRows)
        
        val linePaint = Paint().apply {
            color = Color.rgb(235, 238, 242)
            strokeWidth = 1f
        }
        
        for (record in listToDraw) {
            canvas.drawLine(40f, rowY, pageWidth - 40f, rowY, linePaint)
            
            val dateStr = dateFormat.format(Date(record.timestamp))
            canvas.drawText(dateStr, colPositions[0] + 10f, rowY + 18f, textPaint.apply { typeface = Typeface.DEFAULT; textSize = 9f })
            canvas.drawText("%.1f kg".format(record.weightKg), colPositions[1] + 10f, rowY + 18f, textPaint)
            canvas.drawText("%.1f cm".format(record.heightCm), colPositions[2] + 10f, rowY + 18f, textPaint)
            canvas.drawText("%.1f".format(record.bmi), colPositions[3] + 10f, rowY + 18f, textPaint)
            
            // Status with distinct Material-inspired colors
            val statusColor = when (record.category) {
                "Underweight" -> Color.rgb(33, 150, 243)  // Clean Blue
                "Normal" -> Color.rgb(76, 175, 80)       // Clean Green
                "Overweight" -> Color.rgb(255, 152, 0)   // Clean Orange
                else -> Color.rgb(244, 67, 54)            // Clean Red
            }
            val statusPaint = Paint().apply {
                color = statusColor
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText(record.category, colPositions[4] + 10f, rowY + 18f, statusPaint)
            
            rowY += 26f
        }
        
        canvas.drawLine(40f, rowY, pageWidth - 40f, rowY, linePaint)
        
        if (records.size > maxRows) {
            canvas.drawText("... and ${records.size - maxRows} more history entries saved locally in the app database ...", 40f, rowY + 18f, subTitlePaint)
        }
        
        // Footer line
        canvas.drawText("Stay active & healthy! Generated using BMI Fit Tracker Android app.", 40f, pageHeight - 40f, subTitlePaint)
        
        document.finishPage(page)
        
        val file = File(context.getExternalFilesDir(null), "BMI_Fitness_Report.pdf")
        return try {
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }
}
