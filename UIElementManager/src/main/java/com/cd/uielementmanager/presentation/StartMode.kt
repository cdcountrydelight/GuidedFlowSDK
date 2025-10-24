package com.cd.uielementmanager.presentation

import kotlinx.serialization.Serializable

@Serializable
enum class StartMode {
    Sender,
    Training,
    Both
}