package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application):AndroidViewModel(application) {
    val readAllCategories : LiveData<List<Category>>?
    val readAllCategoriesWithImages : LiveData<List<CategoryAndImage>>
    private val repository : CategoryRepository

    init {
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        readAllCategories = repository.readAllData
        readAllCategoriesWithImages = repository.readAllCategoriesWithImages
    }

    fun addCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCategory(category)
        }
    }

    fun readCategoryByName(name : String): List<CategoryAndImage> {
        return repository.readCategoriesByName(name)
    }
}