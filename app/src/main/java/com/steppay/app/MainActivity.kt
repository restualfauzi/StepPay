package com.steppay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.navigation.StepPayNavHost
import com.steppay.app.ui.theme.StepPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepPayTheme {
                val navController = rememberNavController()
                val viewModel: StepPayViewModel = viewModel(factory = StepPayViewModel.Factory)

                StepPayNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}