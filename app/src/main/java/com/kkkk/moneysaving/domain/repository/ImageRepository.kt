package com.kkkk.moneysaving.domain.repository

import android.net.Uri

interface ImageRepository {
    suspend fun uploadProfileImage(imageUri: Uri): Result<String>
    suspend fun saveImageToGallery(imageUrl: String): Result<Unit>
}
