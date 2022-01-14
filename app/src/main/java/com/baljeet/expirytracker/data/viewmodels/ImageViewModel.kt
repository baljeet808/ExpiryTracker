package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(application : Application): AndroidViewModel(application) {

    private val readAllImages : LiveData<List<Image>>
    private val repository : ImageRepository

    init {
        val imageDao = AppDatabase.getDatabase(application).imageDao()
        repository = ImageRepository(imageDao)
        readAllImages = repository.readAllData
    }

    fun addImage(image: Image){
            repository.addImage(image)
    }

    fun getImageByName(name : String): List<Image>{
        return repository.getImageByName(name)
    }
}