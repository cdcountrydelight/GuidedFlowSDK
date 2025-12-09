package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName


 data class FlowListResponseEntity(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean? = null,
    @SerializedName("step_count")
    val stepCount: Int? = null,
    @SerializedName("user_progress")
    val userProgress: UserProgressResponseEntity? = null,
)