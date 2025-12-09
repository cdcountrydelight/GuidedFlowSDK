package com.cd.extracttagapp.di

import com.cd.uielementmanager.presentation.viewmodels.UIElementViewModel
import org.koin.dsl.module

/** * Koin module for UI element extraction dependency injection
 * Updated to use clean architecture with backward compatibility
 */
val tooltipModule = module {

    // Clean architecture ViewModel - Application-scoped singleton
    single(createdAtStart = true) {
        UIElementViewModel()
    }
}

/**
 * To use this module, add it to your Koin configuration in the Application class:
 *
 * startKoin {
 *     androidContext(this@YourApplication)
 *     modules(
 *         // ... other modules
 *         tooltipModule
 *     )
 * }
 */