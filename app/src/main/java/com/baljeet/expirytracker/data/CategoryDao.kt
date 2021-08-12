package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(category : Category)

    @Query("SELECT * FROM categories ORDER BY categoryId ASC")
    fun readAllCategories(): LiveData<List<Category>>

}