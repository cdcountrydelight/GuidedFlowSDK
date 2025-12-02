package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

data class CompleteQnAResponseEntity(@SerializedName("score") val calculatedScore: Double? = null)