package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BmiRecord
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.itemsIndexed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: ImageVector) {
    CALCULATOR("Calculator", Icons.Default.Add),
    HISTORY("History Log", Icons.Default.List),
    AI_ADVICE("AI Insights", Icons.Default.Favorite),
    WEARABLE_SYNC("Sync & Steps", Icons.Default.Refresh)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiAppScreen(viewModel: BmiViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.CALCULATOR) }
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    var showThemeMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile info left
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BD",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Column {
                            Text(
                                text = "Good morning,",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Bondon Deb",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Quick action buttons right
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    Toast.makeText(context, "No new alerts", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Theme Switcher Button with Dropdown Selector
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    showThemeMenu = true
                                }
                                .testTag("theme_switcher_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            val themeIcon = when (themeMode) {
                                "light" -> Icons.Default.LightMode
                                "dark" -> Icons.Default.DarkMode
                                else -> Icons.Default.BrightnessAuto
                            }
                            Icon(
                                imageVector = themeIcon,
                                contentDescription = "Switch Theme",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            DropdownMenu(
                                expanded = showThemeMenu,
                                onDismissRequest = { showThemeMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Light Mode") },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.LightMode, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        if (themeMode == "light") {
                                            Icon(
                                                imageVector = Icons.Default.Check, 
                                                contentDescription = "Selected", 
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setThemeMode("light")
                                        showThemeMenu = false
                                        Toast.makeText(context, "Theme: Light Mode", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("theme_light_option")
                                )
                                DropdownMenuItem(
                                    text = { Text("Dark Mode") },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.DarkMode, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        if (themeMode == "dark") {
                                            Icon(
                                                imageVector = Icons.Default.Check, 
                                                contentDescription = "Selected", 
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setThemeMode("dark")
                                        showThemeMenu = false
                                        Toast.makeText(context, "Theme: Dark Mode", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("theme_dark_option")
                                )
                                DropdownMenuItem(
                                    text = { Text("System Default") },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.BrightnessAuto, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        if (themeMode == "system") {
                                            Icon(
                                                imageVector = Icons.Default.Check, 
                                                contentDescription = "Selected", 
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setThemeMode("system")
                                        showThemeMenu = false
                                        Toast.makeText(context, "Theme: System Default", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("theme_system_option")
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Modern Styled Tab row (Scrollable Pill layout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = tab == currentTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { currentTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.title.split(" ").first(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Screen content switching with fade animation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_transition"
                ) { targetTab ->
                    when (targetTab) {
                        AppTab.CALCULATOR -> CalculatorTab(viewModel)
                        AppTab.HISTORY -> HistoryTab(viewModel)
                        AppTab.AI_ADVICE -> AiAdviceTab(viewModel)
                        AppTab.WEARABLE_SYNC -> WearableSyncTab(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorTab(viewModel: BmiViewModel) {
    val weight by viewModel.weightInput.collectAsStateWithLifecycle()
    val height by viewModel.heightInput.collectAsStateWithLifecycle()
    val age by viewModel.ageInput.collectAsStateWithLifecycle()
    val gender by viewModel.genderSelection.collectAsStateWithLifecycle()
    val notes by viewModel.notesInput.collectAsStateWithLifecycle()

    val weightError by viewModel.weightError.collectAsStateWithLifecycle()
    val heightError by viewModel.heightError.collectAsStateWithLifecycle()
    val ageError by viewModel.ageError.collectAsStateWithLifecycle()
    val isInputValid by viewModel.isInputValid.collectAsStateWithLifecycle()

    val bmi by viewModel.calculatedBmi.collectAsStateWithLifecycle()
    val category by viewModel.bmiCategory.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Elegant Welcome Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Empower Your Wellness Journey",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculate your Body Mass Index (BMI) and generate customized fitness plans to unlock clinical nutritional advice.",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            // Main Input Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Biometric Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Gender Selector Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            val isSelected = option == gender
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { viewModel.setGender(option) }
                                    .testTag("gender_${option.lowercase()}_card")
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Height & Weight row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { viewModel.setHeight(it) },
                            label = { Text("Height (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            isError = heightError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            trailingIcon = {
                                if (heightError != null) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Height Error",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (height.isNotBlank()) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Height Valid",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            supportingText = {
                                if (heightError != null) {
                                    Text(
                                        text = heightError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("height_input")
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { viewModel.setWeight(it) },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            isError = weightError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            trailingIcon = {
                                if (weightError != null) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Weight Error",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (weight.isNotBlank()) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Weight Valid",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            supportingText = {
                                if (weightError != null) {
                                    Text(
                                        text = weightError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("weight_input")
                        )
                    }

                    // Age input
                    OutlinedTextField(
                        value = age,
                        onValueChange = { viewModel.setAge(it) },
                        label = { Text("Age (years)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = ageError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        trailingIcon = {
                            if (ageError != null) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Age Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (age.isNotBlank()) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Age Valid",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        supportingText = {
                            if (ageError != null) {
                                Text(
                                    text = ageError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("age_input")
                    )

                    // Optional Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.setNotes(it) },
                        label = { Text("Notes (e.g. Activity level, morning weight)") },
                        singleLine = false,
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notes_input")
                    )
                }
            }
        }

        item {
            AnimatedContent(
                targetState = bmi,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.98f, animationSpec = tween(350)))
                        .togetherWith(fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.98f, animationSpec = tween(200)))
                },
                label = "bmi_result_container_transition"
            ) { targetBmi ->
                if (targetBmi != null) {
                    val categoryColor = when (category) {
                        "Underweight" -> Color(0xFF2563EB)  // Blue
                        "Normal" -> Color(0xFF059669)       // Teal
                        "Overweight" -> Color(0xFFD97706)   // Amber/Orange
                        else -> Color(0xFFDC2626)            // Crimson Red
                    }

                    val animatedCategoryColor by animateColorAsState(
                        targetValue = categoryColor,
                        animationSpec = tween(350),
                        label = "category_color_fade"
                    )

                    val currentConnectedWearable by viewModel.connectedWearable.collectAsStateWithLifecycle()

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Geometric summary container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(1.dp, RoundedCornerShape(32.dp))
                                .animateContentSize()
                                .padding(24.dp)
                        ) {
                            // Absolute top-right decorative element
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(bottomStart = 96.dp))
                                    .background(animatedCategoryColor.copy(alpha = 0.08f))
                                    .align(Alignment.TopEnd)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "BMI GEOMETRIC DISPLAY",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )

                                // Double Concentric Ring Gauge with smooth kinetic spring
                                val progressFraction = when {
                                    targetBmi < 16f -> 0.15f
                                    targetBmi > 35f -> 0.95f
                                    else -> 0.15f + ((targetBmi - 16f) / 19f) * 0.7f
                                }
                                
                                val animatedProgressFraction by animateFloatAsState(
                                    targetValue = progressFraction,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    label = "bmi_progress_spring"
                                )

                                // Physics-based spring pop animation whenever BMI value recalculates
                                var triggerAnim by remember { mutableStateOf(false) }
                                LaunchedEffect(targetBmi) {
                                    triggerAnim = !triggerAnim
                                }
                                val animatedScale by animateFloatAsState(
                                    targetValue = if (triggerAnim) 1.03f else 1.0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "bmi_scale_spring"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(140.dp)
                                        .scale(animatedScale),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Outer full ring
                                    CircularProgressIndicator(
                                        progress = 1.0f,
                                        modifier = Modifier.size(136.dp),
                                        color = animatedCategoryColor.copy(alpha = 0.1f),
                                        strokeWidth = 11.dp,
                                        trackColor = Color.Transparent
                                    )
                                    // Inner dynamic ring matching categoryColor with animated progress slice
                                    CircularProgressIndicator(
                                        progress = animatedProgressFraction,
                                        modifier = Modifier.size(112.dp),
                                        color = animatedCategoryColor,
                                        strokeWidth = 11.dp,
                                        trackColor = Color.Transparent
                                    )
                                    // Centered Value with smooth kinetic spring
                                    val animatedBmiValue by animateFloatAsState(
                                        targetValue = targetBmi,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "bmi_score_ticker"
                                    )

                                    Text(
                                        text = "%.1f".format(animatedBmiValue),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.scale(animatedScale)
                                    )
                                }

                                // Dynamic pill badge
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(animatedCategoryColor.copy(alpha = 0.12f))
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = category.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp,
                                        color = animatedCategoryColor
                                    )
                                }

                                // Weight & Height side-by-side grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Weight box
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Weight",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = weight,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "kg",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    // Height box
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Height",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = height,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "cm",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Sync & Action Section (Side-by-side Add Log and Export PDF buttons)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.saveBmiRecord()
                                    Toast.makeText(context, "Log saved to history!", Toast.LENGTH_SHORT).show()
                                },
                                enabled = isInputValid,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .testTag("save_record_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Log Icon", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Log",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .clickable {
                                        viewModel.exportToPdf(context) { file ->
                                            if (file != null) {
                                                Toast.makeText(context, "PDF saved to: ${file.name}", Toast.LENGTH_LONG).show()
                                                viewModel.sharePdf(context, file)
                                            } else {
                                                Toast.makeText(context, "Failed to generate PDF.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .testTag("export_pdf_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Picture as PDF",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Device Sync Card (Dark / Slate 900 active block)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF0F172A)) // Slate 900
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Sync Watch",
                                            tint = Color(0xFF60A5FA), // Blue 400
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (currentConnectedWearable.isNotEmpty()) currentConnectedWearable else "Mobile Sensors",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "SYNCED TELEMETRY ACTIVE",
                                            color = Color(0xFF94A3B8), // Slate 400
                                            letterSpacing = 1.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                // Pulsing green dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E)) // Green 500
                                )
                            }
                        }

                        // Personalized Insight (The Warm Peach Card)
                        val isDark = MaterialTheme.colorScheme.background.red < 0.5f
                        val insightBgColor = if (isDark) Color(0xFF2C1E1A) else Color(0xFFFDECE7)
                        val insightHeaderColor = if (isDark) Color(0xFFFDBA74) else Color(0xFF9A3412)
                        val insightTextColor = if (isDark) Color(0xFFFFEDD5) else Color(0xFF7C2D12)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(insightBgColor)
                                .animateContentSize()
                                .padding(20.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Insight Icon",
                                        tint = insightHeaderColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "PERSONALIZED INSIGHT",
                                        color = insightHeaderColor,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        fontSize = 11.sp
                                    )
                                }
                                AnimatedContent(
                                    targetState = category,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                                    },
                                    label = "insight_text_fade"
                                ) { targetCategory ->
                                    Text(
                                        text = when (targetCategory) {
                                            "Underweight" -> "Your metabolic targets indicate your energy intake is below target. Consider adding healthy fats such as Greek yogurt, avocados, or almonds to your breakfast to support balanced muscle mass."
                                            "Normal" -> "Your profile is beautifully balanced! Ensure you consume sufficient lean proteins and fiber. Consider adding organic seeds to your breakfast to support lean energy."
                                            "Overweight" -> "To support your fitness goals, focus on high-fiber vegetables and steady hydration. Try replacing processed carbohydrates with dynamic complex alternatives."
                                            else -> "To optimize wellness outcomes, focus on structural cardiovascular activities paired with balanced dietary metrics. Prioritize dynamic walking routines."
                                        },
                                        color = insightTextColor,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Polished Pending / Dotted Empty State Gauge Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .shadow(1.dp, RoundedCornerShape(32.dp))
                            .animateContentSize()
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "BMI REAL-TIME DISPLAY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )

                            // Dotted Pending Circle Indicator
                            Box(
                                modifier = Modifier.size(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val outlineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                Canvas(modifier = Modifier.size(112.dp)) {
                                    drawCircle(
                                        color = outlineColor,
                                        radius = size.minDimension / 2f,
                                        style = Stroke(
                                            width = 4.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                        )
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Pending details icon",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "--.-",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            // Informative guidance badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "AWAITING BIOMETRIC DATA",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "Please ensure height, weight, and age are valid to display your real-time BMI score and body analysis.",
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            FitnessGoalsCard(viewModel = viewModel)
        }
    }
}

@Composable
fun FitnessGoalsCard(viewModel: BmiViewModel) {
    val targetBmi by viewModel.targetBmi.collectAsStateWithLifecycle()
    val historyRecords by viewModel.historyRecords.collectAsStateWithLifecycle()
    val currentBmi by viewModel.calculatedBmi.collectAsStateWithLifecycle()
    val heightStr by viewModel.heightInput.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }
    var sliderValue by remember(targetBmi) { mutableStateOf(targetBmi ?: 22.0f) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(1.dp, RoundedCornerShape(24.dp))
            .testTag("fitness_goals_card")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(primaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Goals Icon",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "FITNESS GOALS",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Target BMI Tracker",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (targetBmi != null && !isEditing) {
                    IconButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.testTag("edit_target_bmi_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Goal",
                            tint = primaryColor
                        )
                    }
                }
            }

            if (targetBmi == null || isEditing) {
                // Goal Setup / Edit UI
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (targetBmi == null) 
                            "Set a target Body Mass Index (BMI) to start tracking your health milestones and visualize progress."
                            else "Adjust your target BMI below:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val targetCategory = when {
                            sliderValue < 18.5f -> "Underweight"
                            sliderValue >= 18.5f && sliderValue < 25.0f -> "Normal"
                            sliderValue >= 25.0f && sliderValue < 30.0f -> "Overweight"
                            else -> "Obese"
                        }
                        val categoryColor = when (targetCategory) {
                            "Underweight" -> Color(0xFF2563EB)
                            "Normal" -> Color(0xFF059669)
                            "Overweight" -> Color(0xFFD97706)
                            else -> Color(0xFFDC2626)
                        }

                        Text(
                            text = "%.1f".format(sliderValue),
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            color = categoryColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(categoryColor.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = targetCategory.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = categoryColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 16f..32f,
                            steps = 160,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_bmi_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("16.0 (Underweight)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("32.0 (Obese)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (targetBmi != null) {
                            OutlinedButton(
                                onClick = { isEditing = false },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("cancel_edit_target_button")
                            ) {
                                Text("Cancel")
                            }
                        }
                        Button(
                            onClick = {
                                viewModel.setTargetBmi(sliderValue)
                                isEditing = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("save_target_bmi_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save target")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Goal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Goal Display UI with Countdown & Progress
                val target = targetBmi!!
                val current = currentBmi

                if (current == null) {
                    // No current BMI calculated yet
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info icon",
                                tint = primaryColor.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Your target is set to %.1f.".format(target),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Please enter your current weight and height above to calculate your current BMI and view your fitness goal progress countdown!",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    val diff = current - target
                    val heightCm = heightStr.toFloatOrNull() ?: 0f
                    val heightM = heightCm / 100f

                    // Calculate weight to lose or gain
                    val weightDiffKg = if (heightM > 0f) {
                        diff * (heightM * heightM)
                    } else 0f

                    val absDiff = if (diff < 0f) -diff else diff
                    val absWeightDiffKg = if (weightDiffKg < 0f) -weightDiffKg else weightDiffKg

                    val hasAchieved = absDiff <= 0.1f

                    val oldestRecord = historyRecords.minByOrNull { it.timestamp }
                    val startingBmi = oldestRecord?.bmi ?: (current + diff * 0.5f)

                    val totalJourney = (startingBmi - target).let { if (it == 0f) 1f else it }
                    val currentJourneyLeft = current - target
                    val progressFraction = if (totalJourney != 0f) {
                        (1f - (currentJourneyLeft / totalJourney)).coerceIn(0f, 1f)
                    } else 1f

                    val isLossGoal = diff > 0f
                    val goalLabelText = if (isLossGoal) "WEIGHT LOSS TARGET" else "WEIGHT GAIN TARGET"
                    val colorBox = if (hasAchieved) Color(0xFF059669) else if (isLossGoal) Color(0xFFD97706) else Color(0xFF2563EB)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorBox.copy(alpha = 0.08f))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = goalLabelText,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = colorBox
                                )

                                if (hasAchieved) {
                                    Text(
                                        text = "Goal Achieved! 🎉",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = colorBox
                                    )
                                    Text(
                                        text = "Incredible job! You've successfully hit your target BMI of %.1f. Keep maintaining your spectacular healthy habits!",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                } else {
                                    val actionWord = if (isLossGoal) "lose" else "gain"
                                    val formattedWeight = "%.1f kg".format(absWeightDiffKg)
                                    val formattedBmiDiff = "%.1f".format(absDiff)

                                    Text(
                                        text = "You need to $actionWord $formattedWeight",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "You are $formattedBmiDiff BMI points away from your healthy target of %.1f. Steady progress wins the journey!".format(target),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Goal Progress",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (hasAchieved) "100%" else "${(progressFraction * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = colorBox
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(if (hasAchieved) 1f else progressFraction)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(colorBox.copy(alpha = 0.7f), colorBox)
                                            )
                                        )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Current: %.1f".format(current),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Target: %.1f".format(target),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        TextButton(
                            onClick = { viewModel.setTargetBmi(null) },
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("clear_goal_button")
                        ) {
                            Text(
                                text = "Clear Goal",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(viewModel: BmiViewModel) {
    val records by viewModel.historyRecords.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History Log",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (records.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearAllLogs() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Logs",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.exportToPdf(context) { file ->
                                if (file != null) {
                                    Toast.makeText(context, "PDF saved to: ${file.name}", Toast.LENGTH_LONG).show()
                                    viewModel.sharePdf(context, file)
                                } else {
                                    Toast.makeText(context, "Failed to generate PDF.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.testTag("export_pdf_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export Icon", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export PDF", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Empty Log",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your log book is empty",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Calculate and save your BMI to view history",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    BmiLineChart(records = records)
                }
                item {
                    Text(
                        text = "Individual Logs",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                itemsIndexed(records) { index, record ->
                    HistoryItemCard(index = index, record = record, onDelete = { viewModel.deleteRecord(record.id) })
                }
            }
        }
    }
}

@Composable
fun BmiLineChart(records: List<BmiRecord>) {
    val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
    val recentRecords = remember(records) {
        records.filter { it.timestamp >= thirtyDaysAgo }.sortedBy { it.timestamp }
    }

    if (recentRecords.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(recentRecords) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(1.dp, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "30-DAY PROGRESS TREND",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Smooth BMI Trajectory",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (recentRecords.size >= 2) {
                    val firstBmi = recentRecords.first().bmi
                    val lastBmi = recentRecords.last().bmi
                    val diff = lastBmi - firstBmi
                    val trendText = if (diff >= 0) "+%.1f".format(diff) else "%.1f".format(diff)
                    val trendColor = if (diff >= 0) Color(0xFFD97706) else Color(0xFF059669)

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(trendColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = trendText,
                            color = trendColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val bmis = recentRecords.map { it.bmi }
                val maxBmiValue = bmis.maxOrNull() ?: 25f
                val minBmiValue = bmis.minOrNull() ?: 20f

                val spread = (maxBmiValue - minBmiValue).coerceAtLeast(4f)
                val pad = spread * 0.15f
                val yMin = minBmiValue - pad
                val yMax = maxBmiValue + pad
                val yRange = yMax - yMin

                val firstTime = recentRecords.firstOrNull()?.timestamp ?: thirtyDaysAgo
                val lastTime = recentRecords.lastOrNull()?.timestamp ?: System.currentTimeMillis()
                val xRange = (lastTime - firstTime).coerceAtLeast(1L)

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val leftPadding = 38.dp.toPx()
                    val rightPadding = 12.dp.toPx()
                    val topPadding = 12.dp.toPx()
                    val bottomPadding = 24.dp.toPx()

                    val chartWidth = size.width - leftPadding - rightPadding
                    val chartHeight = size.height - topPadding - bottomPadding

                    val horizontalTicks = 4
                    for (i in 0 until horizontalTicks) {
                        val fraction = i.toFloat() / (horizontalTicks - 1)
                        val gridY = topPadding + chartHeight * (1f - fraction)
                        val bmiTick = yMin + fraction * yRange

                        drawLine(
                            color = gridColor,
                            start = Offset(leftPadding, gridY),
                            end = Offset(leftPadding + chartWidth, gridY),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        drawText(
                            textMeasurer = textMeasurer,
                            text = "%.1f".format(bmiTick),
                            style = TextStyle(
                                color = textColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            topLeft = Offset(4.dp.toPx(), gridY - 8.dp.toPx())
                        )
                    }

                    val points = recentRecords.map { record ->
                        val ratioX = if (recentRecords.size <= 1) 0.5f else (record.timestamp - firstTime).toFloat() / xRange
                        val ratioY = (record.bmi - yMin) / yRange
                        Offset(
                            x = leftPadding + ratioX * chartWidth,
                            y = topPadding + (1f - ratioY) * chartHeight
                        )
                    }

                    if (points.isNotEmpty()) {
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = leftPadding + chartWidth * animationProgress.value,
                            bottom = size.height
                        ) {
                            if (points.size > 1) {
                                val areaPath = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 0 until points.size - 1) {
                                        val p1 = points[i]
                                        val p2 = points[i + 1]
                                        cubicTo(
                                            p1.x + (p2.x - p1.x) / 3f, p1.y,
                                            p1.x + 2f * (p2.x - p1.x) / 3f, p2.y,
                                            p2.x, p2.y
                                        )
                                    }
                                    lineTo(points.last().x, topPadding + chartHeight)
                                    lineTo(points.first().x, topPadding + chartHeight)
                                    close()
                                }

                                drawPath(
                                    path = areaPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.25f),
                                            primaryColor.copy(alpha = 0.0f)
                                        ),
                                        startY = topPadding,
                                        endY = topPadding + chartHeight
                                    )
                                )
                            }

                            if (points.size > 1) {
                                val splinePath = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 0 until points.size - 1) {
                                        val p1 = points[i]
                                        val p2 = points[i + 1]
                                        cubicTo(
                                            p1.x + (p2.x - p1.x) / 3f, p1.y,
                                            p1.x + 2f * (p2.x - p1.x) / 3f, p2.y,
                                            p2.x, p2.y
                                        )
                                    }
                                }

                                drawPath(
                                    path = splinePath,
                                    color = primaryColor,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        miter = 1f,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                )
                            } else {
                                drawCircle(
                                    color = primaryColor,
                                    radius = 6.dp.toPx(),
                                    center = points.first()
                                )
                            }

                            points.forEach { pt ->
                                drawCircle(
                                    color = Color.White,
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = primaryColor,
                                    radius = 3.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    if (recentRecords.isNotEmpty()) {
                        val sdfDay = SimpleDateFormat("dd MMM", Locale.getDefault())
                        val totalXLabels = if (recentRecords.size > 3) 3 else recentRecords.size
                        for (i in 0 until totalXLabels) {
                            val fraction = if (totalXLabels <= 1) 0.5f else i.toFloat() / (totalXLabels - 1)
                            val idx = ((recentRecords.size - 1) * fraction).toInt()
                            val record = recentRecords[idx]
                            val labelX = leftPadding + fraction * chartWidth
                            val dateLabel = sdfDay.format(Date(record.timestamp))

                            drawText(
                                textMeasurer = textMeasurer,
                                text = dateLabel,
                                style = TextStyle(
                                    color = textColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                topLeft = Offset(
                                    x = (labelX - 18.dp.toPx()).coerceAtLeast(leftPadding),
                                    y = topPadding + chartHeight + 6.dp.toPx()
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(index: Int, record: BmiRecord, onDelete: () -> Unit) {
    val categoryColor = when (record.category) {
        "Underweight" -> Color(0xFF2563EB)  // Blue
        "Normal" -> Color(0xFF059669)       // Teal
        "Overweight" -> Color(0xFFD97706)   // Amber/Orange
        else -> Color(0xFFDC2626)            // Crimson Red
    }

    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffsetY = remember { Animatable(30f) }

    LaunchedEffect(key1 = record.id) {
        delay(index * 60L) // Staggered delay
        launch {
            animatedAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
        launch {
            animatedOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
    }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(record.timestamp))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                alpha = animatedAlpha.value,
                translationY = animatedOffsetY.value
            )
            .shadow(1.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "%.1f".format(record.bmi),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = categoryColor
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = record.category.uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp,
                            color = categoryColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Weight: ${record.weightKg} kg   |   Height: ${record.heightCm} cm",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                if (record.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "📝 ${record.notes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Record",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AiAdviceTab(viewModel: BmiViewModel) {
    val adviceState by viewModel.adviceState.collectAsStateWithLifecycle()
    val activity by viewModel.activityLevel.collectAsStateWithLifecycle()
    val goal by viewModel.fitnessGoal.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Clinical AI Insights",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tailor your assessment with your physical attributes, activity parameters, and target milestones for advanced dietary logic.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        item {
            // Setup parameters
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Activity selection
                    Column {
                        Text(
                            text = "Daily Activity Level",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val activities = listOf("Sedentary", "Lightly Active", "Moderately Active", "Highly Active")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            activities.forEach { act ->
                                val isSelected = act == activity
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { viewModel.setActivity(act) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = act.split(" ").first(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Fitness Goal
                    Column {
                        Text(
                            text = "Primary Wellness Goal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val goals = listOf("Lose Weight", "Maintain Weight", "Build Muscle")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            goals.forEach { g ->
                                val isSelected = g == goal
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { viewModel.setGoal(g) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = g,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.generateAdvice() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("generate_advice_button")
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "AI Advice Icon")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Get Personalized AI Advice")
                    }
                }
            }
        }

        item {
            // Render Advice responses beautifully
            when (adviceState) {
                is AdviceUiState.Idle -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Personalized Insights Await",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Tap the advice button to invoke Gemini models. It compiles specialized daily calories, diet tips, and lifestyle habits tailored exclusively to you.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                is AdviceUiState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Consulting Gemini Clinical Dietitian...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is AdviceUiState.Success -> {
                    val rawText = (adviceState as AdviceUiState.Success).advice
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Your Personalized Fitness Blueprint",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Format the markdown content simply
                            Text(
                                text = formatMarkdown(rawText),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                is AdviceUiState.Error -> {
                    val errMsg = (adviceState as AdviceUiState.Error).message
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Error generating advice",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errMsg,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WearableSyncTab(viewModel: BmiViewModel) {
    val context = LocalContext.current
    val liveSteps by viewModel.liveSteps.collectAsStateWithLifecycle()
    val isTrackingSteps by viewModel.isTrackingSteps.collectAsStateWithLifecycle()

    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
    val connectedDevice by viewModel.connectedWearable.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Wearable Health Devices",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sync BMI weight inputs dynamically from your fitness wearable. Real-time steps are pulled directly from mobile internal motion sensors.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        item {
            // Live Step Counter Widget
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Real-Time Mobile Step Counter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Large circular pedometer gauge
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Running Steps",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "$liveSteps",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "steps today",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleStepTracking() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTrackingSteps) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("toggle_steps_button")
                    ) {
                        Icon(
                            imageVector = if (isTrackingSteps) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = "Pedometer action icon"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isTrackingSteps) "Pause Mobile Step Tracker" else "Activate Mobile Step Tracker")
                    }
                }
            }
        }

        item {
            // Synced Wearable Device Control
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Choose Wearable Source to Sync",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val wearableOptions = listOf("Fitbit Sense 2", "Garmin Fenix 7", "Apple Health", "Wear OS")
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        wearableOptions.forEach { brand ->
                            val isSelected = brand == connectedDevice
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                    )
                                    .clickable { viewModel.syncWithWearable(brand) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = brand,
                                    tint = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = brand,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Log / Message outputs
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sync Telemetry Log:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Column {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.CenterHorizontally),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Text(
                                text = syncMessage,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (syncMessage.contains("Success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Simple Helper to format markdown tags out for neat display
private fun formatMarkdown(text: String): String {
    return text
        .replace("###", "")
        .replace("##", "")
        .replace("**", "")
        .replace("* ", " • ")
        .trim()
}
