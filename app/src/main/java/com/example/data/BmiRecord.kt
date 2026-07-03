package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_records")
data class BmiRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weightKg: Float,
    val heightCm: Float,
    val age: Int,
    val gender: String, // "Male", "Female", "Other"
    val bmi: Float,
    val category: String, // "Underweight", "Normal", "Overweight", "Obese"
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
