package com.kyant.color.palette

data class PaletteStyle(
    val accent1Spec: ColorSpec,
    val accent2Spec: ColorSpec,
    val accent3Spec: ColorSpec,
    val neutral1Spec: ColorSpec,
    val neutral2Spec: ColorSpec
) {
    companion object {
        val Default = PaletteStyle(
            accent1Spec = ColorSpec(0.0, 32.0, false),
            accent2Spec = ColorSpec(0.0, 16.0, true),
            accent3Spec = ColorSpec(60.0, 24.0, true),
            neutral1Spec = ColorSpec(0.0, 4.0, true),
            neutral2Spec = ColorSpec(0.0, 8.0, true)
        )
        val Spritz = PaletteStyle(
            accent1Spec = ColorSpec(0.0, 12.0, true),
            accent2Spec = ColorSpec(0.0, 8.0, true),
            accent3Spec = ColorSpec(0.0, 16.0, true),
            neutral1Spec = ColorSpec(0.0, 4.0, true),
            neutral2Spec = ColorSpec(0.0, 8.0, true)
        )
        val Vibrant = PaletteStyle(
            accent1Spec = ColorSpec(0.0, 48.0, false),
            accent2Spec = ColorSpec(15.0, 24.0, true),
            accent3Spec = ColorSpec(30.0, 32.0, false),
            neutral1Spec = ColorSpec(0.0, 8.0, true),
            neutral2Spec = ColorSpec(0.0, 16.0, true)
        )
        val Expressive = PaletteStyle(
            accent1Spec = ColorSpec(-60.0, 64.0, false),
            accent2Spec = ColorSpec(-30.0, 24.0, true),
            accent3Spec = ColorSpec(0.0, 48.0, false),
            neutral1Spec = ColorSpec(0.0, 12.0, true),
            neutral2Spec = ColorSpec(0.0, 16.0, true)
        )
        val Rainbow = PaletteStyle(
            accent1Spec = ColorSpec(0.0, 48.0, false),
            accent2Spec = ColorSpec(0.0, 16.0, true),
            accent3Spec = ColorSpec(-60.0, 24.0, true),
            neutral1Spec = ColorSpec(0.0, 0.0, true),
            neutral2Spec = ColorSpec(0.0, 0.0, true)
        )
        val FruitSalad = PaletteStyle(
            accent1Spec = ColorSpec(-50.0, 48.0, false),
            accent2Spec = ColorSpec(-30.0, 36.0, true),
            accent3Spec = ColorSpec(0.0, 36.0, true),
            neutral1Spec = ColorSpec(0.0, 10.0, true),
            neutral2Spec = ColorSpec(0.0, 16.0, true)
        )
    }
}
