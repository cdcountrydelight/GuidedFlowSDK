package com.cd.extracttagapp

import android.app.Application
import com.cd.extracttagapp.di.networkModule
import com.cd.extracttagapp.di.tooltipModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApp: Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            // Log Koin configuration (only in debug builds)
            androidLogger(Level.ERROR)
            
            // Android context
            androidContext(this@BaseApp)
            
            // Load modules
            modules(
                networkModule,
                tooltipModule // This now uses clean architecture
            )
        }
    }
}