package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baljeet.expirytracker.data.Image

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addImage(image : Image)

    @Query("SELECT * FROM images ORDER BY imageId ASC")
    fun readAllImage(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE imageId == :id")
    fun getImageById(id : Int): Image

    @Query("SELECT * FROM images WHERE imageName == :name")
    fun getImageById(name : String): List<Image>
}