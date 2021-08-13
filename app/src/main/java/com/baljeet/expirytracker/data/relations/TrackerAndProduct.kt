package com.baljeet.expirytracker.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.Tracker

data class TrackerAndProduct(
    @Embedded val tracker : Tracker,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId",
        entity = Product::class
    )
    val productAndCategoryAndImage: ProductAndCategoryAndImage
)
