package com.example.focusparty.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.annotation.DrawableRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import com.example.focusparty.R

fun resizeImageBitmapKeepRatio(
    source: ImageBitmap,
    maxSize: Int
): ImageBitmap {

    val bmp = source.asAndroidBitmap()

    val width = bmp.width
    val height = bmp.height
    val maxDim = maxOf(width, height)

    // Rien à redimensionner
    if (maxDim <= maxSize) return source

    val scale = maxSize.toFloat() / maxDim.toFloat()

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    val resizedBmp = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, true)

    return resizedBmp.asImageBitmap()
}


@DrawableRes
fun treeDrawableForLevel(level: Int): Int {
    return when ((level / 10).coerceIn(0, 12)) {
        0 -> R.drawable.tree_0
        1 -> R.drawable.tree_1
        2 -> R.drawable.tree_2
        3 -> R.drawable.tree_3
        4 -> R.drawable.tree_4
        5 -> R.drawable.tree_5
        6 -> R.drawable.tree_6
        7 -> R.drawable.tree_7
        8 -> R.drawable.tree_8
        9 -> R.drawable.tree_9
        10 -> R.drawable.tree_10
        11 -> R.drawable.tree_11
        12 -> R.drawable.tree_12
        else -> R.drawable.tree_0
    }
}



@Composable
fun TreeLevelImage(
    level: Int,
    maxSize: Int,
    modifier: Modifier = Modifier
) {
    val img: ImageBitmap =
        ImageBitmap.imageResource(id = treeDrawableForLevel(level))
            .let { resizeImageBitmapKeepRatio(it, maxSize) }

    Image(
        bitmap = img,
        contentDescription = null,
        modifier = modifier
    )
}

