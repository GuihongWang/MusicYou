package com.kyant.musicyou.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.kyant.musicyou.R

@OptIn(ExperimentalTextApi::class)
object Fonts {
    private val fontProvider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    private val GoogleFont.fontFamily
        get() = FontFamily(
            Font(this, fontProvider, weight = FontWeight.W100),
            Font(this, fontProvider, weight = FontWeight.W200),
            Font(this, fontProvider, weight = FontWeight.W300),
            Font(this, fontProvider, weight = FontWeight.W400),
            Font(this, fontProvider, weight = FontWeight.W500),
            Font(this, fontProvider, weight = FontWeight.W600),
            Font(this, fontProvider, weight = FontWeight.W700),
            Font(this, fontProvider, weight = FontWeight.W800),
            Font(this, fontProvider, weight = FontWeight.W900)
        )
    private val FontFamily.materialTypography
        @Composable
        get() = Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = this),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = this),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = this),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = this),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = this),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = this),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = this),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = this),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = this),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = this),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = this),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = this),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = this),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = this),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = this)
        )

    private val googleSansFont = GoogleFont("Google Sans")
    val googleSansFontFamily = googleSansFont.fontFamily

    private val googleSansTextFont = GoogleFont("Google Sans Text")
    private val googleSansTextFontFamily = googleSansTextFont.fontFamily
    val googleSansTextMaterialTypography
        @Composable
        get() = googleSansTextFontFamily.materialTypography
}
