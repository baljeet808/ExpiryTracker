package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.relations.CategoryAndImage

interface OnCategorySelected {
    fun openInfoOfCategory(categoryAndImage : CategoryAndImage)
}