package com.cd.extracttagapp

import android.os.Bundle
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
import com.cd.uielementmanager.presentation.composables.UIElementProvider
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val viewModel by inject<UIElementViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExtractTagAppTheme {
                AppNavigation(viewModel)
            }
        }
    }

}

@Composable
fun AppNavigation(viewModel: UIElementViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                elementTracker = viewModel,
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
                uIElementViewModel = viewModel,
                enableTrainingFlow = enableOverlay
            ) {
                UIElementsScreen(
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
                uIElementViewModel = viewModel
            ) {
                SimpleElementsScreen(
                    elementTracker = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}