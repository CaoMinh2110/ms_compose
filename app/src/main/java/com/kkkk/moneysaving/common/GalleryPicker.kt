package com.kkkk.moneysaving.common

import android.content.Context
import android.content.Intent
import android.provider.MediaStore

object GalleryPicker {
    
    fun openGallery(context: Context) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        context.startActivity(intent)
    }

    fun getGalleryPickerIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }
}
