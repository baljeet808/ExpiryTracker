package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData

class CategoryRepository(private val categoryDao : CategoryDao){

    val readAllData : LiveData<List<Category>> = categoryDao.readAllCategories()

    suspend fun addCategory(c : Category){
        categoryDao.addCategory(c)
    }
}