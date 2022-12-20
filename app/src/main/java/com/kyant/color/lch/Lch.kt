package com.kyant.color.lch

interface Lch {
    fun reduceChroma(error: Double = 0.01): Lch
}
