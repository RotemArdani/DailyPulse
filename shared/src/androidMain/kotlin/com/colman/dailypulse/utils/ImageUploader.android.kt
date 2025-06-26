package com.colman.dailypulse.utils

import co.touchlab.kermit.Logger
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class ImageUploader {
    private var log = Logger.withTag("ImageUploader")

    actual suspend fun uploadImage(imageBytes: ByteArray): String {
        return suspendCancellableCoroutine { continuation ->
            val uploadRequest = MediaManager.get().upload(imageBytes)
                .unsigned( "dailypulse")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = (bytes * 100) / totalBytes
//                        println("Cloudinary Android Upload Progress: $progress%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(IllegalStateException("Cloudinary upload success but no secure_url found in result: $resultData"))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo?) {
                        log.e("Error uploading, ${error?.description ?: error}")
                        val errorMessage = error?.description ?: "Unknown Cloudinary upload error"
                        continuation.resumeWithException(RuntimeException("Cloudinary Android Upload Failed: $errorMessage"))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        // Optional: Log reschedule
                        println("Cloudinary Android Upload Rescheduled: $requestId, Error: ${error?.description}")
                    }
                })

            uploadRequest.dispatch() // Start the upload

            // If the coroutine is cancelled, try to cancel the upload
            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(uploadRequest.requestId)
                println("Cloudinary Android Upload Cancelled: ${uploadRequest.requestId}")
            }
        }
    }
}