package com.baljeet.expirytracker.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageConvertor {

    @Suppress("DEPRECATION")
    fun bitMapToString(bitmap: Bitmap?, format : String): String? {
        return bitmap?.let {
            try {
                val bios = ByteArrayOutputStream()
                if(format.contains("png",true)){
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bios)
                }else if(format.contains("jpeg",true) || format.contains("jpg",true)){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bios)
                }
                else if(format.contains("webp",true)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS,90,bios)
                    }else{
                        bitmap.compress(Bitmap.CompressFormat.WEBP,90,bios)
                    }
                }else{
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bios)
                }
                val b = bios.toByteArray()
                Base64.encodeToString(b, Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun stringToBitmap(imageString : String): Bitmap{
        Base64.decode(imageString,Base64.DEFAULT).apply {
            return BitmapFactory.decodeByteArray(this,0,size)
        }
    }

    fun uriToBitmap(uri: Uri, context: Context): Bitmap {
        val source =  ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source)
    }

}