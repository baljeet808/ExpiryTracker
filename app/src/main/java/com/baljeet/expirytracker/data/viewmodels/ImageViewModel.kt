package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(application : Application): AndroidViewModel(application) {

    private val readAllImages : LiveData<List<Image>>
    private val repository : ImageRepository
    var imageById :MutableLiveData<Image> = MutableLiveData()

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

    fun getImageById(id : Int){
        imageById.postValue(repository.getImageById(id))
    }
}