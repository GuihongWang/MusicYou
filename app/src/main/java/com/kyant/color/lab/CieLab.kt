package com.kyant.color.lab

import com.kyant.color.xyz.CieXyz
import com.kyant.color.xyz.Illuminant
import kotlin.math.pow

data class CieLab(
    val L: Double,
    val a: Double,
    val b: Double,
    val white: CieXyz = Illuminant.D65_2deg
) {
    fun toCieXyz(): CieXyz {
        fun fInv(x: Double): Double {
            return if (x > 6.0 / 29.0) x.pow(3.0)
            else 108.0 / 841.0 * (x - 4.0 / 29.0)
        }

        val lp = (L + 16.0) / 116.0
        return CieXyz(
            x = white.x * fInv(lp + (a / 500.0)),
            y = white.y * fInv(lp),
            z = white.z * fInv(lp - (b / 200.0))
        )
    }

    companion object {
        fun CieXyz.toCieLab(white: CieXyz = Illuminant.D65_2deg): CieLab {
            fun f(x: Double): Double {
                return if (x > 216.0 / 24389.0) x.pow(1.0 / 3.0)
                else x / (108.0 / 841.0) + 4.0 / 29.0
            }

            return CieLab(
                L = 116.0 * f(y / white.y) - 16.0,
                a = 500.0 * (f(x / white.x) - f(y / white.y)),
                b = 200.0 * (f(y / white.y) - f(z / white.z)),
                white = white
            )
        }
    }
}
