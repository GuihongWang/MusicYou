package com.kyant.ncmapi

import com.kyant.ncmapi.utils.encryptToAESHex
import com.kyant.ncmapi.utils.toMD5Hex
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal suspend fun <T> String.apiGet(
    params: HttpRequestBuilder.() -> Unit = {},
    cookie: String? = null,
    content: suspend (String) -> T
): T {
    val url = Url(this)
    val body = client.get(url) {
        contentType(ContentType.Application.Json)
        header("Cookie", "${cookie}os=pc;appver=2.9.7;")
        params()
    }.bodyAsText()
    try {
        return content(body)
    } catch (e: NullPointerException) {
        val jsonBody = Json.parseToJsonElement(body)
        val code = jsonBody.jsonObject["code"]?.jsonPrimitive?.intOrNull
        val message = jsonBody.jsonObject["message"]?.jsonPrimitive?.contentOrNull
        when {
            message != null -> error(message)
            code != null -> error("error code: $code")
            else -> error(body)
        }
    }
}

internal suspend fun <T> String.apiPost(
    queries: JsonObject? = null,
    cookie: String? = null,
    content: suspend (String) -> T
): T {
    val url = Url(this)
    val path = url.encodedPath.replace("eapi", "api")
    val body = client.post(url) {
        contentType(ContentType.Application.Json)
        header("Cookie", "${cookie}os=pc;appver=2.9.7;")
        parameter(
            "params",
            "$path-36cd479b6b5-$queries-36cd479b6b5-${"nobody${path}use${queries}md5forencrypt".toMD5Hex()}"
                .encryptToAESHex()
        )
    }.bodyAsText()
    try {
        return content(body)
    } catch (e: NullPointerException) {
        val jsonBody = Json.parseToJsonElement(body)
        val code = jsonBody.jsonObject["code"]?.jsonPrimitive?.intOrNull
        val message = jsonBody.jsonObject["message"]?.jsonPrimitive?.contentOrNull
        when {
            message != null -> error(message)
            code != null -> error("error code: $code")
            else -> error(body)
        }
    }
}

internal suspend fun <T> String.apiPostWithCookieReturned(
    queries: JsonObject? = null,
    content: suspend (String, String) -> T
): T {
    val url = Url(this)
    val path = url.encodedPath.replace("eapi", "api")
    val response = client.post(url) {
        contentType(ContentType.Application.Json)
        header("Cookie", "os=pc;appver=2.9.7;")
        parameter(
            "params",
            "$path-36cd479b6b5-$queries-36cd479b6b5-${"nobody${path}use${queries}md5forencrypt".toMD5Hex()}"
                .encryptToAESHex()
        )
    }
    val body = response.bodyAsText()
    try {
        return content(body, response.getCookie())
    } catch (e: NullPointerException) {
        val jsonBody = Json.parseToJsonElement(body)
        val code = jsonBody.jsonObject["code"]?.jsonPrimitive?.intOrNull
        val message = jsonBody.jsonObject["message"]?.jsonPrimitive?.contentOrNull
        when {
            message != null -> error(message)
            code != null -> error("error code: $code")
            else -> error(body)
        }
    }
}

private fun HttpResponse.getCookie(): String {
    val cookie = headers.getAll("Set-Cookie")
    val musicRT = cookie?.find { it.contains("MUSIC_R_T") }?.substringBefore(";")
    val musicU = cookie?.find { it.contains("MUSIC_U") }?.substringBefore(";")
    return "$musicRT; $musicU;"
}
