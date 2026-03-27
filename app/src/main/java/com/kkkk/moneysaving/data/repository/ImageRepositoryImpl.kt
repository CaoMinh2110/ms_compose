package com.kkkk.moneysaving.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.kkkk.moneysaving.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "ImageRepository"
private const val PROFILE_IMAGES_PATH = "profile_images"

class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
) : ImageRepository {

    override suspend fun uploadProfileImage(imageUri: Uri): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser == null) {
                    return@withContext Result.failure(Exception("User not authenticated"))
                }

                val uid = currentUser.uid
                val timeStamp = System.currentTimeMillis()
                val fileName = "profile_${uid}_${timeStamp}"

                val storageReference = firebaseStorage
                    .reference
                    .child(PROFILE_IMAGES_PATH)
                    .child(fileName)

                Log.d(TAG, "Uploading image to Firebase Storage: $fileName")

                storageReference.putFile(imageUri).await()

                val downloadUrl = storageReference.downloadUrl.await()
                val urlString = downloadUrl.toString()

                Log.d(TAG, "Image uploaded successfully: $urlString")
                Result.success(urlString)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload image", e)
                Result.failure(e)
            }
        }

    override suspend fun saveImageToGallery(imageUrl: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(imageUrl) ?: return@withContext Result.failure(
                    Exception("Failed to download image")
                )

                val filename = "MoneySaving_${System.currentTimeMillis()}.jpg"
                var outputStream: OutputStream? = null
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                if (imageUri != null) {
                    outputStream = context.contentResolver.openOutputStream(imageUri)
                    outputStream?.use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        context.contentResolver.update(imageUri, contentValues, null, null)
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to create MediaStore entry"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save image to gallery", e)
                Result.failure(e)
            }
        }

    private fun downloadBitmap(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading bitmap", e)
            null
        }
    }
}
