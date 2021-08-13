package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.relations.ProductAndImage

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProduct(product : Product)

    @Query("SELECT * FROM products ORDER BY productId ASC")
    fun readAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId == :id")
    fun getProductByCategoryId(id : Int) : List<Product>

    @Transaction
    @Query("SELECT * FROM products WHERE categoryId == :id")
    fun readProductWithImagesById(id : Int) : List<ProductAndImage>

}