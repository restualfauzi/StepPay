package com.steppay.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
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

                // Launcher to request all required runtime permissions at app startup
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                ) { /* Permissions result handled gracefully by app services */ }

                LaunchedEffect(Unit) {
                    val permissionsToRequest = buildList {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            add(Manifest.permission.ACTIVITY_RECOGNITION)
                        }
                        add(Manifest.permission.ACCESS_FINE_LOCATION)
                        add(Manifest.permission.ACCESS_COARSE_LOCATION)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }.toTypedArray()

                    permissionLauncher.launch(permissionsToRequest)
                }

                StepPayNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}