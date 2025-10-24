package com.cd.uielementmanager.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.service.UIElementTrackingService
import com.cd.uielementmanager.presentation.utils.FunctionHelper.showToast
import com.cd.uielementmanager.presentation.utils.ViewModelHelper

object UIElementTrackingSDK {

    private const val OVERLAY_PERMISSION_REQUEST_CODE = 1234


    private fun initializeSDK(
        activity: Activity,
        viewModel: UIElementViewModel,
        authToken: String,
        isProdEnvironment: Boolean,
        onSDKInitialized: () -> Unit
    ) {
        if (!Settings.canDrawOverlays(activity)) {
            requestOverlayPermission(activity)
        }
        if (isSDKRunning()) return
        try {
            ViewModelHelper.viewModel = viewModel
            HttpClientManager.initializeDetails(authToken, isProdEnvironment)
            onSDKInitialized()
        } catch (e: Exception) {
            activity.showToast("Failed to start tracking service ${e.localizedMessage}")
        }
    }

    fun startSenderSDK(
        activity: Activity,
        viewModel: UIElementViewModel,
        authToken: String,
        isProdEnvironment: Boolean,
        resultCode: Int,
        resultData: Intent?,
        packageName: String? = null
    ) {
        initializeSDK(activity, viewModel, authToken, isProdEnvironment) {
            val intent = Intent(activity, UIElementTrackingService::class.java)
            intent.putExtra("packageName", packageName ?: activity.packageName)
            intent.putExtra("resultCode", resultCode)
            intent.putExtra("data", resultData)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent)
            } else {
                activity.startService(intent)
            }
        }
    }

    fun startTrainingSDK(
        activity: Activity,
        viewModel: UIElementViewModel,
        authToken: String,
        isProdEnvironment: Boolean,
        packageName: String? = null
    ) {
        initializeSDK(activity, viewModel, authToken, isProdEnvironment) {
            viewModel.fetchTrainingFlow(activity, packageName ?: activity.packageName)
            if (!isProdEnvironment) {
                activity.showToast("Training SDK Started")
            }
        }
    }

    fun stopService(context: Context) {
        context.stopService(Intent(context, UIElementTrackingService::class.java))
    }

    private fun isSDKRunning(): Boolean {
        return UIElementTrackingService.Companion.isRunning()
    }

    private fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${activity.packageName}".toUri()
        )
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
}