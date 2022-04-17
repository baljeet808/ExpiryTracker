package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.repository.CategoryRepository
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application):AndroidViewModel(application) {
    val readAllCategories : LiveData<List<Category>>?
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
    fun updateCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }

    fun readCategoryByName(name : String): List<Category> {
        return repository.readCategoriesByName(name)
    }

    val searchResults = MutableLiveData<List<Category>>(ArrayList())

    fun searchCategoryByWord(text : String){
         searchResults.value = repository.searchCategoryByWord(text)
    }

    fun showAllAsResult(){
        searchResults.value =  repository.getAllCategories()
    }

    fun deleteCategory(category : Category){
        repository.markCategoryDeleted(category)
    }
}