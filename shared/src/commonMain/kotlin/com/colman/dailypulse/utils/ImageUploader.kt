package com.colman.dailypulse.utils

expect class ImageUploader() {
    suspend fun uploadImage(imageBytes: ByteArray): String
}
