package com.cd.uielementmanager.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cd.uielementmanager.presentation.utils.CompletedTrainingScreenDestination
import com.cd.uielementmanager.presentation.utils.FlowListScreenDestination
import com.cd.uielementmanager.presentation.utils.QnAScreenDestination
import com.cd.uielementmanager.presentation.viewmodels.QuizViewModel

@Composable
fun FlowListNavHost(
    appName: String,
    packageName: String,
    authToken: String,
    viewModel: QuizViewModel = viewModel(),
    isProdEnv: Boolean,
    onClosed: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = FlowListScreenDestination,
    ) {
        composable<FlowListScreenDestination> {
            FlowListScreen(
                appName = appName,
                packageName = packageName,
                viewModel = viewModel,
                onFlowSelected = {
                    navController.navigate(QnAScreenDestination)
                },
                onBackClicked = onClosed
            )
        }
        composable<QnAScreenDestination> {
            QnAScreen(
                viewModel = viewModel,
                onNavigateToCompleteTraining = {
                    navController.navigate(CompletedTrainingScreenDestination(it ?: 0.0)) {
                        popUpTo(FlowListScreenDestination) { inclusive = false }
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
                },
                onBackButton = {
                    navController.popBackStack()
                },
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initData(authToken, isProdEnv)
    }
}