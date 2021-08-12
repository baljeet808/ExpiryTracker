package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData

class ImageRepository(private val imageDao : ImageDao) {
    val readAllData : LiveData<List<Image>> = imageDao.readAllImage()

    suspend fun addImage(image : Image){
        imageDao.addImage(image)
    }
}