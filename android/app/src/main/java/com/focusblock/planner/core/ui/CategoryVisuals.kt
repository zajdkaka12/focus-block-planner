package com.focusblock.planner.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.focusblock.planner.R

@DrawableRes
fun categorySprite(spriteIndex: Int): Int = when (spriteIndex) {
    1 -> R.drawable.sprite_1
    2 -> R.drawable.sprite_2
    3 -> R.drawable.sprite_3
    else -> R.drawable.sprite_4
}

fun argbColor(value: Long): Color = Color(value.toInt())

fun darken(color: Color, factor: Float = 0.62f): Color = Color(
    red = color.red * factor,
    green = color.green * factor,
    blue = color.blue * factor,
    alpha = color.alpha
)
