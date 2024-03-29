package com.baljeet.expirytracker.data.repository

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.daos.CategoryDao
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import io.reactivex.Flowable

@Keep
class CategoryRepository(private val categoryDao : CategoryDao){

    val readAllData : LiveData<List<Category>> = categoryDao.readAllCategories()

    suspend fun addCategory(c : Category){
        categoryDao.addCategory(c)
    }

    fun readCategoriesByName(name : String): List<Category> {
        return categoryDao.readCategoryByName(name)
    }

    fun searchCategoryByWord(text: String): List<Category> {
            return categoryDao.searchCategoryByWord(text)
    }

    fun getAllCategories(): List<Category>{
        return categoryDao.getAllCategories()
    }

    suspend fun updateCategory(category : Category){
        categoryDao.updateCategory(category)
    }

    fun markCategoryDeleted(category : Category){
        categoryDao.markDeleted(category.categoryId, true)
    }
}