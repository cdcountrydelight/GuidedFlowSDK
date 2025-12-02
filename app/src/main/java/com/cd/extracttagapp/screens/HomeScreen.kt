package com.cd.extracttagapp.screens

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cd.uielementmanager.presentation.UIElementTrackingSDK
import com.cd.uielementmanager.presentation.composables.UIElementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    elementTracker: UIElementViewModel,
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    onNavigateToTestScreen: () -> Unit,
    onNavigateToTestOverlay: () -> Unit = {},
    onNavigateToQuizScreen: () -> Unit
) {

    val context = LocalContext.current
    val activity = context as? Activity ?: return

    val authToken = remember {
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDIxMiwiZXhwIjoxNzY0Njc2Mjc1LCJpYXQiOjE3NjQ1ODk4NzV9.uhFhGc4P_Up_SX-DAyipBfRu1MJZW9L3C8vtfwLBsHw"
    }

    val isProdEnv = remember {
        false
    }

    val packageName = remember {
        "deliveryapp.countrydelight.in.deliveryapp"
    }

    val projectionManager = remember {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    val mediaProjectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            UIElementTrackingSDK.startSenderSDK(
                activity,
                elementTracker,
                isProdEnv,
                result.resultCode,
                result.data,
                packageName,
            )
            homeScreenViewModel.isSenderSDKStarted = true
        } else {
            Toast.makeText(context, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "UI Element Extractor",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Button(
                    onClick = {
                        if (homeScreenViewModel.isSenderSDKStarted) {
                            UIElementTrackingSDK.stopSenderSDK(context)
                            homeScreenViewModel.isSenderSDKStarted = false
                        } else {
                            val captureIntent = projectionManager.createScreenCaptureIntent()
                            mediaProjectionLauncher.launch(captureIntent)
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("${if (homeScreenViewModel.isSenderSDKStarted) "Stop" else "Start"} Sender SDK")
                }

                Button(
                    onClick = {
                        UIElementTrackingSDK.startTrainingSDK(
                            activity,
                            elementTracker,
                            authToken,
                            isProdEnv,
                            packageName
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Start Training SDK")
                }

                Button(
                    onClick = onNavigateToTestScreen,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Navigate to Test Screen",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Button(
                    onClick = onNavigateToTestOverlay,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Test Overlay Functionality",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Button(
                    onClick = onNavigateToQuizScreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Quiz Section",
                        style =MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}