package com.kyant.ncmapi

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 5000
    }
    install(ContentNegotiation) {
        json(
            Json {
                isLenient = true
            }
        )
    }
}
