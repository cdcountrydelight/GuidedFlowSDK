package com.cd.extracttagapp

import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.cd.extracttagapp.ui.theme.ExtractTagAppTheme
import com.cd.uielementmanager.presentation.UIElementTrackingSDK
import com.cd.uielementmanager.presentation.screens.FlowListNavHost
import com.cd.uielementmanager.presentation.viewmodels.UIElementViewModel
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val uiElementVIewModel by inject<UIElementViewModel>()

    private lateinit var projectionManager: MediaProjectionManager

    private val mediaProjectionLauncher = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            UIElementTrackingSDK.startSenderSDK(
                this,
                uiElementVIewModel,
                false,
                result.resultCode,
                result.data,
                packageName,
                true
            )
        } else {
            Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        requestMediaProjectionPermission()
        setContent {
            ExtractTagAppTheme {
                FlowListNavHost(
                    "CD Partner",
                    "deliveryapp.countrydelight.in.deliveryapp",
                    authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDIxMiwiZXhwIjoxNzY1MzQ4NDEzLCJpYXQiOjE3NjUyNjIwMTN9.lXbAugDJnivCHpTaKV7GWLOqfSjM3dt3Ark4iBBShxM",
                    onClosed = { finish() },
                    isProdEnv = false
                )
            }
        }
    }

    private fun requestMediaProjectionPermission() {
        val intent = projectionManager.createScreenCaptureIntent()
        mediaProjectionLauncher.launch(intent)
    }
}
