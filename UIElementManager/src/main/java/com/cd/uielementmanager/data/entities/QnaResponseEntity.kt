package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

internal data class QnaResponseEntity(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("questions")
    val question: List<QuestionResponseEntity>?,
    @SerializedName("flow_id")
    val flowId: String? = null,
    @SerializedName("calculated_score")
    val calculatedScore: Double? = null
)