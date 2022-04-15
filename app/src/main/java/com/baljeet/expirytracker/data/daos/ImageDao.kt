package com.baljeet.expirytracker.data.daos

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Image

@Keep
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

    @Transaction
    @Query("SELECT * FROM images WHERE imageName LIKE :name || '%'")
    fun getImageByName(name : String): List<Image>

    @Transaction
    @Query("Select * From images WHERE mimeType == :mime")
    fun getAllImages(mime : String = "asset"):List<Image>

    @Transaction
    @Query("SELECT * FROM images WHERE imageName LIKE :text || '%'")
    fun searchImagesByText(text : String) : List<Image>

}