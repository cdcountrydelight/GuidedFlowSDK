package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

internal data class OptionsEntity(
    @SerializedName("id") val optionId: String?,
    @SerializedName("text") val option: String?
)