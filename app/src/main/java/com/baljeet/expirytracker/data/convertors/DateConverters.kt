package com.baljeet.expirytracker.data.convertors

import androidx.room.TypeConverter
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DateConverters {

    @TypeConverter
    fun fromLongToLocalDateTime(value : Long?): LocalDateTime?{
        return value?.let {
            TimeConvertor.fromEpochMillisecondsToInstant(it).toLocalDateTime(TimeZone.UTC)
        }
    }

    @TypeConverter
    fun fromLocalDateTimeToLong(date : LocalDateTime): Long {
        return date.toInstant(TimeZone.UTC).toEpochMilliseconds()
    }
}