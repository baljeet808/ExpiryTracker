package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.data.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(application : Application): AndroidViewModel(application) {

    private val readAllImages : LiveData<List<Image>>
    private val repository : ImageRepository

    private var recentlySelectedIcon : Image? = null

    init {
        val imageDao = AppDatabase.getDatabase(application).imageDao()
        repository = ImageRepository(imageDao)
        readAllImages = repository.readAllData
    }

    fun addImage(image: Image){
            repository.addImage(image)
    }

    fun getImageByName(name : String): Image{
        return repository.getImageByName(name)
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