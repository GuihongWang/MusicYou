package com.kyant.ncmapi.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun String.obj(key: String): JsonObject =
    Json.parseToJsonElement(this).jsonObject[key]!!.jsonObject

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun String.array(key: String): JsonArray =
    Json.parseToJsonElement(this).jsonObject[key]!!.jsonArray

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun String.content(key: String): String =
    Json.parseToJsonElement(this).jsonObject[key]!!.jsonPrimitive.content

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun JsonElement.obj(key: String): JsonObject = (this as JsonObject)[key] as JsonObject

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun JsonElement.array(key: String): JsonArray = jsonObject[key]!!.jsonArray

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun JsonElement.content(key: String): String = jsonObject[key]!!.jsonPrimitive.content

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun JsonElement.int(key: String): Int = jsonObject[key]!!.jsonPrimitive.int

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun JsonElement.long(key: String): Long = jsonObject[key]!!.jsonPrimitive.long

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun JsonElement.arrayContentList(key: String): List<String> = (this array key).map {
    it.jsonPrimitive.content
}
