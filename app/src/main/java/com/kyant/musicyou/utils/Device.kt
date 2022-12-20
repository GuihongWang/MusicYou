package com.kyant.musicyou.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.doOnLayout

fun Context.tickVibrate() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            .vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    }
}

@Composable
fun isKeyboardOpen(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        var visible by remember { mutableStateOf(false) }
        with(LocalView.current) {
            doOnLayout {
                visible = rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true
                setOnApplyWindowInsetsListener { _, windowInsets ->
                    visible = rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true
                    windowInsets
                }
            }
        }
        visible
    } else androidx.compose.foundation.layout.WindowInsets.ime.asPaddingValues().calculateBottomPadding() != 0.dp
}
