package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

internal data class TrainingFlowEntity(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("step_count")
    val stepCount: Int?,
    @SerializedName("steps")
    val steps: List<TrainingStepEntity>?
)
