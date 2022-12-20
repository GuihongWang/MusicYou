package com.kyant.color.cam16

import com.kyant.color.lab.CieLab
import com.kyant.color.rgb.Srgb
import com.kyant.color.rgb.Srgb.Companion.toSrgb
import com.kyant.color.util.Matrix3
import com.kyant.color.util.toDegrees
import com.kyant.color.util.toRadians
import com.kyant.color.xyz.CieXyz
import com.kyant.color.xyz.CieXyz.Companion.asCieXyz
import com.kyant.color.xyz.Illuminant
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

data class Cam16(
    val J: Double = Double.NaN,
    val C: Double = Double.NaN,
    val h: Double = Double.NaN,
    val Q: Double = Double.NaN,
    val M: Double = Double.NaN,
    val s: Double = Double.NaN,
    val cond: ViewingConditions = DefaultViewingConditions
) {
    fun toSrgb(): Srgb {
        return toCieXyz().toSrgb(cond.white.luminance)
    }

    fun toCieXyz(): CieXyz {
        require(!h.isNaN()) { "Must provide h." }
        require(!Q.isNaN() || !J.isNaN()) { "Must provide Q or J." }
        require(!M.isNaN() || !C.isNaN() || !s.isNaN()) { "Must provide M, C or s." }

        val J = when {
            !J.isNaN() -> J
            !Q.isNaN() -> 50.0 * cond.c * Q / cond.A_w
            else -> Double.NaN
        }
        val Q = when {
            !Q.isNaN() -> Q
            !J.isNaN() -> 2.0 / cond.c * J / 100.0 * cond.A_w
            else -> Double.NaN
        }
        val M = when {
            !M.isNaN() -> M
            !C.isNaN() -> C * cond.A_w / 35.0
            !s.isNaN() -> s * Q / 100.0
            else -> Double.NaN
        }

        if (J == 100.0) return cond.white
        else if (J == 0.0) return CieXyz(0.0, 0.0, 0.0)

        val A = cond.A_w * (J / 100.0).pow(1.0 / cond.cz)
        val gamma = M / (43.0 * cond.N_c * hueToEccentricity(h))
        val hRad = h.toRadians()
        val a = gamma * cos(hRad)
        val b = gamma * sin(hRad)

        val lms_a = aabToLmsaMatrix * doubleArrayOf(A, a, b)
        val lms_c = lms_a.map {
            it.sign * 100.0 / cond.F_L * (27.13 * abs(it) / (400.0 - abs(it))).pow(1.0 / 0.42)
        }
        val lms = lms_c.mapIndexed { i, u -> u / cond.D_LMS[i] }.toDoubleArray()
        val xyz = lmsToXyzMatrix * lms

        return xyz.asCieXyz()
    }

    companion object {
        private val xyzToLmsMatrix = Matrix3(
            doubleArrayOf(0.401288, 0.650173, -0.051461),
            doubleArrayOf(-0.250268, 1.204414, 0.045854),
            doubleArrayOf(-0.002079, 0.048952, 0.953127)
        )
        private val lmsToXyzMatrix = xyzToLmsMatrix.inverse()
        private val lmsaToAabMatrix = Matrix3(
            doubleArrayOf(2.0, 1.0, 0.05),
            doubleArrayOf(1.0, -12.0 / 11.0, 1.0 / 11.0),
            doubleArrayOf(1.0 / 9.0, 1.0 / 9.0, -2.0 / 9.0)
        )
        private val aabToLmsaMatrix = lmsaToAabMatrix.inverse()

        val DefaultViewingConditions = ViewingConditions(
            white = Illuminant.D65_2deg * 100.0,
            L_A = 2.0 / PI * CieLab(50.0, 0.0, 0.0, Illuminant.D65_2deg * 100.0).toCieXyz().luminance,
            Y_b = CieLab(50.0, 0.0, 0.0, Illuminant.D65_2deg * 100.0).toCieXyz().luminance
        )

        private fun hueToEccentricity(h: Double): Double {
            val hRad = h.toRadians()
            return -0.0582 * cos(hRad) -
                0.0258 * cos(2.0 * hRad) -
                0.1347 * cos(3.0 * hRad) +
                0.0289 * cos(4.0 * hRad) -
                0.1475 * sin(hRad) -
                0.0308 * sin(2.0 * hRad) +
                0.0385 * sin(3.0 * hRad) +
                0.0096 * sin(4.0 * hRad) +
                1.0
        }

        fun Srgb.toCam16(cond: ViewingConditions = DefaultViewingConditions): Cam16 {
            return toCieXyz(cond.white.luminance).toCam16(cond)
        }

        fun CieXyz.toCam16(cond: ViewingConditions = DefaultViewingConditions): Cam16 {
            val lms = xyzToLmsMatrix * xyz
            val lms_c = lms.mapIndexed { i, u -> cond.D_LMS[i] * u }
            val lms_a =
                lms_c.map { it.sign * 400.0 * (1.0 - 27.13 / ((cond.F_L * it / 100.0).pow(0.42) + 27.13)) }

            val (A, a, b) = lmsaToAabMatrix * lms_a.toDoubleArray()

            val h = atan2(b, a).toDegrees().mod(360.0)
            val J = 100.0 * (A / cond.A_w).pow(cond.cz)
            val Q = 2.0 / cond.c * J / 100.0 * cond.A_w
            val M = 43.0 * cond.N_c * hueToEccentricity(h) * sqrt(a.pow(2.0) + b.pow(2.0))
            val C = 35.0 * M / cond.A_w
            val s = 100.0 * M / Q

            return Cam16(J, C, h, Q, M, s, cond)
        }

        data class ViewingConditions(
            val white: CieXyz,
            val L_A: Double,
            val Y_b: Double
        ) {
            private val F = 1.0
            val c = 0.69
            val N_c = 1.0

            private val D = (F * (1.0 - exp(-(L_A + 42.0) / 92.0) / 3.6)).coerceIn(0.0..1.0)
            private val lms_w = xyzToLmsMatrix * white.xyz
            val D_LMS = lms_w.map { D * (100.0 / it - 1.0) + 1.0 }
            private val k = 1.0 / (5.0 * L_A + 1.0).pow(4.0)
            val F_L = k * L_A + 0.1 * (1 - k).pow(2.0) * (5.0 * L_A).pow(1.0 / 3.0)
            private val n = Y_b / 100.0
            private val z = 1.48 + sqrt(n)
            val cz = c * z

            private val lms_wc = lms_w.mapIndexed { i, u -> D_LMS[i] * u }.apply {
                println(joinToString())
            }
            private val lms_wa =
                lms_wc.map { 400.0 * (1.0 - 27.13 / ((F_L * it / 100.0).pow(0.42) + 27.13)) }.apply {
                    println(joinToString())
                }
            val A_w = 2.0 * lms_wa[0] + lms_wa[1] + lms_wa[2] / 20.0
        }
    }
}
