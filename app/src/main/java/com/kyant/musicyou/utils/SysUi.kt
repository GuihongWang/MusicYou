package com.kyant.musicyou.utils

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowCompat

fun Activity.createImmersiveScene() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}
