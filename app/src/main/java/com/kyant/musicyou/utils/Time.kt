package com.kyant.musicyou.utils

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Long.toReadableTimeString(): String {
    return milliseconds.inWholeSeconds.toDuration(DurationUnit.SECONDS).toString()
}
