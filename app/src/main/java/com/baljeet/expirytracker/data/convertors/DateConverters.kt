package com.baljeet.expirytracker.data.convertors

import androidx.annotation.Keep
import androidx.room.TypeConverter
import java.time.LocalDateTime


@Keep
class DateConverters {
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}