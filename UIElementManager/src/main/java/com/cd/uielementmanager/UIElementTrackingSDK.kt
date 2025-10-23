package com.cd.uielementmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.presentation.StartMode
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.utils.FunctionHelper.showToast
import com.cd.uielementmanager.presentation.utils.ViewModelHelper
import com.cd.uielementmanager.service.UIElementTrackingService

/**
 * Main entry point for the UI Element Tracking SDK
 * Provides simple API to start/stop tracking UI elements across the app
 */
object UIElementTrackingSDK {

    private const val OVERLAY_PERMISSION_REQUEST_CODE = 1234

    /**
     * Start tracking UI elements
     * Automatically checks for overlay permission and requests it if needed
     *
     * @param activity The activity to use for permission request
     */
    fun startService(
        activity: Activity,
        viewModel: UIElementViewModel,
        authToken: String,
        startMode: StartMode,
        packageName: String? = null
    ) {
        if (!Settings.canDrawOverlays(activity)) {
            requestOverlayPermission(activity)
        }
        if (isSDKRunning()) return
        try {
            ViewModelHelper.viewModel = viewModel
            HttpClientManager.authToken = authToken
            val intent = Intent(activity, UIElementTrackingService::class.java)
            intent.putExtra("packageName", packageName ?: activity.packageName)
            intent.putExtra(
                "showOverLay",
                startMode == StartMode.Sender || startMode == StartMode.Both
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent)
            } else {
                activity.startService(intent)
            }
        } catch (e: Exception) {
            activity.showToast("Failed to start tracking service ${e.localizedMessage}")
        }
    }

    /**
     * Stop tracking UI elements
     */
    fun stopService(context: Context) {
        context.stopService(Intent(context, UIElementTrackingService::class.java))
    }

    /**
     * Check if tracking service is currently running
     */
    fun isSDKRunning(): Boolean {
        return UIElementTrackingService.isRunning()
    }

    /**
     * Request overlay permission
     * Opens system settings for the user to grant permission
     */
    private fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${activity.packageName}".toUri()
        )
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
}