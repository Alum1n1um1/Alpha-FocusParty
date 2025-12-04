package com.example.focusparty.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.focusparty.utils.treeDrawableForLevel


@Composable
fun TreeLevelImage(
    level: Int,
    modifier: Modifier = Modifier
) {
    val painter: Painter = painterResource(id = treeDrawableForLevel(level))

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )
}
