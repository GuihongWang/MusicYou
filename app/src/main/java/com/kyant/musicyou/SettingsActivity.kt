package com.kyant.musicyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.SettingScreens
import com.kyant.musicyou.theme.MusicYouTheme
import com.kyant.musicyou.utils.appViewModels
import com.kyant.musicyou.utils.createImmersiveScene

class SettingsActivity : ComponentActivity() {
    private val viewModel: AppViewModel by appViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createImmersiveScene()
        setContent {
            with(viewModel) {
                MusicYouTheme {
                    SettingScreens()
                }
            }
        }
    }
}
