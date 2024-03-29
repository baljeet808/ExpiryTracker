package com.baljeet.expirytracker.data.convertors

import android.net.Uri
import androidx.annotation.Keep
import androidx.room.TypeConverter

@Keep
class FileTypeConversion {

    @TypeConverter
    fun getStringFromUri(uri : Uri): String{
        return uri.toString()
    }

    @TypeConverter
    fun getUriFromString(value : String):Uri{
        return Uri.parse(value)
    }

}