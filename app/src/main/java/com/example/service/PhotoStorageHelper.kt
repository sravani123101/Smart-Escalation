package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoStorageHelper {

    private const val PHOTO_DIR_NAME = "dose_photos"

    fun getPhotoDirectory(context: Context): File {
        val dir = File(context.filesDir, PHOTO_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Creates a temporary URI for the camera capture intent using FileProvider
     */
    fun createTempCameraUri(context: Context): Uri {
        val cacheDir = File(context.cacheDir, PHOTO_DIR_NAME)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val tempFile = File.createTempFile(
            "camera_capture_",
            ".jpg",
            cacheDir
        )
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, tempFile)
    }

    /**
     * Saves a photo from a source Uri or Bitmap into app-private storage,
     * compressing it to avoid wasting storage space.
     * Returns the absolute path of the saved private file.
     */
    fun savePhotoPrivately(context: Context, sourceUri: Uri, reminderId: Long): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null
            saveBitmapPrivately(context, originalBitmap, reminderId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapPrivately(context: Context, bitmap: Bitmap, reminderId: Long): String? {
        return try {
            val dir = getPhotoDirectory(context)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val targetFile = File(dir, "dose_${reminderId}_${timeStamp}.jpg")

            // Scale down if dimensions are huge
            val maxDimension = 1200
            val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val targetW: Int
                val targetH: Int
                if (ratio > 1) {
                    targetW = maxDimension
                    targetH = (maxDimension / ratio).toInt()
                } else {
                    targetH = maxDimension
                    targetW = (maxDimension * ratio).toInt()
                }
                Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
            } else {
                bitmap
            }

            val fos = FileOutputStream(targetFile)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            fos.flush()
            fos.close()

            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deletePhoto(filePath: String?): Boolean {
        if (filePath.isNullOrBlank()) return false
        return try {
            val file = File(filePath)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }

    fun clearAllPrivatePhotos(context: Context) {
        try {
            val dir = getPhotoDirectory(context)
            dir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
