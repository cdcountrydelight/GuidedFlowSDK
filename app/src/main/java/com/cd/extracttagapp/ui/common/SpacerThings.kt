package com.cd.extracttagapp.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
private fun GenericSpacer(width: Dp = 0.dp, height: Dp = 0.dp) {
    Spacer(
        modifier = Modifier
            .width(width)
            .height(height)
    )
}

//height
@Composable
internal fun SpacerHeight8() {
    GenericSpacer(height = 8.dp)
}

@Composable
internal fun SpacerHeight4() {
    GenericSpacer(height = 4.dp)
}


@Composable
internal fun SpacerHeight12() {
    GenericSpacer(height = 12.dp)
}

@Composable
internal fun SpacerHeight16() {
    GenericSpacer(height = 16.dp)
}

@Composable
internal fun SpacerHeight24() {
    GenericSpacer(height = 24.dp)
}


//width
@Composable
internal fun SpacerWidth4() {
    GenericSpacer(width = 4.dp)
}

