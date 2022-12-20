package com.kyant.color.palette

data class ColorSpec(
    val hueShift: Double = 0.0,
    val chroma: Double,
    val fixedChroma: Boolean = true
)
