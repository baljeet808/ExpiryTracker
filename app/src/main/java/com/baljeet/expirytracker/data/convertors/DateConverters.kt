package com.baljeet.expirytracker.data.convertors

import androidx.room.TypeConverter
import com.dwellify.contractorportal.util.TimeConvertor
import kotlinx.datetime.*

class DateConverters {

    @TypeConverter
    fun fromLongToLocalDate(value : Long?): LocalDate?{
        val instant =value?.let {
             TimeConvertor.fromEpochMillisecondsToInstant(it)
        }
        val date = instant?.toLocalDateTime(TimeZone.currentSystemDefault())
        return date?.let {
            LocalDate(it.year,it.month,it.dayOfMonth)
        }
    }

    @TypeConverter
    fun fromLocalDateToLong(date : LocalDate): Long {
        return LocalDateTime(date.year,date.monthNumber,date.dayOfMonth,0,0,0,0).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }
}