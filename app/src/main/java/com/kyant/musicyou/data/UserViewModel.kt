package com.kyant.musicyou.data

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.kyant.musicyou.utils.mutableSaveableStateOf
import com.kyant.musicyou.utils.withException
import com.kyant.ncmapi.LoginApi
import com.kyant.ncmapi.UserApi
import com.kyant.ncmapi.data.UserLogin
import com.kyant.ncmapi.data.UserProfile

open class UserViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    var login: UserLogin? by mutableSaveableStateOf("login")
    var profile: UserProfile? by mutableSaveableStateOf("profile")

    suspend fun logIn(
        phone: Long?,
        password: String
    ) {
        context.withException {
            login = LoginApi.phoneNumberLogIn(
                phone = phone,
                password = password
            ).also {
                profile = UserApi.getUserProfile(login = it)
            }
        }
    }

    fun logOut() {
        login = null
        profile = null
    }
}
