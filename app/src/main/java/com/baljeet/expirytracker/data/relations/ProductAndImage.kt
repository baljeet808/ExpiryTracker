package com.baljeet.expirytracker.data.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductAndImage(
    @Embedded val product : Product,
    @Relation(
        parentColumn = "imageId",
        entityColumn = "imageId"
    )
    val image: Image
):Parcelable
