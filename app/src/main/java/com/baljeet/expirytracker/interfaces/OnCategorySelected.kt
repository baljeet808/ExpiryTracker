package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.Category

interface OnCategorySelected {
    fun openInfoOfCategory(category : Category)
}