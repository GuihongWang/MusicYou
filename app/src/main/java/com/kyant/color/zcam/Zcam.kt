package com.kyant.color.zcam

import com.kyant.color.lab.CieLab
import com.kyant.color.rgb.Srgb
import com.kyant.color.rgb.Srgb.Companion.toSrgb
import com.kyant.color.util.square
import com.kyant.color.util.toDegrees
import com.kyant.color.util.toRadians
import com.kyant.color.xyz.CieXyz
import com.kyant.color.xyz.Illuminant
import com.kyant.color.zcam.Izazbz.Companion.toIzazbz
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Zcam(
    val hz: Double = Double.NaN,
    val Qz: Double = Double.NaN,
    val Jz: Double = Double.NaN,
    val Mz: Double = Double.NaN,
    val Cz: Double = Double.NaN,
    val Sz: Double = Double.NaN,
    val Vz: Double = Double.NaN,
    val Kz: Double = Double.NaN,
    val Wz: Double = Double.NaN,
    val cond: ViewingConditions = DefaultViewingConditions
) {
    fun toSrgb(): Srgb {
        return toIzazbz().toCieXyz().toSrgb(cond.white.luminance)
    }

    fun toIzazbz(): Izazbz {
        require(!hz.isNaN()) { "Must provide hz." }
        require(!Qz.isNaN() || !Jz.isNaN()) { "Must provide Qz or Jz." }
        require(
            !Mz.isNaN() || !Cz.isNaN() || !Sz.isNaN() || !Vz.isNaN() || !Kz.isNaN() || !Wz.isNaN()
        ) {
            "Must provide Mz, Cz, Sz, Vz, Kz or Wz."
        }

        val Iz = (
            when {
                !Qz.isNaN() -> Qz
                !Jz.isNaN() -> Jz * cond.Qzw / 100.0
                else -> Double.NaN
            } / (2700.0 * cond.F_s.pow(2.2) * sqrt(cond.F_b) * cond.F_L.pow(0.2))
            ).pow(cond.F_b.pow(0.12) / (1.6 * cond.F_s))
        val Jz = Jz.takeUnless { it.isNaN() } ?: when {
            !Qz.isNaN() -> 100.0 * Qz / cond.Qzw
            else -> Double.NaN
        }
        val Qz = Qz.takeUnless { it.isNaN() } ?: when {
            !Jz.isNaN() -> Jz * cond.Qzw / 100.0
            else -> Double.NaN
        }
        val Cz = Cz.takeUnless { it.isNaN() } ?: when {
            !Sz.isNaN() -> Qz * square(Sz) / (100.0 * cond.Qzw * cond.F_L.pow(1.2))
            !Vz.isNaN() -> sqrt((square(Vz) - square(Jz - 58.0)) / 3.4)
            !Kz.isNaN() -> sqrt((square((100.0 - Kz) / 0.8) - square(Jz)) / 8.0)
            !Wz.isNaN() -> sqrt(square(100.0 - Wz) - square(100.0 - Jz))
            else -> Double.NaN
        }
        val Mz = Mz.takeUnless { it.isNaN() } ?: (Cz * cond.Qzw / 100.0)

        val ez = 1.015 + cos(89.038 + hz).toRadians()
        val Cz_ =
            (
                Mz * cond.Izw.pow(0.78) * cond.F_b.pow(0.1) / (
                    100.0 * ez.pow(0.068) * cond.F_L.pow(
                        0.2
                    )
                    )
                )
                .pow(1.0 / (0.37 * 2.0))
        val hzRad = hz.toRadians()
        val az = Cz_ * cos(hzRad)
        val bz = Cz_ * sin(hzRad)

        return Izazbz(
            Iz = Iz,
            az = az,
            bz = bz
        )
    }

    companion object {
        val DefaultViewingConditions = ViewingConditions(
            white = Illuminant.D65_2deg * 203.0,
            F_s = 0.69,
            L_a = 0.4 * 203.0,
            Y_b = CieLab(50.0, 0.0, 0.0, Illuminant.D65_2deg * 203.0).toCieXyz().luminance
        )

        data class ViewingConditions(
            val white: CieXyz,
            val F_s: Double,
            val L_a: Double,
            val Y_b: Double
        ) {
            private val Y_w = white.luminance
            val F_b = sqrt(Y_b / Y_w)
            val F_L = 0.171 * L_a.pow(1.0 / 3.0) * (1 - exp(-48.0 / 9.0 * L_a))
            val Izw = white.toIzazbz().Iz
            val Qzw =
                2700.0 * Izw.pow(1.6 * F_s / F_b.pow(0.12)) * F_s.pow(2.2) * sqrt(F_b) * F_L.pow(
                    0.2
                )
        }

        fun Srgb.toZcam(cond: ViewingConditions = DefaultViewingConditions): Zcam {
            return toCieXyz(cond.white.luminance).toIzazbz().toZcam(cond)
        }

        fun Izazbz.toZcam(cond: ViewingConditions = DefaultViewingConditions): Zcam {
            val hz = atan2(bz, az).toDegrees().mod(360.0)
            val Qz =
                2700.0 * Iz.pow(1.6 * cond.F_s / cond.F_b.pow(0.12)) * cond.F_s.pow(2.2) * sqrt(
                    cond.F_b
                ) *
                    cond.F_L.pow(0.2)
            val Jz = 100.0 * Qz / cond.Qzw
            val ez = 1.015 + cos(89.038 + hz).toRadians()
            val Mz =
                100.0 * (square(az) + square(bz)).pow(0.37) * ez.pow(0.068) * cond.F_L.pow(0.2) /
                    (cond.F_b.pow(0.1) * cond.Izw.pow(0.78))
            val Cz = 100.0 * Mz / cond.Qzw

            val Sz = 100.0 * cond.F_L.pow(0.6) * sqrt(Mz / Qz)
            val Vz = sqrt(square(Jz - 58.0) + 3.4 * square(Cz))
            val Kz = 100.0 - 0.8 * sqrt(square(Jz) + 8.0 * square(Cz))
            val Wz = 100.0 - sqrt(square(100.0 - Jz) + square(Cz))

            return Zcam(
                hz = hz,
                Qz = Qz,
                Jz = Jz,
                Mz = Mz,
                Cz = Cz,
                Sz = Sz,
                Vz = Vz,
                Kz = Kz,
                Wz = Wz,
                cond = cond
            )
        }
    }
}
