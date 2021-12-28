package com.baljeet.expirytracker.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image

data class CategoryAndImage(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "imageId",
        entityColumn = "imageId"
    )
    val image: Image
)