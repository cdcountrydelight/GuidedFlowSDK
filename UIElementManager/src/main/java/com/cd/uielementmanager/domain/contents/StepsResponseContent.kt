package com.cd.uielementmanager.domain.contents

import kotlinx.serialization.Serializable


@Serializable
 data class StepsResponseContent(
    val id: Int,
    val stepNumber: Int,
    val height: Double,
    val width: Double,
    val screenshotUrl: String,
    val annotation: AnnotationResponseContent?,
    val instructions: List<String>
)