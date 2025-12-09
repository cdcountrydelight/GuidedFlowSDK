package com.cd.uielementmanager.domain.contents

import kotlinx.serialization.Serializable

@Serializable
data class AnnotationResponseContent(
    val annotations: List<AnnotationItemResponseContent>,
)