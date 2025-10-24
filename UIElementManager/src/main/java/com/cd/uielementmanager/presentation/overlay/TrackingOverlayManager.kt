package com.cd.uielementmanager.presentation.overlay

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.cd.uielementmanager.domain.contents.UIElementContent
import com.cd.uielementmanager.presentation.ScreenshotHelper
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Manages the overlay window for UI element tracking FAB
 */
internal class TrackingOverlayManager(
    private val context: Context,
    private val uiElementViewModel: UIElementViewModel
) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: ViewBasedTrackingOverlay? = null
    private var overlayParams: WindowManager.LayoutParams? = null
    private var isOverlayShown = false
    private lateinit var packageName: String
    private var capturedElements: Map<String, UIElementContent> = emptyMap()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var screenShotHelper: ScreenshotHelper? = null

    /**
     * Show the tracking overlay
     */
    fun showOverlay() {
        if (isOverlayShown) {
            return
        }
        try {
            val trackingOverlay = ViewBasedTrackingOverlay(
                context = context,
                onSendClicked = {
                    captureRootViewAndSendToServer()
                },
            )
            trackingOverlay.setOnPositionChanged { x, y ->
                updateOverlayPosition(x, y)
            }
            observeTrackedElements(trackingOverlay)
            observeUploadStatus(trackingOverlay)
            val params = WindowManager.LayoutParams().apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.TOP or Gravity.START

                // Calculate FAB size and margins
                val density = context.resources.displayMetrics.density
                val fabSize = (56 * density).toInt() // FAB size
                val horizontalPadding =
                    (2 * density).toInt() // Horizontal padding added in ViewBasedTrackingOverlay
                val totalSize =
                    fabSize + (horizontalPadding * 2) // Total size including horizontal padding
                val margin = (16 * density).toInt() // Margin from screen edge

                // Position at bottom-right with proper offsets
                x = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    margin
                } else {
                    context.resources.displayMetrics.widthPixels - totalSize - margin - (100 * density).toInt()
                }
                y = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    windowManager.currentWindowMetrics.bounds.height() - totalSize - margin
                } else {
                    context.resources.displayMetrics.heightPixels - totalSize - margin
                }
            }
            trackingOverlay.windowParams = params
            windowManager.addView(trackingOverlay, params)
            overlayView = trackingOverlay
            overlayParams = params
            isOverlayShown = true
        } catch (e: Exception) {
            e.printStackTrace()
            isOverlayShown = false
            context.showToast("Error showing overlay ${e.localizedMessage}")
        }
    }

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    fun setMediaProjectionData(resultCode: Int, resultData: Intent) {
        screenShotHelper = ScreenshotHelper(context, resultCode, resultData)
        screenShotHelper?.initMediaProjection()
    }

    fun releaseMediaProjection() {
        screenShotHelper?.releaseVirtualDisplay()
    }


    /**
     * Hide the tracking overlay
     */
    fun hideOverlay() {
        if (!isOverlayShown) return
        try {
            overlayView?.let {
                windowManager.removeView(it)
                overlayView = null
                isOverlayShown = false
            }
        } catch (e: Exception) {
            context.showToast("Error while hiding overlay ${e.localizedMessage}")
        }
    }

    /**
     * Observe tracked elements and update overlay
     */
    private fun observeTrackedElements(overlay: ViewBasedTrackingOverlay) {
        coroutineScope.launch {
            uiElementViewModel.trackedElements.collectLatest { screenMap ->
                // Get elements for the current screen
                val currentScreenElements = uiElementViewModel.getCurrentScreen()?.let { screen ->
                    screenMap[screen] ?: emptyMap()
                } ?: emptyMap()
                capturedElements = currentScreenElements
                overlay.updateElementCount(currentScreenElements.size)
            }
        }
    }

    /**
     * Observe upload status and update FAB states accordingly
     */
    private fun observeUploadStatus(overlay: ViewBasedTrackingOverlay) {
        coroutineScope.launch {
            uiElementViewModel.sendUiElementsStateFlow.collectLatest { status ->
                when (status) {
                    is DataUiResponseStatus.Loading -> {
                        overlay.showLoading()
                    }

                    is DataUiResponseStatus.Success -> {
                        overlay.showSuccess()
                        context.showToast("Upload successful!")
                        delay(3000)
                        overlay.resetToNormalState()
                    }

                    is DataUiResponseStatus.Failure -> {
                        overlay.showError()
                        context.showToast("Upload failed: ${status.errorMessage}")
                        delay(3000)
                        overlay.resetToNormalState()
                    }

                    else -> {
                        // DataUiResponseStatus.None - keep current state
                    }
                }
            }
        }
    }

    /**
     * Temporarily hide overlay during screenshot capture to prevent it from appearing in the image
     */
    fun temporarilyHideOverlay(callback: suspend () -> Unit) {
        coroutineScope.launch {
            try {
                overlayView?.visibility = View.GONE
                callback()
            } catch (e: Exception) {
                context.showToast("Error capturing screenshot: ${e.localizedMessage}")
            } finally {
                overlayView?.visibility = View.VISIBLE
            }
        }
    }


    private fun updateOverlayPosition(x: Int, y: Int) {
        overlayParams?.let { params ->
            overlayView?.let { view ->
                // Get screen dimensions
                val screenWidth: Int
                val screenHeight: Int

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics = windowManager.currentWindowMetrics
                    val bounds = windowMetrics.bounds
                    screenWidth = bounds.width()
                    screenHeight = bounds.height()
                } else {
                    val displayMetrics = context.resources.displayMetrics
                    screenWidth = displayMetrics.widthPixels
                    screenHeight = displayMetrics.heightPixels
                }

                // Calculate FAB dimensions (approximate)
                val fabSize = (56 * context.resources.displayMetrics.density).toInt()
                val margin = (16 * context.resources.displayMetrics.density).toInt()
                val textSize = (100 * context.resources.displayMetrics.density).toInt()
                // Calculate boundaries
                val maxX = screenWidth - fabSize - margin - textSize
                val maxY = screenHeight - fabSize - margin

                // Constrain position within bounds
                params.x = x.coerceIn(margin, maxX)
                params.y = y.coerceIn(margin, maxY)

                // Update the params reference in the overlay view
                view.windowParams = params

                windowManager.updateViewLayout(view, params)
            }
        }
    }

    /**
     * Capture screenshot without overlay and show preview with send option
     */
    private fun captureRootViewAndSendToServer() {
        temporarilyHideOverlay {
            if (screenShotHelper == null) {
                context.showToast("Permission Denied , Please Grant Permission Again")
            } else {
                screenShotHelper?.captureScreenshot {
                    uiElementViewModel.sendUIElements(context, it, packageName)
                }
            }
        }
    }
}