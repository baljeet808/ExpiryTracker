package com.dwellify.contractorportal.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate

object TimeConvertor {

    //pass Iso date String and get time string in HH:MM AM/PM format back
    fun getTimeFromDateString(date : String, appendAmPm : Boolean) : String?{
        val ldt = convertDateStringToLocalDateTime(date)
        return getTime(ldt?.hour,ldt?.minute,appendAmPm)
    }

    //pass Iso date String and get kotlinx LocalDateTime object back
    fun convertDateStringToLocalDateTime(date : String): LocalDateTime?{
        return try{
            with(date){
                if(this.isNotEmpty()){
                    Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
                }else{
                    null
                }
            }
        }catch (e : Exception){
            null
        }
    }

    fun fromEpochMillisecondsToLocalDateTime(date : Long?): LocalDateTime?{
        return try{
            date?.let {
                Instant.fromEpochMilliseconds(date).toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }catch (e : Exception){
            null
        }
    }

    fun fromEpochMillisecondsToInstant(date : Long) : Instant{
        return Instant.fromEpochMilliseconds(date)
    }

    fun convertStringToJavaLocalDate(date : String): LocalDate{
        val mDate = convertDateStringToLocalDateTime(date)
        return LocalDate.of(mDate!!.year, mDate.month, mDate.dayOfMonth)
    }

    // get the formatted time back HH:MM AM/PM
    fun getTime (hour: Int?, min : Int?, isSuffixEnabled : Boolean) :String?{
        var time: String? = null
        if(hour in 12..23){
            if (hour != null) {
                time = if(hour == 12){
                    (hour).toString()
                }else{
                    (hour-12).toString()
                }
            }
            if (min != null) {
                time = if(min<10){
                    time+":0"+min.toString()+ if(isSuffixEnabled){" PM"}else{""}
                }else{
                    time+":"+min.toString()+if(isSuffixEnabled){" PM"}else{""}
                }
            }

        }else{
            if (min != null) {
                time = if(min<10){
                    hour.toString()+":0"+min.toString()+if(isSuffixEnabled){" AM"}else{""}
                }else{
                    hour.toString()+":"+min.toString()+if(isSuffixEnabled){" AM"}else{""}
                }
            }
        }
        return time
    }



}