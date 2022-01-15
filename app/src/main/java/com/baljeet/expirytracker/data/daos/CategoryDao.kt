package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import io.reactivex.Flowable

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(category : Category)

    @Query("SELECT * FROM categories")
    fun readAllCategories(): LiveData<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun readAllCategoriesWithImages() : LiveData<List<CategoryAndImage>>


    @Transaction
    @Query("SELECT * FROM categories where categoryName == :name COLLATE NOCASE")
    fun readCategoryByName(name : String) : List<CategoryAndImage>

    @Transaction
    @Query("SELECT * FROM categories WHERE categoryName LIKE :text || '%'")
    fun searchCategoryByWord(text: String) : List<CategoryAndImage>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<CategoryAndImage>
}