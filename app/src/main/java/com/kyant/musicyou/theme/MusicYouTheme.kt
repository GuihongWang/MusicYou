package com.kyant.musicyou.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.kyant.monet.Hct.Companion.toHct
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.kyant.monet.n1
import com.kyant.monet.toColor
import com.kyant.monet.toSrgb

@Composable
fun MusicYouTheme(
    color: Color? = null,
    content: @Composable () -> Unit
) {
    val baseColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        colorResource(id = android.R.color.system_accent1_500)
    } else Color(0xFF007FAC)
    val keyColor = color ?: baseColor
    val keyColorHct = keyColor.toSrgb().toHct()
    MaterialTheme(typography = Fonts.googleSansTextMaterialTypography) {
        CompositionLocalProvider(
            LocalTonalPalettes provides keyColor.toSrgb().toColor().toTonalPalettes(
                style = if (color != null) when {
                    keyColorHct.c >= 32.0 -> PaletteStyle.Vibrant
                    keyColorHct.c >= 24.0 && keyColorHct.t >= 20.0 -> PaletteStyle.TonalSpot
                    else -> PaletteStyle.Content
                } else PaletteStyle.TonalSpot
            ),
            LocalContentColor provides if (isSystemInDarkTheme()) 100.n1 else 0.n1
        ) {
            content()
        }
    }
}
