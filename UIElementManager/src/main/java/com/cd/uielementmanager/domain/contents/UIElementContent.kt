package com.cd.uielementmanager.domain.contents

/**
 * UI element entity for API communication and tracking
 * This is the single source of truth for UI element data
 */
internal data class UIElementContent(val tag: String, val bounds: BoundsContent)