package com.colman.dailypulse.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class Cloudinary () {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val apiKey = "514281681473139"
    private val apiSecret = "bls6UbobOmpUdsiyFDpnGDiTIgA"

    suspend fun uploadImageToCloudinary(
        imageData: ByteArray,
        uploadPreset: String
    ): String? {
        val response: HttpResponse = httpClient.submitFormWithBinaryData(
            url = "https://api.cloudinary.com/v1_1/dgia4gw4w/image/upload",
            formData = formData {
                append("file", imageData, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg") // adjust if needed
                    append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                })
                append("upload_preset", uploadPreset)
                append("api_key", apiKey)
                append("api_secret", apiSecret)
            }
        )

        val json = response.bodyAsText()
        val jsonObj = Json.parseToJsonElement(json).jsonObject
        return jsonObj["secure_url"]?.jsonPrimitive?.content
    }
}