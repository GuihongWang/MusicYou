package com.kyant.color.zcam

import com.kyant.color.lch.Lch

data class ZcamJzczhz(
    val Jz: Double,
    val Cz: Double,
    val hz: Double,
    val cond: Zcam.Companion.ViewingConditions = Zcam.DefaultViewingConditions
) : Lch {
    fun toZcam(): Zcam {
        return Zcam(
            Jz = Jz,
            Cz = Cz,
            hz = hz,
            cond = cond
        )
    }

    override fun reduceChroma(error: Double): ZcamJzczhz {
        if (Jz <= error) return copy(Jz = 0.0)
        else if (Jz >= 100.0 - error) return copy(Jz = 100.0)

        var low = 0.0
        var high = Cz
        var current = this
        while (high - low >= error) {
            val mid = (low + high) / 2.0
            current = copy(Cz = mid)
            if (!current.toZcam().toSrgb().isInGamut()) {
                high = mid
            } else {
                val next = current.copy(Cz = mid + error).toZcam().toSrgb()
                if (next.isInGamut()) {
                    low = mid
                } else {
                    break
                }
            }
        }
        return current
    }

    companion object {
        fun Zcam.toZcamJzczhz(): ZcamJzczhz {
            return ZcamJzczhz(Jz, Cz, hz, cond)
        }
    }
}
