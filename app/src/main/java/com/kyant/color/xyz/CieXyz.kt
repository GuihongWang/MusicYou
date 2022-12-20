package com.kyant.color.xyz

import com.kyant.color.util.div
import com.kyant.color.util.times

data class CieXyz(
    val x: Double,
    val y: Double,
    val z: Double
) {
    inline val xyz
        get() = doubleArrayOf(x, y, z)

    val luminance = y

    operator fun times(luminance: Double): CieXyz {
        return (xyz * luminance).asCieXyz()
    }

    operator fun div(luminance: Double): CieXyz {
        return (xyz / luminance).asCieXyz()
    }

    companion object {
        fun DoubleArray.asCieXyz(): CieXyz {
            return CieXyz(this[0], this[1], this[2])
        }
    }
}
