package com.kyant.color

import com.kyant.color.cam16.Cam16
import com.kyant.color.cam16.Cam16.Companion.toCam16
import com.kyant.color.rgb.Srgb
import com.kyant.color.rgb.Srgb.Companion.toSrgb

fun main() {
    println(Cam16.DefaultViewingConditions)
    println(Cam16.DefaultViewingConditions.D_LMS)
    println(Cam16.DefaultViewingConditions.F_L)
    println(Cam16.DefaultViewingConditions.cz)
    println(Cam16.DefaultViewingConditions.A_w)

    println(Srgb(0.5, 0.5, 0.5).toCieXyz().toSrgb())
    println(Srgb(0.5, 0.5, 0.5).toCieXyz().toCam16())
}
