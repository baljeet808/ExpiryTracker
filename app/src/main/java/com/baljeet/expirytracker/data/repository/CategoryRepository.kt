package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.daos.CategoryDao
import com.baljeet.expirytracker.data.relations.CategoryAndImage

class CategoryRepository(private val categoryDao : CategoryDao){

    val readAllData : LiveData<List<Category>> = categoryDao.readAllCategories()
    val readAllCategoriesWithImages : LiveData<List<CategoryAndImage>> = categoryDao.readAllCategoriesWithImages()

    suspend fun addCategory(c : Category){
        categoryDao.addCategory(c)
    }

    fun readCategoriesByName(name : String): List<CategoryAndImage> {
        return categoryDao.readCategoryByName(name)
    }
}