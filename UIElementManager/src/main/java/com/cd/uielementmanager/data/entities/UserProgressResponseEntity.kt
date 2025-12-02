package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserProgressResponseEntity(
    @SerializedName("started")
    val isStarted: Boolean? = null,
    @SerializedName("completed")
    val isCompleted: Boolean? = null,
    @SerializedName("started_at")
    val startedAt: String? = null,
    @SerializedName("completed_at")
    val completedAt: String? = null,
)