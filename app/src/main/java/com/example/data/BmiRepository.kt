package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BmiRepository(private val bmiDao: BmiDao) {
    val allRecords: Flow<List<BmiRecord>> = bmiDao.getAllRecords()

    suspend fun insertRecord(record: BmiRecord) {
        bmiDao.insertRecord(record)
    }

    suspend fun deleteRecordById(id: Int) {
        bmiDao.deleteRecordById(id)
    }

    suspend fun clearAllRecords() {
        bmiDao.clearAllRecords()
    }

    fun getNutritionalAdvice(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        gender: String,
        bmi: Float,
        category: String,
        activityLevel: String,
        goal: String
    ): Flow<Result<String>> = flow {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            emit(Result.failure(Exception("Gemini API key is not configured in Secrets. Please add GEMINI_API_KEY to the Secrets panel in AI Studio.")))
            return@flow
        }

        val prompt = """
            You are an elite, highly credentialed sports clinical nutritionist and weight management expert.
            Generate a highly professional, visually structured, and personalized nutritional and wellness report based on the following patient profile:
            - Age: $age years old
            - Gender: $gender
            - Current Weight: $weightKg kg
            - Current Height: $heightCm cm
            - Calculated BMI: ${"%.1f".format(bmi)} ($category)
            - Daily Activity Level: $activityLevel
            - Fitness/Wellness Goal: $goal

            Format the output beautifully using Markdown with clear headings and formatting.
            Provide the following specific sections:
            
            ### 📊 Assessment Summary
            A brief expert analysis of their current BMI and activity status. Keep it encouraging but realistic.
            
            ### 🍎 Personalized Caloric & Macronutrient Goals
            Estimate their Daily TDEE (Total Daily Energy Expenditure) and suggest a target daily calorie intake. Break down recommended macro percentages (Proteins, Carbs, Fats) with examples of whole-food sources.
            
            ### 🥗 Dietary Recommendations & Meal Structure
            3-4 specific meal and nutrition tips (e.g. portion control, hydration, protein timing) suited to their goal.
            
            ### 🏋️ Health & Lifestyle Insights
            Specific physical activity or habit modifications to optimize long-term success.

            Ensure the advice is practical, highly actionable, scientifically grounded, and tailored strictly to their profile.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are a professional clinical sports dietitian. Keep advice scientifically rigorous, practical, motivating, and neatly formatted in Markdown.")))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val advice = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (advice != null) {
                emit(Result.success(advice))
            } else {
                emit(Result.failure(Exception("No response could be extracted from Gemini API.")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
