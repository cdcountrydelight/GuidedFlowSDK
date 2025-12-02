package com.cd.uielementmanager.domain.contents

data class QuestionResponseContent(
    val questionId: String,
    val question: String,
    val options: List<OptionsContent>,
    val isMsq: Boolean,
    val isRequired: Boolean,
    val selectedOptions: ArrayList<OptionsContent> = arrayListOf()
)