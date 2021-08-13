package com.svap.chat.utils.extentions

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import com.yalantis.ucrop.util.FileUtils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Uri.getRealPath(context: Context): String {
    when {
        DocumentsContract.isDocumentUri(
            context,
            this
        )
        -> {
            when {
                isExternalStorageDocument(this) -> {
                    val docId = DocumentsContract.getDocumentId(this)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    return "${context.getExternalFilesDir(type)}/" + split[1]
                }
                isDownloadsDocument(this) -> {
                    val id = DocumentsContract.getDocumentId(this)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
                isMediaDocument(this) -> {
                    val docId = DocumentsContract.getDocumentId(this)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }

            }
        }
        "content".equals(scheme, ignoreCase = true) -> {
            // Return the remote address
            return if (isGooglePhotosUri(this)) this.lastPathSegment?:"" else getDataColumn(
                context,
                this,
                null,
                null
            )
        }
        "file".equals(scheme, ignoreCase = true) -> {
            return path?:""
        }
    }
    return ""
}

fun File.toMultiPartFile(partName: String, type: String = "*/*"): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
            partName,
            name,
            this.asRequestBody(type.toMediaTypeOrNull())
    )
}
