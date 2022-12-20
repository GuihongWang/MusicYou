package com.kyant.musicyou.screens.main

import android.app.AlertDialog
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyant.monet.a1
import com.kyant.monet.n1
import com.kyant.monet.n2
import com.kyant.monet.withNight
import com.kyant.musicyou.SettingsActivity
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.data.UserViewModel
import com.kyant.musicyou.theme.Fonts
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.musicyou.ui.TextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppViewModel.MenuDialog() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(95.n2 withNight 10.n1)
            .pointerInput(Unit) {
                detectTapGestures {
                    isMenuOpen = false
                }
            }
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AnimatedContent(targetState = login == null) {
            if (it) {
                LoginCard()
            } else {
                ProfileCard()
            }
        }
        Menu()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UserViewModel.LoginCard() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    var phone by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

    fun logIn() {
        scope.launch(Dispatchers.IO) {
            keyboardController?.hide()
            logIn(
                phone = phone.text.toLongOrNull(),
                password = password.text
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(SmoothRoundedCornerShape(32.dp, 0.5f))
            .background(99.n1 withNight 20.n1)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "网易云音乐账户",
            modifier = Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        TextField(
            value = phone,
            onValueChange = { phone = it },
            placeholderText = "手机号码",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholderText = "密码",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { logIn() }),
            visualTransformation = PasswordVisualTransformation()
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(85.a1 withNight 90.a1)
                .clickable { logIn() }
                .padding(16.dp, 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "登录",
                color = 0.n1
            )
        }
    }
}

@Composable
private fun UserViewModel.ProfileCard() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(SmoothRoundedCornerShape(32.dp, 0.5f))
            .background(99.n1 withNight 20.n1)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile?.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .aspectRatio(1f)
                .fillMaxSize()
                .clip(CircleShape)
        )
        Text(
            text = profile?.nickname ?: "",
            fontWeight = FontWeight.Bold,
            fontFamily = Fonts.googleSansFontFamily,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.headlineLarge
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(95.n1 withNight 30.n1)
                .clickable {
                    AlertDialog.Builder(context)
                        .setTitle("确定要退出登录？")
                        .setMessage("确定要退出登录？")
                        .setPositiveButton("退出登录") { _, _ -> logOut() }
                        .setNegativeButton("取消", null)
                        .show()
                }
                .padding(16.dp, 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "退出登录")
        }
    }
}

@Composable
private fun Menu() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(SmoothRoundedCornerShape(32.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Surface(
            shape = SmoothRoundedCornerShape(4.dp),
            color = 96.n1 withNight 20.n1,
            contentColor = 10.n1 withNight 95.n1
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { context.startActivity(Intent(context, SettingsActivity::class.java)) }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null
                )
                Text(
                    text = "账户",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Surface(
            shape = SmoothRoundedCornerShape(4.dp),
            color = 97.n1 withNight 22.n1,
            contentColor = 10.n1 withNight 95.n1
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { context.startActivity(Intent(context, SettingsActivity::class.java)) }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null
                )
                Text(
                    text = "设置",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Surface(
            shape = SmoothRoundedCornerShape(4.dp),
            color = 98.n1 withNight 24.n1,
            contentColor = 10.n1 withNight 95.n1
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { context.startActivity(Intent(context, SettingsActivity::class.java)) }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
                Text(
                    text = "关于应用",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
