package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

data class CompleteFlowResponseEntity(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("flow_id")
    val flowId: Int? = null,

    @SerializedName("flow_name")
    val flowName: String? = null,

    @SerializedName("completed_at")
    val completedAt: String? = null,

    @SerializedName("user_id")
    val userId: Int? = null
)