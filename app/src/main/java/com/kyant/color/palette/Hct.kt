package com.kyant.color.palette

import com.kyant.color.cam16.Cam16.Companion.toCam16
import com.kyant.color.cam16.Cam16Jch
import com.kyant.color.cam16.Cam16Jch.Companion.toCam16Jch
import com.kyant.color.cam16.Cam16Ucs.Companion.toCam16Ucs
import com.kyant.color.lab.CieLab
import com.kyant.color.lab.CieLab.Companion.toCieLab
import com.kyant.color.rgb.Srgb
import com.kyant.color.rgb.Srgb.Companion.toSrgb
import com.kyant.color.zcam.Zcam.Companion.toZcam
import com.kyant.color.zcam.ZcamJzczhz
import com.kyant.color.zcam.ZcamJzczhz.Companion.toZcamJzczhz
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

data class Hct(
    val h: Double,
    val c: Double,
    val t: Double,
    val type: HctType = HctType.Cam16
) {
    fun toSrgb(): Srgb {
        when (type) {
            HctType.Cam16 -> run {
                if (c < 1.0 || t.roundToInt() <= 0.0 || t.roundToInt() >= 100.0) {
                    return Cam16Jch(
                        CieLab(t, 0.0, 0.0).toCieXyz().toSrgb().toCam16().J,
                        c,
                        h
                    ).toCam16().toSrgb()
                }
                var high = c
                var mid = high
                var low = 0.0
                var isFirstLoop = true
                var answer: Cam16Jch? = null
                while (high - low >= 0.4) {
                    val possibleAnswer = copy(c = mid).findCam16JchByTone()
                    if (isFirstLoop) {
                        return if (possibleAnswer != null) {
                            possibleAnswer.toCam16().toSrgb()
                        } else {
                            isFirstLoop = false
                            mid = low + (high - low) / 2.0
                            continue
                        }
                    }
                    if (possibleAnswer == null) {
                        high = mid
                    } else {
                        answer = possibleAnswer
                        low = mid
                    }
                    mid = low + (high - low) / 2.0
                }
                return answer?.toCam16()?.toSrgb()
                    ?: Cam16Jch(CieLab(t, 0.0, 0.0).toCieXyz().toSrgb().toCam16().J, c, h).toCam16()
                        .toSrgb()
            }
            HctType.Zcam -> run {
                if (t.roundToInt() <= 0.0) {
                    return Srgb(0.0, 0.0, 0.0)
                } else if (t.roundToInt() >= 100.0) {
                    return Srgb(1.0, 1.0, 1.0)
                }
                var high = c
                var mid = high
                var low = 0.0
                var isFirstLoop = true
                var answer: ZcamJzczhz? = null
                while (high - low >= 0.4) {
                    val possibleAnswer = copy(c = mid).findZcamJzczhzByTone()
                    if (isFirstLoop) {
                        return if (possibleAnswer != null) {
                            possibleAnswer.toZcam().toSrgb()
                        } else {
                            isFirstLoop = false
                            mid = low + (high - low) / 2.0
                            continue
                        }
                    }
                    if (possibleAnswer == null) {
                        high = mid
                    } else {
                        answer = possibleAnswer
                        low = mid
                    }
                    mid = low + (high - low) / 2.0
                }
                return answer?.toZcam()?.toSrgb()
                    ?: ZcamJzczhz(
                        CieLab(t, 0.0, 0.0).toCieXyz().toSrgb().toZcam().Jz,
                        c,
                        h
                    ).toZcam().toSrgb()
            }
        }
    }

    private fun findCam16JchByTone(): Cam16Jch? {
        var low = 0.0
        var high = 100.0
        var mid: Double
        var bestdL = 1000.0
        var bestdE = 1000.0
        var bestCam: Cam16Jch? = null

        while (high - low > 0.01) {
            mid = low + (high - low) / 2.0
            val camBeforeClip = Cam16Jch(mid, c, h).toCam16()
            val clipped = camBeforeClip.toSrgb().clamp()
            val clippedTone = clipped.toCieXyz().toCieLab().L
            val dL = abs(t - clippedTone)
            if (dL < 0.2) {
                val camClipped = clipped.toCam16()
                val dE = camClipped.toCam16Ucs().deltaE(camClipped.copy(h = h).toCam16Ucs())
                if (dE <= 1.0) {
                    bestdL = dL
                    bestdE = dE
                    bestCam = camClipped.toCam16Jch()
                }
            }
            if (bestdL == 0.0 && bestdE == 0.0) {
                break
            }
            if (clippedTone < t) {
                low = mid
            } else {
                high = mid
            }
        }
        return bestCam
    }

    private fun findZcamJzczhzByTone(): ZcamJzczhz? {
        var low = 0.0
        var high = 100.0
        var mid: Double
        var bestdL = 1000.0
        var bestdE = 1000.0
        var bestCam: ZcamJzczhz? = null

        while (high - low > 0.01) {
            mid = low + (high - low) / 2.0
            val camBeforeClip = ZcamJzczhz(mid, c, h).toZcam()
            val clipped = camBeforeClip.toSrgb().clamp()
            val clippedTone = clipped.toCieXyz().toCieLab().L
            val dL = abs(t - clippedTone)
            if (dL < 0.2) {
                val camClipped = clipped.toZcam()
                val dE = camClipped.toSrgb().toCam16().toCam16Ucs()
                    .deltaE(camClipped.copy(hz = h).toSrgb().toCam16().toCam16Ucs())
                if (dE <= 1.0) {
                    bestdL = dL
                    bestdE = dE
                    bestCam = camClipped.toZcamJzczhz()
                }
            }
            if (bestdL == 0.0 && bestdE == 0.0) {
                break
            }
            if (clippedTone < t) {
                low = mid
            } else {
                high = mid
            }
        }
        return bestCam
    }

    fun harmonizeTowards(
        target: Hct,
        factor: Double = 0.5,
        maxHueShift: Double = 15.0
    ): Hct = copy(
        h = h + (((180.0 - abs(abs(h - target.h) - 180.0)) * factor).coerceAtMost(maxHueShift)) * (
            listOf(
                target.h - h,
                target.h - h + 360.0,
                target.h - h - 360.0
            ).minOf { abs(it) }.sign.takeIf { it != 0.0 } ?: 1.0
            )
    )

    companion object {
        fun Srgb.toHct(type: HctType = HctType.Cam16): Hct {
            return when (type) {
                HctType.Cam16 -> {
                    val cam = toCam16()
                    Hct(cam.h, cam.C, toCieXyz().toCieLab().L, HctType.Cam16)
                }
                HctType.Zcam -> {
                    val cam = toZcam()
                    Hct(cam.hz, cam.Cz, toCieXyz().toCieLab().L, HctType.Zcam)
                }
            }
        }
    }
}
