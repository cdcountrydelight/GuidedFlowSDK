package com.cd.uielementmanager.presentation.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.utils.CompletedTrainingScreenDestination
import com.cd.uielementmanager.presentation.utils.FlowListScreenDestination
import com.cd.uielementmanager.presentation.utils.QnAScreenDestination

@Composable
fun FlowListNavHost(
    appName: String,
    packageName: String,
    authToken: String,
    onClosed: () -> Unit,
) {
    val navController = rememberNavController()
    val viewModel: UIElementViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = FlowListScreenDestination,
    ) {

        composable<FlowListScreenDestination> {
            FlowListScreen(
                authToken = authToken,
                appName = appName,
                packageName = packageName,
                viewModel = viewModel,
                onFlowSelected = {
                    navController.navigate(QnAScreenDestination())
                },
                onBackClicked = onClosed
            )
        }
        composable<QnAScreenDestination> {
            QnAScreen(
                viewModel = viewModel,
                onNavigateToCompleteTraining = {
                    navController.navigate(CompletedTrainingScreenDestination(it ?: 0.0)) {
                        popUpTo(FlowListScreenDestination) { inclusive = true }
                    }
                },
                onBackRequested = {
                    navController.popBackStack()
                }
            )
        }

        composable<CompletedTrainingScreenDestination> { backStack ->
            val calculatedScore =
                backStack.toRoute<CompletedTrainingScreenDestination>().calculatedScore
            CompletedTrainingScreen(
                viewModel = viewModel,
                calculatedScore = calculatedScore ?: 0.0,
                onStartNextFlow = {
                    navController.popBackStack()
                    navController.navigate(FlowListScreenDestination)
                    //navController.popBackStack(FlowListScreenDestination, false)
                },
                onBackButton = {
                        navController.popBackStack()
                        navController.navigate(FlowListScreenDestination)
                },
                onGoToHome = {
                    navController.popBackStack(FlowListScreenDestination, false)
                }


            )
        }
    }
}