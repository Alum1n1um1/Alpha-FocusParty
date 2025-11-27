package com.example.focusparty.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SurfaceLevel {
    Low, Normal, High
}

@Composable
fun CustomSurface(
    level: SurfaceLevel,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    color: Color? = null,
    contentColor: Color? = null,
    tonalElevation: Dp? = null,
    shadowElevation: Dp? = null,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    // Valeurs par défaut basées sur level (uniquement si non fournies explicitement)
    val resolvedColor = color ?: when (level) {
        SurfaceLevel.Low    -> MaterialTheme.colorScheme.surfaceContainerLow
        SurfaceLevel.Normal -> MaterialTheme.colorScheme.surfaceContainer
        SurfaceLevel.High   -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val resolvedTonal = tonalElevation ?: when (level) {
        SurfaceLevel.Low    -> 0.dp
        SurfaceLevel.Normal -> 3.dp
        SurfaceLevel.High   -> 6.dp
    }

    val resolvedShadow = shadowElevation ?: when (level) {
        SurfaceLevel.Low    -> 0.dp
        SurfaceLevel.Normal -> 3.dp
        SurfaceLevel.High   -> 6.dp
    }

    val resolvedShape = shape ?: when (level) {
        SurfaceLevel.Low    -> MaterialTheme.shapes.medium
        SurfaceLevel.Normal -> MaterialTheme.shapes.medium
        SurfaceLevel.High   -> MaterialTheme.shapes.large
    }

    val resolvedContentColor =
        contentColor ?: contentColorFor(resolvedColor)

    // Interaction source
    val resolvedInteraction = interactionSource ?: remember { MutableInteractionSource() }

    // Composition
    androidx.compose.material3.Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = resolvedShape,
        color = resolvedColor,
        contentColor = resolvedContentColor,
        tonalElevation = resolvedTonal,
        shadowElevation = resolvedShadow,
        border = border,
        interactionSource = resolvedInteraction
    ) {
        content()
    }
}

@Composable
fun HighSurface(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    color: Color? = null,
    contentColor: Color? = null,
    tonalElevation: Dp? = null,
    shadowElevation: Dp? = null,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    // Composition
    CustomSurface(
        level=SurfaceLevel.High,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource
    ) {
        content()
    }
}

@Composable
fun LowSurface(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    color: Color? = null,
    contentColor: Color? = null,
    tonalElevation: Dp? = null,
    shadowElevation: Dp? = null,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    // Composition
    CustomSurface(
        level=SurfaceLevel.Low,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource
    ) {
        content()
    }
}

@Composable
fun NormalSurface(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    color: Color? = null,
    contentColor: Color? = null,
    tonalElevation: Dp? = null,
    shadowElevation: Dp? = null,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    // Composition
    CustomSurface(
        level=SurfaceLevel.Normal,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource
    ) {
        content()
    }
}