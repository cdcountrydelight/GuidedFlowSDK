package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

internal data class QuestionResponseEntity(
    @SerializedName("id") val questionId: String?,
    @SerializedName("text") val question: String?,
    @SerializedName("options") val options: List<OptionsEntity>?,
    @SerializedName("is_multiple_choice") val isMsq: Boolean? = false,
    @SerializedName("required") val isRequired: Boolean? = true
)