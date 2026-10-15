package com.example.breaktime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breaktime.ui.BreakTimeApp
import com.example.breaktime.ui.theme.BreakTimeTheme
import com.example.breaktime.ui.viewmodel.BreakTimeViewModel
import com.example.breaktime.ui.viewmodel.BreakTimeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BreakTimeViewModel = viewModel(
                factory = BreakTimeViewModelFactory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsState()

            BreakTimeTheme(
                appTheme = uiState.settings.theme,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                BreakTimeApp(viewModel = viewModel)
            }
        }
    }
}