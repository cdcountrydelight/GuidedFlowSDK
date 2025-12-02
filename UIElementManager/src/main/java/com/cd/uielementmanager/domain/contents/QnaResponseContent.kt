package com.cd.uielementmanager.domain.contents

data class QnaResponseContent(
    val id: String,
    val question: List<QuestionResponseContent> = listOf(),
    val flowId: String,
    val calculatedScore:Double?
)