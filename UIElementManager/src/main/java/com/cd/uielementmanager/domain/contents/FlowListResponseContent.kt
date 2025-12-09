package com.cd.uielementmanager.domain.contents

data class FlowListResponseContent(
    val id: Int,
    val name: String?,
    val description: String? = null,
    val isActive: Boolean? = null,
    val stepCount: Int? = null,
    var userProgress: UserProgressResponseContent? = null,)
