package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.relations.CategoryAndImage

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(category : Category)

    @Query("SELECT * FROM categories")
    fun readAllCategories(): LiveData<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun readAllCategoriesWithImages() : LiveData<List<CategoryAndImage>>
}