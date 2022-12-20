package com.kyant.color.rgb

import com.kyant.color.util.Matrix3
import com.kyant.color.util.div
import com.kyant.color.xyz.CieXyz
import com.kyant.color.xyz.CieXyz.Companion.asCieXyz
import com.kyant.color.xyz.Illuminant
import kotlin.math.pow
import kotlin.math.roundToInt

data class Srgb(
    val r: Double,
    val g: Double,
    val b: Double
) {
    inline val rgb
        get() = doubleArrayOf(r, g, b)

    fun isInGamut(): Boolean {
        return rgb.map { it in 0.0..1.0 }.all { it }
    }

    fun clamp(): Srgb {
        return Srgb(
            r = r.coerceIn(0.0, 1.0),
            g = g.coerceIn(0.0, 1.0),
            b = b.coerceIn(0.0, 1.0)
        )
    }

    fun toIntSrgb(): IntSrgb {
        return IntSrgb(
            r = (r * 255.0).roundToInt(),
            g = (g * 255.0).roundToInt(),
            b = (b * 255.0).roundToInt()
        )
    }

    fun toCieXyz(luminance: Double = 1.0): CieXyz {
        return (rgbToXyzMatrix * rgb.map { EOTF(it) }.toDoubleArray()).asCieXyz() * luminance
    }

    companion object {
        private const val gamma = 2.4
        private const val alpha = 1.055
        private val beta = ((gamma * (alpha - 1.0)) / (alpha * (gamma - 1.0))).pow(gamma)
        private val delta = (alpha - 1.0) * (gamma - 1.0).pow(gamma - 1.0) / (gamma).pow(gamma)
        private val E_0 = (alpha - 1.0).pow(gamma + 1.0) / (alpha * (gamma - 1.0))
        private val white = Illuminant.D65_2deg
        private val primaries = Matrix3(
            doubleArrayOf(0.64, 0.33, 0.03),
            doubleArrayOf(0.30, 0.60, 0.10),
            doubleArrayOf(0.15, 0.06, 0.79)
        )
        private val rgbToXyzMatrix: Matrix3
            get() {
                val M1 = Matrix3(
                    doubleArrayOf(
                        primaries[0][0] / primaries[0][1],
                        primaries[1][0] / primaries[1][1],
                        primaries[2][0] / primaries[2][1]
                    ),
                    doubleArrayOf(1.0, 1.0, 1.0),
                    doubleArrayOf(
                        primaries[0][2] / primaries[0][1],
                        primaries[1][2] / primaries[1][1],
                        primaries[2][2] / primaries[2][1]
                    )
                )
                val M2 = M1.inverse() * white.xyz
                return Matrix3(
                    doubleArrayOf(M1[0][0] * M2[0], M1[0][1] * M2[1], M1[0][2] * M2[2]),
                    doubleArrayOf(M1[1][0] * M2[0], M1[1][1] * M2[1], M1[1][2] * M2[2]),
                    doubleArrayOf(M1[2][0] * M2[0], M1[2][1] * M2[1], M1[2][2] * M2[2])
                )
            }
        private val xyzToRgbMatrix: Matrix3 = rgbToXyzMatrix.inverse()

        private fun EOTF(x: Double): Double {
            return if (x >= E_0) ((x + alpha - 1.0) / alpha).pow(gamma)
            else x / delta
        }

        private fun OETF(x: Double): Double {
            return if (x >= beta) alpha * (x.pow(1.0 / gamma) - 1.0) + 1.0
            else x * delta
        }

        fun CieXyz.toSrgb(luminance: Double = 1.0): Srgb {
            return (xyzToRgbMatrix * (xyz / luminance))
                .map { OETF(it) }
                .toDoubleArray().asSrgb()
        }

        fun DoubleArray.asSrgb(): Srgb {
            return Srgb(this[0], this[1], this[2])
        }
    }
}

data class IntSrgb(
    val r: Int,
    val g: Int,
    val b: Int
) {
    inline val rgb
        get() = intArrayOf(r, g, b)

    fun isInGamut(): Boolean {
        return r in 0..255 && g in 0..255 && b in 0..255
    }

    fun toSrgb(): Srgb {
        return Srgb(
            r = r / 255.0,
            g = g / 255.0,
            b = b / 255.0
        )
    }

    fun toHexString(): String {
        return "#${"%02X".format(r)}${"%02X".format(g)}${"%02X".format(b)}"
    }

    companion object {
        fun IntArray.asIntSrgb(): IntSrgb {
            return IntSrgb(this[0], this[1], this[2])
        }
    }
}
