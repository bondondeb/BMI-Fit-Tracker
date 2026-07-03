package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface AdviceUiState {
    object Idle : AdviceUiState
    object Loading : AdviceUiState
    data class Success(val advice: String) : AdviceUiState
    data class Error(val message: String) : AdviceUiState
}

class BmiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = BmiRepository(database.bmiDao())
    private val sharedPrefs = application.getSharedPreferences("bmi_fitness_goals", Context.MODE_PRIVATE)

    // Target BMI State Flow - defaults to 22.0f if not set but can be customized
    val targetBmi = MutableStateFlow<Float?>(
        if (sharedPrefs.contains("target_bmi")) sharedPrefs.getFloat("target_bmi", 22.0f) else null
    )

    fun setTargetBmi(value: Float?) {
        targetBmi.value = value
        if (value != null) {
            sharedPrefs.edit().putFloat("target_bmi", value).apply()
        } else {
            sharedPrefs.edit().remove("target_bmi").apply()
        }
    }

    // Theme Mode: "light", "dark", "system"
    val themeMode = MutableStateFlow(
        sharedPrefs.getString("theme_mode", "system") ?: "system"
    )

    fun setThemeMode(mode: String) {
        themeMode.value = mode
        sharedPrefs.edit().putString("theme_mode", mode).apply()
    }

    // Expose BMI records
    val historyRecords: StateFlow<List<BmiRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current inputs
    var weightInput = MutableStateFlow("70.0")
    var heightInput = MutableStateFlow("175.0")
    var ageInput = MutableStateFlow("25")
    var genderSelection = MutableStateFlow("Male") // Male, Female, Other
    var notesInput = MutableStateFlow("")

    // Validation State Flows
    private val _weightError = MutableStateFlow<String?>(null)
    val weightError: StateFlow<String?> = _weightError

    private val _heightError = MutableStateFlow<String?>(null)
    val heightError: StateFlow<String?> = _heightError

    private val _ageError = MutableStateFlow<String?>(null)
    val ageError: StateFlow<String?> = _ageError

    // Helper state for button enabling
    val isInputValid: StateFlow<Boolean> = combine(weightError, heightError, ageError) { wErr, hErr, aErr ->
        wErr == null && hErr == null && aErr == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    // Nutritional Advice goals and activity
    var activityLevel = MutableStateFlow("Moderately Active") // Sedentary, Lightly Active, Moderately Active, Highly Active
    var fitnessGoal = MutableStateFlow("Maintain Weight") // Lose Weight, Maintain Weight, Build Muscle

    // Calculator output state
    private val _calculatedBmi = MutableStateFlow<Float?>(null)
    val calculatedBmi: StateFlow<Float?> = _calculatedBmi

    private val _bmiCategory = MutableStateFlow("")
    val bmiCategory: StateFlow<String> = _bmiCategory

    // Advice generation state
    private val _adviceState = MutableStateFlow<AdviceUiState>(AdviceUiState.Idle)
    val adviceState: StateFlow<AdviceUiState> = _adviceState

    // Live Step counter
    private val stepTracker = SensorStepTracker(application)
    val liveSteps: StateFlow<Int> = stepTracker.stepCount

    private val _isTrackingSteps = MutableStateFlow(false)
    val isTrackingSteps: StateFlow<Boolean> = _isTrackingSteps

    // Wearable Sync State
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    private val _syncMessage = MutableStateFlow("Not Synced")
    val syncMessage: StateFlow<String> = _syncMessage

    private val _connectedWearable = MutableStateFlow("None") // Fitbit, Garmin, Wear OS, Apple Health
    val connectedWearable: StateFlow<String> = _connectedWearable

    init {
        // Run validations initially
        validateWeight(weightInput.value)
        validateHeight(heightInput.value)
        validateAge(ageInput.value)
        calculateBmi()
    }

    private fun validateWeight(weight: String) {
        if (weight.isBlank()) {
            _weightError.value = "Weight cannot be empty"
            return
        }
        val w = weight.toFloatOrNull()
        if (w == null) {
            _weightError.value = "Invalid decimal number"
        } else if (w < 20.0f || w > 350.0f) {
            _weightError.value = "Must be between 20 and 350 kg"
        } else {
            _weightError.value = null
        }
    }

    private fun validateHeight(height: String) {
        if (height.isBlank()) {
            _heightError.value = "Height cannot be empty"
            return
        }
        val h = height.toFloatOrNull()
        if (h == null) {
            _heightError.value = "Invalid decimal number"
        } else if (h < 50.0f || h > 250.0f) {
            _heightError.value = "Must be between 50 and 250 cm"
        } else {
            _heightError.value = null
        }
    }

    private fun validateAge(age: String) {
        if (age.isBlank()) {
            _ageError.value = "Age cannot be empty"
            return
        }
        val a = age.toIntOrNull()
        if (a == null) {
            _ageError.value = "Invalid whole number"
        } else if (a < 1 || a > 120) {
            _ageError.value = "Must be between 1 and 120 years"
        } else {
            _ageError.value = null
        }
    }

    fun setWeight(weight: String) {
        weightInput.value = weight
        validateWeight(weight)
        calculateBmi()
    }

    fun setHeight(height: String) {
        heightInput.value = height
        validateHeight(height)
        calculateBmi()
    }

    fun setAge(age: String) {
        ageInput.value = age
        validateAge(age)
    }

    fun setGender(gender: String) {
        genderSelection.value = gender
    }

    fun setNotes(notes: String) {
        notesInput.value = notes
    }

    fun setActivity(activity: String) {
        activityLevel.value = activity
    }

    fun setGoal(goal: String) {
        fitnessGoal.value = goal
    }

    // Mathematical calculations
    fun calculateBmi() {
        val w = weightInput.value.toFloatOrNull()
        val h = heightInput.value.toFloatOrNull() // in cm
        val isWValid = w != null && w >= 20.0f && w <= 350.0f
        val isHValid = h != null && h >= 50.0f && h <= 250.0f
        
        if (isWValid && isHValid && h != null && h > 0f) {
            val heightM = h / 100f
            val score = w!! / (heightM * heightM)
            _calculatedBmi.value = score
            _bmiCategory.value = determineCategory(score)
        } else {
            _calculatedBmi.value = null
            _bmiCategory.value = ""
        }
    }

    private fun determineCategory(score: Float): String {
        return when {
            score < 18.5f -> "Underweight"
            score >= 18.5f && score < 25.0f -> "Normal"
            score >= 25.0f && score < 30.0f -> "Overweight"
            else -> "Obese"
        }
    }

    // Database insertions
    fun saveBmiRecord() {
        val w = weightInput.value.toFloatOrNull() ?: return
        val h = heightInput.value.toFloatOrNull() ?: return
        val age = ageInput.value.toIntOrNull() ?: 25
        val gender = genderSelection.value
        val score = _calculatedBmi.value ?: return
        val cat = _bmiCategory.value
        val notes = notesInput.value

        viewModelScope.launch(Dispatchers.IO) {
            val record = BmiRecord(
                weightKg = w,
                heightCm = h,
                age = age,
                gender = gender,
                bmi = score,
                category = cat,
                notes = notes
            )
            repository.insertRecord(record)
            // Reset notes
            notesInput.value = ""
        }
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecordById(id)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllRecords()
        }
    }

    // Call Gemini API for customized nutritional guidance
    fun generateAdvice() {
        val w = weightInput.value.toFloatOrNull() ?: return
        val h = heightInput.value.toFloatOrNull() ?: return
        val age = ageInput.value.toIntOrNull() ?: 25
        val gender = genderSelection.value
        val score = _calculatedBmi.value ?: return
        val cat = _bmiCategory.value
        val activity = activityLevel.value
        val goal = fitnessGoal.value

        _adviceState.value = AdviceUiState.Loading

        viewModelScope.launch {
            repository.getNutritionalAdvice(
                weightKg = w,
                heightCm = h,
                age = age,
                gender = gender,
                bmi = score,
                category = cat,
                activityLevel = activity,
                goal = goal
            ).collect { result ->
                result.fold(
                    onSuccess = { adviceText ->
                        _adviceState.value = AdviceUiState.Success(adviceText)
                    },
                    onFailure = { error ->
                        _adviceState.value = AdviceUiState.Error(error.message ?: "An unknown error occurred.")
                    }
                )
            }
        }
    }

    // Native step counter control
    fun toggleStepTracking() {
        if (_isTrackingSteps.value) {
            stepTracker.stopTracking()
            _isTrackingSteps.value = false
        } else {
            stepTracker.startTracking()
            _isTrackingSteps.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepTracker.stopTracking()
    }

    // Simulated wearable health device integration
    fun syncWithWearable(wearableBrand: String) {
        _isSyncing.value = true
        _connectedWearable.value = wearableBrand
        _syncMessage.value = "Establishing secure Bluetooth connection to $wearableBrand..."

        viewModelScope.launch {
            delay(1500)
            _syncMessage.value = "Authenticating tokens and fetching telemetry from $wearableBrand Cloud..."
            delay(1200)
            _syncMessage.value = "Parsing biometric records..."
            delay(1000)

            // Randomize healthy parameters based on selected wearable
            val syncedWeight = when (wearableBrand) {
                "Fitbit Sense 2" -> "68.4"
                "Garmin Fenix 7" -> "71.2"
                "Apple Health" -> "69.5"
                else -> "72.8"
            }
            val syncedHeight = when (wearableBrand) {
                "Fitbit Sense 2" -> "176.0"
                "Garmin Fenix 7" -> "178.5"
                "Apple Health" -> "174.0"
                else -> "175.0"
            }

            weightInput.value = syncedWeight
            heightInput.value = syncedHeight
            calculateBmi()

            _isSyncing.value = false
            _syncMessage.value = "Successfully synced! Loaded: weight $syncedWeight kg, height $syncedHeight cm. BMI updated."
            
            // Automatically insert record into database to keep track!
            val score = _calculatedBmi.value ?: 22.0f
            val record = BmiRecord(
                weightKg = syncedWeight.toFloat(),
                heightCm = syncedHeight.toFloat(),
                age = ageInput.value.toIntOrNull() ?: 25,
                gender = genderSelection.value,
                bmi = score,
                category = determineCategory(score),
                notes = "Auto-synced from wearable health device ($wearableBrand)."
            )
            repository.insertRecord(record)
        }
    }

    // Export to PDF
    fun exportToPdf(context: Context, onCompleted: (File?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val records = historyRecords.value
            val pdfFile = PdfExporter.exportHistoryToPdf(context, records)
            withContext(Dispatchers.Main) {
                onCompleted(pdfFile)
            }
        }
    }

    // Share PDF function
    fun sharePdf(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share BMI Fitness Summary PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}

// ViewModel Factory
class BmiViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BmiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BmiViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
