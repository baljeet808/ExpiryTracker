package com.baljeet.expirytracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.addImage(image)
        }
    }
}