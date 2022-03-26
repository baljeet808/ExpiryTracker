package com.baljeet.expirytracker.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.icu.text.NumberFormat
import android.net.Uri
import android.provider.MediaStore
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
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






