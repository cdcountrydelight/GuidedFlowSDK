package com.cd.uielementmanager.domain.contents

import kotlinx.serialization.Serializable

@Serializable
 data class FlowDetailsResponseContent(
    var id: Int?,
    val isActive: Boolean?,
    val stepCount: Int?,
    val userProgress: UserProgressResponseContent?,
    val steps: List<StepsResponseContent>?
)