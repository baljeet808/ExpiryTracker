package com.baljeet.expirytracker.data.relations

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.Tracker
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class TrackerAndProduct(
    @Embedded val tracker : Tracker,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId",
        entity = Product::class
    )
    val productAndCategoryAndImage: ProductAndCategoryAndImage
):Parcelable
