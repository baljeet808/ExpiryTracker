package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProduct(product : Product)

    @Query("SELECT * FROM products ORDER BY productId ASC")
    fun readAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId == :id")
    fun getProductByCategoryId(id : Int) : List<Product>


}