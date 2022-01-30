package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.relations.ProductAndImage

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addImage(image : Image)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateImage(image : Image)

    @Query("SELECT * FROM images ORDER BY imageId ASC")
    fun readAllImage(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE imageId == :id")
    fun getImageById(id : Int): Image

    @Query("SELECT * FROM images WHERE imageName == :name")
    fun getImageById(name : String): List<Image>

    @Transaction
    @Query("Select * From images")
    fun getAllImages():List<Image>

    @Transaction
    @Query("SELECT * FROM images WHERE imageName LIKE :text || '%'")
    fun searchImagesByText(text : String) : List<Image>

}