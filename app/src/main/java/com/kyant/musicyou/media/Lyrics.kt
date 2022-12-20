package com.kyant.musicyou.media

import com.kyant.musicyou.data.PlayerViewModel
import kotlin.time.Duration

typealias Lyrics = Map<String, String>

fun PlayerViewModel.findLyricOrNull(position: Long?): String? {
    return if (position == null) {
        null
    } else {
        currentLyrics.keys.lastOrNull {
            position >= (it.toMillisecondsOrNull() ?: 0)
        }?.let {
            currentLyrics.getOrElse(it) { null }
        }
    }
}

fun PlayerViewModel.currentLyricOrNull(): String? {
    return findLyricOrNull(currentPosition)
}

fun PlayerViewModel.currentLyricIndex(): Int {
    return currentLyrics.keys.indexOfLast {
        currentPosition >= (it.toMillisecondsOrNull() ?: 0)
    }
}

fun String.toLyrics(): Lyrics {
    return split("\n").dropLast(1).associate {
        it.substringBefore("]").drop(1) to it.substringAfter("]")
    }.filterNot { it.value.isBlank() }
}

fun String.toMillisecondsOrNull(): Long? {
    return Duration.parseOrNull(
        replace(":", "m")
            .replace(".", "s") +
            "ms"
    )?.inWholeMilliseconds
}
