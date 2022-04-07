package com.baljeet.expirytracker.interfaces

import com.baljeet.expirytracker.data.Image

interface ShowImagePreview {
    fun openImagePreviewFor(image : Image)
}