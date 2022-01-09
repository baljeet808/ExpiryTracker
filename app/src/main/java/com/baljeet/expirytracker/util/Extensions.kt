package com.baljeet.expirytracker.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore

@SuppressLint("Range")
fun ContentResolver.getFileName(uri : Uri): String{
    var name = ""
    val cursor = query(uri,null,null,null,null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
    }
    return name
}

fun ContentResolver.getContentType(uri : Uri): String{
    var name = ""
    val cursor = query(uri,null,null,null,null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
    }
    return name
}




