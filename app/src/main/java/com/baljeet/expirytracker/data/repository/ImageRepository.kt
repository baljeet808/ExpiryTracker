package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.daos.ImageDao

class ImageRepository(private val imageDao : ImageDao) {
    val readAllData : LiveData<List<Image>> = imageDao.readAllImage()

    suspend fun addImage(image : Image){
        imageDao.addImage(image)
    }

    fun getImageById(id : Int): Image {
        return imageDao.getImageById(id)
    }
}