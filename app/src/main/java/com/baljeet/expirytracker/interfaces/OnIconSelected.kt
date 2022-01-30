package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.Image

interface OnIconSelected {
    fun selectThisIcon(image : Image)
}