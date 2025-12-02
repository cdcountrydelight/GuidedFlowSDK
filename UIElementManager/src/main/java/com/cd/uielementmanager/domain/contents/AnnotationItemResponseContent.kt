package com.cd.uielementmanager.domain.contents

import kotlinx.serialization.Serializable

@Serializable
data class AnnotationItemResponseContent(
    val type: String?,
    val strokeColor: String?,
    val coordinates: CoordinatesContent,
    val strokeWidth: Float?,
)