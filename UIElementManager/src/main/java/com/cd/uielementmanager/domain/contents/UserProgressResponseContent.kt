package com.cd.uielementmanager.domain.contents

import kotlinx.serialization.Serializable

@Serializable
 data class UserProgressResponseContent(
    var isStarted: Boolean? = null,
    var isCompleted: Boolean? = null,
    val startedAt: String? = null,
    val completedAt: String? = null,
)