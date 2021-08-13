package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addImage(image : Image)

    @Query("SELECT * FROM images ORDER BY imageId ASC")
    fun readAllImage(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE imageId == :id")
    fun getImageById(id : Int): Image
}