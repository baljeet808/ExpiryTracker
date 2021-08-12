package com.baljeet.expirytracker.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeConvertor {

    //pass Iso date String and get time string in HH:MM AM/PM format back
    fun getTimeFromDateString(date : String, appendAmPm : Boolean) : String?{
        val ldt = convertDateStringToLocalDateTime(date)
        return gettime(ldt?.hour,ldt?.minute,appendAmPm)
    }

    fun getLocalDateTimeFromLong(value: Long?): LocalDateTime {
        return  Instant.fromEpochMilliseconds(value!!).toLocalDateTime(TimeZone.currentSystemDefault())
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

    // get the formatted time back HH:MM AM/PM
    fun gettime (hour: Int?, min : Int?, isSuffixEbaled : Boolean) :String?{
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
                    time+":0"+min.toString()+ if(isSuffixEbaled){" PM"}else{""}
                }else{
                    time+":"+min.toString()+if(isSuffixEbaled){" PM"}else{""}
                }
            }

        }else{
            if (min != null) {
                time = if(min<10){
                    hour.toString()+":0"+min.toString()+if(isSuffixEbaled){" AM"}else{""}
                }else{
                    hour.toString()+":"+min.toString()+if(isSuffixEbaled){" AM"}else{""}
                }
            }
        }
        return time
    }
}