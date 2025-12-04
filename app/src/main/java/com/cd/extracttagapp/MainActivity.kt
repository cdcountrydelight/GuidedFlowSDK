package com.cd.extracttagapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cd.extracttagapp.ui.theme.ExtractTagAppTheme
import com.cd.uielementmanager.presentation.screens.FlowListNavHost
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExtractTagAppTheme {
                FlowListNavHost(
                    "CD Partner",
                    "deliveryapp.countrydelight.in.deliveryapp",
                    authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDIxMiwiZXhwIjoxNzY0OTI5MzkyLCJpYXQiOjE3NjQ4NDI5OTJ9.wbUGjntmq5jBk05y4S28_joM60sg85SNHZgJt35vtYk",
                    onClosed = { finish() },
                    isProdEnv = false
                )
            }
        }
    }
}
