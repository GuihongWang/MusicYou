package com.kyant.color.xyz

/** CMFs are grabbed from [http://www.cvrl.org/cmfs.htm] */
object Illuminant {
    val D65_2deg by lazy { D65_1951_2deg }

    /** CIE 1931 2-deg, XYZ CMFs,
     *  1nm interval lerped from 5nm */
    val D65_1931_2deg by lazy {
        CieXyz(
            x = 10043.7000153676 / 10567.0816669881,
            y = 1.0,
            z = 11505.7421788588 / 10567.0816669881
        )
    }

    /** CIE 1931 2-deg, XYZ CMFs modified by Judd (1951),
     * 0.1nm interval lerped from 1nm */
    val D65_1951_2deg by lazy {
        CieXyz(
            x = 100436.833728488 / 105670.644408308,
            y = 1.0,
            z = 115055.861530871 / 105670.644408308
        )
    }

    /** 2-deg XYZ CMFs transformed from the CIE (2006) 2-deg LMS cone fundamentals,
     * 0.1nm interval lerped from 5nm */
    val D65_2012_2deg by lazy {
        CieXyz(
            x = 105847.775067807 / 111706.666652315,
            y = 1.0,
            z = 120113.309922310 / 111706.666652315
        )
    }
}
