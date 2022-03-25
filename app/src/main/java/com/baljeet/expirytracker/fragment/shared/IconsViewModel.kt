package com.baljeet.expirytracker.fragment.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.repository.ImageRepository

class IconsViewModel(app : Application) : AndroidViewModel(app) {

    private val repository : ImageRepository
    var readAllData  = MutableLiveData<ArrayList<Image>>(ArrayList())

    init {
        val imageDao = AppDatabase.getDatabase(app).imageDao()
        repository = ImageRepository(imageDao)
    }

    var selectedIcon : Image? = null

    fun getAllIcons(){
        readAllData.value = repository.getAllImages().toCollection(ArrayList())
    }

    fun getIconByName(name : String){
        readAllData.value = repository.getImagesByName(name).toCollection(ArrayList())
    }

}