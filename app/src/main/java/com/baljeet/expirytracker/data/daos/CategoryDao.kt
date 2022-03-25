package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.CategoryAndImage

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(category : Category)

    @Query("SELECT * FROM categories where isDeleted == :isDeleted")
    fun readAllCategories(isDeleted: Boolean = false): LiveData<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories where isDeleted == :isDeleted")
    fun readAllCategoriesWithImages(isDeleted: Boolean = false) : LiveData<List<CategoryAndImage>>


    @Transaction
    @Query("SELECT * FROM categories where categoryName == :name COLLATE NOCASE and isDeleted == :isDeleted")
    fun readCategoryByName(name : String, isDeleted: Boolean = false) : List<CategoryAndImage>

    @Transaction
    @Query("SELECT * FROM categories WHERE categoryName LIKE :text || '%' and isDeleted == :isDeleted")
    fun searchCategoryByWord(text: String, isDeleted: Boolean = false) : List<CategoryAndImage>

    @Transaction
    @Query("SELECT * FROM categories where isDeleted == :isDeleted ORDER BY categoryId DESC")
    fun getAllCategories(isDeleted: Boolean = false): List<CategoryAndImage>


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategory(category : Category)

    @Transaction
    @Query("Update categories set isDeleted = :isDeleted where categoryId == :categoryId")
    fun markDeleted(categoryId : Int, isDeleted : Boolean)
}