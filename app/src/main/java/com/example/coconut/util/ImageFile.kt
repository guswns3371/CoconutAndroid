package com.example.coconut.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


//카메라로 촬영한 이미지 생성
@Throws(IOException::class)
fun Context.createImageFile(): File? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "TEST_" + timeStamp + "_"
    val imageFile: File
    val storageDir =
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (!storageDir!!.exists()) {
        Log.v("알림", "storageDir 존재 x $storageDir")
        storageDir.mkdirs()
    }
    Log.v("알림", "storageDir 존재함 $storageDir")
    imageFile = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",  /* suffix */
        storageDir /* directory */
    )
    return imageFile
}

//이미지 파일 고를때 경로 적절하게 바꿔주기
private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

fun Context.getPath(uri: Uri): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) { // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id)
            )
            return getDataColumn(this, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(this, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(
            uri.scheme,
            ignoreCase = true
        )
    ) { // Return the remote address
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
            this,
            uri,
            null,
            null
        )
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

private fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}


@NonNull
fun prepareFilePart(fieldName: String, fileName: String?): MultipartBody.Part? {
    return fileName?.let {
        val file = File(it)
        val requestFile =
            RequestBody.create(MediaType.parse("multipart/form-data; boundary=$it"), file)
        MultipartBody.Part.createFormData(fieldName, file.name, requestFile)
    }
}

@NonNull
fun createPartFromString(descriptionString: String?): RequestBody? {
    return descriptionString?.let {
        RequestBody.create(MultipartBody.FORM, it)
    }
}