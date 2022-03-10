package com.baljeet.expirytracker.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.icu.text.NumberFormat
import android.net.Uri
import android.provider.MediaStore
import java.util.*

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

fun Double.getUSCurrencyFormat(): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(this)
}




