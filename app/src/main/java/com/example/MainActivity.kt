package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.AppNavigation
import com.example.ui.theme.SwastikGoldTheme
import com.example.viewmodel.MarketViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: MarketViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SwastikGoldTheme {
        AppNavigation(viewModel)
      }
    }
  }
}
