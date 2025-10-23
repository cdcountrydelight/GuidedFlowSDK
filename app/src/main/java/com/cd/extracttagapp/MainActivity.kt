package com.cd.extracttagapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cd.extracttagapp.screens.HomeScreen
import com.cd.extracttagapp.screens.SimpleElementsScreen
import com.cd.extracttagapp.screens.UIElementsScreen
import com.cd.extracttagapp.ui.theme.ExtractTagAppTheme
import com.cd.uielementmanager.UIElementTrackingSDK
import com.cd.uielementmanager.presentation.composables.UIElementProvider
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val viewModel by inject<UIElementViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ExtractTagAppTheme {
                AppNavigation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check if user granted overlay permission
        if (requestCode == 1234) { // OVERLAY_PERMISSION_REQUEST_CODE
            if (android.provider.Settings.canDrawOverlays(this)) {
                // Permission granted, try to start service
                UIElementTrackingSDK.startService(
                    this,
                    viewModel,
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDIxMiwiZXhwIjoxNzYxMjc5MzIwLCJpYXQiOjE3NjExOTI5MjB9.voC-WWGATWkIvSSjC_5YF9F7MdXdsXT0Xzt9PazJ5oY"
                )
            } else {
                Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Get the singleton UIElementViewModel instance from Koin
    val sharedElementTracker: UIElementViewModel = koinInject()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                elementTracker = sharedElementTracker,
                onNavigateToTestScreen = {
                    navController.navigate("ui_elements/false")
                },
                onNavigateToTestOverlay = {
                    navController.navigate("ui_elements/true")
                }
            )
        }

        composable("ui_elements/{enableOverlay}") { backStackEntry ->
            val enableOverlay =
                backStackEntry.arguments?.getString("enableOverlay")?.toBoolean() ?: false
            UIElementProvider(
                screenName = "ui_elements_screen",
                uIElementViewModel = sharedElementTracker,
                enableTrainingFlow = enableOverlay
            ) {
                UIElementsScreen(
                    elementTracker = sharedElementTracker,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToSimpleElements = {
                        navController.navigate("simple_elements")
                    }
                )
            }
        }

        composable("simple_elements") {
            UIElementProvider(
                screenName = "simple_elements_screen",
                uIElementViewModel = sharedElementTracker
            ) {
                SimpleElementsScreen(
                    elementTracker = sharedElementTracker,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}