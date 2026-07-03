package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.ui.BmiAppScreen
import com.example.ui.BmiViewModel
import com.example.ui.BmiViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Create view model using factory
    val viewModel = ViewModelProvider(this, BmiViewModelFactory(application))[BmiViewModel::class.java]
    
    setContent {
      val themeMode by viewModel.themeMode.collectAsState()
      val darkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
      }
      MyApplicationTheme(darkTheme = darkTheme) {
        BmiAppScreen(viewModel = viewModel)
      }
    }
  }
}
