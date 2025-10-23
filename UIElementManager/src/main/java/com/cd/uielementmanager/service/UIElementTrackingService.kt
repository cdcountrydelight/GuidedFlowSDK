package com.cd.uielementmanager.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.cd.uielementmanager.presentation.overlay.TrackingOverlayManager
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.ViewModelHelper.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Foreground service that manages UI element tracking overlay
 */
class UIElementTrackingService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1002
        private const val CHANNEL_ID = "ui_element_tracking_channel"
        private const val ACTION_STOP_SERVICE = "com.cd.uielementmanager.STOP_TRACKING"

        /**
         * Check if the service is currently running
         */
        internal fun isRunning(): Boolean {
            return instance != null
        }

        private var instance: UIElementTrackingService? = null
    }

    private lateinit var overlayManager: TrackingOverlayManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        instance = this
        overlayManager = TrackingOverlayManager(this, viewModel)
        createNotificationChannel()
        observeUploadStatus()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopSelf()
                return START_NOT_STICKY
            }

            else -> {
                val packageName = intent?.getStringExtra("packageName") ?: this.packageName
                val showOverlay = intent?.getBooleanExtra("showOverLay", true) ?: true
                viewModel.fetchTrainingFlow(this, packageName)
                instance?.overlayManager?.setPackageName("deliveryapp.countrydelight.in.deliveryapp")
                startForeground(NOTIFICATION_ID, createNotification())
                if (showOverlay) {
                    overlayManager.showOverlay()
                }
            }
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent) = null

    override fun onDestroy() {
        overlayManager.hideOverlay()
        serviceScope.cancel()
        instance = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "UI Element Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when UI element tracking is active"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun observeUploadStatus() {
        serviceScope.launch {
            viewModel.sendUiElementsStateFlow.collectLatest { status ->
                when (status) {
                    is DataUiResponseStatus.Loading -> {
                        updateNotification(createLoadingNotification())
                    }

                    is DataUiResponseStatus.Success -> {
                        updateNotification(createSuccessNotification())
                        // Revert to default after 5 seconds
                        delay(5000)
                        updateNotification(createNotification())
                    }

                    is DataUiResponseStatus.Failure -> {
                        updateNotification(
                            createErrorNotification(
                                status.errorMessage,
                                status.errorCode
                            )
                        )
                        // Revert to default after 5 seconds
                        delay(5000)
                        updateNotification(createNotification())
                    }

                    is DataUiResponseStatus.None -> {
                        updateNotification(createNotification())
                    }
                }
            }
        }
    }

    private fun updateNotification(notification: Notification) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createLoadingNotification(): Notification {
        val stopIntent = Intent(this, UIElementTrackingService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("UI Element Tracking Active")
            .setContentText("Uploading tracked elements...")
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setProgress(0, 0, true) // Indeterminate progress bar
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                stopPendingIntent
            )
            .build()
    }

    private fun createSuccessNotification(): Notification {
        val stopIntent = Intent(this, UIElementTrackingService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("UI Element Tracking Active")
            .setContentText("UI Elements uploaded successfully")
            .setSmallIcon(android.R.drawable.ic_menu_upload_you_tube)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                stopPendingIntent
            )
            .build()
    }

    private fun createErrorNotification(errorMessage: String, errorCode: Int): Notification {
        val stopIntent = Intent(this, UIElementTrackingService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val fullErrorText = "Upload failed (Code: $errorCode): $errorMessage"
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("UI Element Tracking Active")
            .setContentText("Upload failed - tap to see details")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullErrorText))
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                stopPendingIntent
            )
            .build()
    }

    private fun createNotification(message: String = "Tracking UI elements..."): Notification {
        val stopIntent = Intent(this, UIElementTrackingService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("UI Element Tracking Active")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_view) // Use a default icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                stopPendingIntent
            )
            .build()
    }

}