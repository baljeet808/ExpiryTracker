package com.baljeet.expirytracker.data.repository

import androidx.annotation.Keep
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.daos.ImageDao

@Keep
class ImageRepository(private val imageDao : ImageDao) {

    fun addImage(image : Image){
        imageDao.addImage(image)
    }

    fun getImageByName(name : String): Image{
        return imageDao.getImageByName(name).first()
    }

    fun getImageById(id : Int): Image{
        return imageDao.getImageById(id)
    }

    fun getImagesByName(name : String): List<Image>{
        return imageDao.getImageByName(name)
    }

    fun getAllImages():List<Image>{
        return imageDao.getAllImages()
    }

    fun searchImagesByText(text : String ): List<Image>{
        return imageDao.searchImagesByText(text)
    }

    fun updateImage(image : Image){
        imageDao.updateImage(image)
    }
}