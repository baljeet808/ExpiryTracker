package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.repository.ImageRepository

class ImageViewModel(application : Application): AndroidViewModel(application) {

    private val repository : ImageRepository

    private var recentlySelectedIcon : Image? = null

    init {
        val imageDao = AppDatabase.getDatabase(application).imageDao()
        repository = ImageRepository(imageDao)
    }

    fun addImage(image: Image){
            repository.addImage(image)
    }

    fun getImageByName(name : String): Image{
        return repository.getImageByName(name)
    }

    fun getImageById(id : Int): Image{
        return repository.getImageById(id)
    }


    val searchResults  = MutableLiveData<List<Image>>(ArrayList())

    fun searchByText(text : String ){
        searchResults.value =  repository.searchImagesByText(text)
    }

    fun updateImage(image : Image){
        repository.updateImage(image)
    }

    fun getAllProducts(){
        searchResults.value = repository.getAllImages()
    }
}