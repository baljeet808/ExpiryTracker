package com.baljeet.expirytracker.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.CategoryAndImage
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

    @Transaction
    @Query("SELECT * FROM products WHERE name == :name")
    fun readProductByName(name : String) : List<ProductAndImage>


    @Transaction
    @Query("SELECT * FROM products WHERE name LIKE :text || '%'")
    fun searchProductsByText(text : String) : List<ProductAndImage>

    @Transaction
    @Query("SELECT * FROM products ORDER BY productId DESC")
    fun getAllProducts(): List<ProductAndImage>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product : Product)

}