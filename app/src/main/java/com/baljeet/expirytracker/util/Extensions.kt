package com.baljeet.expirytracker.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.drawable.LayerDrawable
import android.icu.text.NumberFormat
import android.net.Uri
import android.provider.MediaStore
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

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

fun ArrayList<TrackerAndProduct>.getGroupedListByCategories():ArrayList<TrackerByCategories> {
    val list = ArrayList<TrackerByCategories>()

    val categories = ArrayList<Category>()
    for(tracker in this){
        if(!categories.contains(tracker.productAndCategoryAndImage.categoryAndImage.category)){
           categories.add(tracker.productAndCategoryAndImage.categoryAndImage.category)
        }
    }

    for(category in categories){
        if(this.filter { t->t.productAndCategoryAndImage.categoryAndImage.category.categoryId == category.categoryId }.filter { t->t.tracker.isUsed }.isNotEmpty()) {
            list.add(
                TrackerByCategories(
                    categoryName = category.categoryName,
                    trackers = this.filter { t -> (t.productAndCategoryAndImage.categoryAndImage.category.categoryId == category.categoryId) && t.tracker.isUsed }
                )
            )
        }
    }
    return list
}

data class TrackerByCategories(
    var categoryName : String,
    var trackers : List<TrackerAndProduct>
)

enum class ActiveTrackingStatus{
    FRESH,NEAR_EXPIRY,STILL_OK,EXPIRED
}

fun Tracker.getTrackingStatus():ActiveTrackingStatus{
    val expiryDate = this.expiryDate
    val mfgDate = this.mfgDate

    val dateToday = LocalDateTime.now()

    val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
    val spentHours = Duration.between(mfgDate,dateToday).toMinutes()

    val progressValue = (spentHours.toFloat() / totalHours.toFloat()) * 100

      return  when {
            progressValue >= 100->{
                ActiveTrackingStatus.EXPIRED
            }
            progressValue >= 80 -> {
                ActiveTrackingStatus.NEAR_EXPIRY
            }
            progressValue < 80 && progressValue >= 50 -> {
                ActiveTrackingStatus.STILL_OK
            }
            progressValue < 50 -> {
                ActiveTrackingStatus.FRESH
            }
          else -> ActiveTrackingStatus.EXPIRED
      }
}






