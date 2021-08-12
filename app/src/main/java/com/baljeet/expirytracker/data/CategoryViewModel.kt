package com.baljeet.expirytracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application):AndroidViewModel(application) {
    private val readAllCategories : LiveData<List<Category>>
    private val repository : CategoryRepository

    init {
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        readAllCategories = repository.readAllData
    }

    fun addCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCategory(category)
        }
    }
}