package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

data class CompleteQnARequestEntity(
    @SerializedName("answers")
    val answers: List<CompleteQuestionsRequestEntity>

//    @SerializedName("questions")
//    val questions: List<CompleteQuestionsRequestEntity>
)