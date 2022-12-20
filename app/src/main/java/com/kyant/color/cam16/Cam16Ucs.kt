package com.kyant.color.cam16

import com.kyant.color.cam16.Cam16.Companion.DefaultViewingConditions
import com.kyant.color.util.toDegrees
import com.kyant.color.util.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Cam16Ucs(
    val J: Double,
    val a: Double,
    val b: Double,
    val cond: Cam16.Companion.ViewingConditions = DefaultViewingConditions
) {
    fun toCam16(): Cam16 {
        val J = -J / (0.007 * J - 1.7)
        val M_ = sqrt(a.pow(2.0) + b.pow(2.0))
        val M = (exp(0.0228 * M_) - 1.0) / 0.0228
        val h = atan2(b, a).toDegrees()

        return Cam16(J = J, M = M, h = h, cond = cond)
    }

    fun deltaE(other: Cam16Ucs): Double {
        return 1.41 * sqrt((J - other.J).pow(2.0) + (a - other.a).pow(2.0) + (b - other.b).pow(2.0)).pow(
            0.63
        )
    }

    companion object {
        fun Cam16.toCam16Ucs(): Cam16Ucs {
            val J_ = 1.7 * J / (1.0 + 0.007 * J)
            val M_ = ln(1.0 + 0.0228 * M) / 0.0228
            val a_ = M_ * cos(h.toRadians())
            val b_ = M_ * sin(h.toRadians())

            return Cam16Ucs(J_, a_, b_, cond)
        }
    }
}
