package com.cd.uielementmanager.domain.contents

data class CompleteFlowResponseContent(
    val message: String? = null,
    val flowId: Int? = null,
    val flowName: String? = null,
    val completedAt: String? = null,
    val userId: Int? = null
)