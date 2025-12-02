package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

data class CompleteQuestionsRequestEntity(
    @SerializedName("question_id") val question: String,
    @SerializedName("selected_option_ids") val options: List<String>

//    @SerializedName("question") val question: String,
//    @SerializedName("options") val options: List<String>
)