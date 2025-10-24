package com.cd.extracttagapp.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cd.uielementmanager.UIElementTrackingSDK
import com.cd.uielementmanager.presentation.StartMode
import com.cd.uielementmanager.presentation.composables.UIElementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    elementTracker: UIElementViewModel,
    onNavigateToTestScreen: () -> Unit,
    onNavigateToTestOverlay: () -> Unit = {}
) {
    val context = LocalContext.current
    var isServiceRunning by remember { mutableStateOf(UIElementTrackingSDK.isSDKRunning()) }

    // Clear any previous tracking data when entering home screen
    LaunchedEffect(Unit) {
        elementTracker.clearTrackedElements()
        // Check service status on screen load
        isServiceRunning = UIElementTrackingSDK.isSDKRunning()
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
                        if (isServiceRunning) {
                            // Stop the service
                            UIElementTrackingSDK.stopService(context)
                            Toast.makeText(context, "UI tracking stopped", Toast.LENGTH_SHORT)
                                .show()
                            isServiceRunning = false
                        } else {
                            val activity = context as? Activity
                            if (activity != null) {
                                // Start service (will automatically check and request overlay permission if needed)
                                UIElementTrackingSDK.startService(
                                    activity,
                                    elementTracker,
                                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDIxMiwiZXhwIjoxNzYxMzY1OTgxLCJpYXQiOjE3NjEyNzk1ODF9.2jurE3TB9sPGJ8-n4IxWRBDNiJr3VXkQg9Uz6-Y01es",
                                    StartMode.Both,
                                    "deliveryapp.countrydelight.in.deliveryapp",
                                )
                                // Update the state after a short delay to check if service started
                                isServiceRunning = UIElementTrackingSDK.isSDKRunning()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Unable to start service",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (isServiceRunning) "Stop UI Element Extraction" else "Start UI Element Extraction",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
            }
        }
    }
}