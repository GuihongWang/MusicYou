package com.kyant.color.palette

data class TonalPalettes(
    val keyColor: Hct,
    val style: PaletteStyle,
    val accent1: TonalPalette,
    val accent2: TonalPalette,
    val accent3: TonalPalette,
    val neutral1: TonalPalette,
    val neutral2: TonalPalette
) {
    companion object {
        val tonalValues = doubleArrayOf(
            100.0,
            99.0,
            95.0,
            90.0,
            80.0,
            70.0,
            60.0,
            49.6,
            40.0,
            30.0,
            20.0,
            10.0,
            0.0
        )

        fun Hct.generateTonalPalettes(style: PaletteStyle = PaletteStyle.Default): TonalPalettes {
            return TonalPalettes(
                keyColor = this,
                style = style,
                accent1 = tonalValues.associateWith { tone ->
                    transform(tone, style.accent1Spec)
                },
                accent2 = tonalValues.associateWith { tone ->
                    transform(tone, style.accent2Spec)
                },
                accent3 = tonalValues.associateWith { tone ->
                    transform(tone, style.accent3Spec)
                },
                neutral1 = tonalValues.associateWith { tone ->
                    transform(tone, style.neutral1Spec)
                },
                neutral2 = tonalValues.associateWith { tone ->
                    transform(tone, style.neutral2Spec)
                }
            )
        }

        private fun Hct.transform(
            tone: Double,
            spec: ColorSpec
        ): Hct {
            return copy(
                h = h + spec.hueShift,
                c = with(
                    if (spec.fixedChroma) spec.chroma
                    else c.coerceAtLeast(spec.chroma)
                ) {
                    if (tone >= 90.0) coerceAtMost(40.0) else this
                } * when (type) {
                    HctType.Cam16 -> 2.0 / 3.0
                    HctType.Zcam -> 1.0 / 3.0
                },
                t = tone
            )
        }
    }
}
