package com.kyant.color.cam16

import com.kyant.color.lch.Lch

data class Cam16Jch(
    val J: Double,
    val C: Double,
    val h: Double,
    val cond: Cam16.Companion.ViewingConditions = Cam16.DefaultViewingConditions
) : Lch {
    fun toCam16(): Cam16 {
        return Cam16(
            J = J,
            C = C,
            h = h,
            cond = cond
        )
    }

    override fun reduceChroma(error: Double): Cam16Jch {
        if (J <= error) return copy(J = 0.0)
        else if (J >= 100.0 - error) return copy(J = 100.0)

        var low = 0.0
        var high = C
        var current = this
        while (high - low >= error) {
            val mid = (low + high) / 2.0
            current = copy(C = mid)
            if (!current.toCam16().toSrgb().isInGamut()) {
                high = mid
            } else {
                val next = current.copy(C = mid + error).toCam16().toSrgb()
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
        fun Cam16.toCam16Jch(): Cam16Jch {
            return Cam16Jch(J, C, h, cond)
        }
    }
}
