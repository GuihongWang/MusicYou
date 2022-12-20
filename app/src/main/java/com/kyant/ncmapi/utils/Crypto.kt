package com.kyant.ncmapi.utils

import io.ktor.util.hex
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

internal fun String.toMD5Hex(): String {
    return hex(MessageDigest.getInstance("MD5").digest(toByteArray()))
}

@Suppress("GetInstance")
internal fun String.encryptToAESHex(): String {
    return Cipher.getInstance("AES/ECB/PKCS5Padding").run {
        init(Cipher.ENCRYPT_MODE, SecretKeySpec("e82ckenh8dichen8".toByteArray(), "AES"))
        hex(doFinal(this@encryptToAESHex.toByteArray()))
    }
}
