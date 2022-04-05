package com.baljeet.expirytracker.data.daos

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.room.*
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.ProductAndImage

@Keep
@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProduct(product : Product)

    @Query("SELECT * FROM products where isDeleted == :isDeleted ORDER BY productId ASC")
    fun readAllProducts(isDeleted: Boolean = false): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId == :id")
    fun getProductByCategoryId(id : Int) : List<Product>

    @Transaction
    @Query("SELECT * FROM products WHERE categoryId == :id")
    fun readProductWithImagesById(id : Int) : List<ProductAndImage>

    @Transaction
    @Query("SELECT * FROM products WHERE name == :name and isDeleted == :isDeleted")
    fun readProductByName(name : String, isDeleted: Boolean = false) : List<ProductAndImage>


    @Transaction
    @Query("SELECT * FROM products WHERE name LIKE :text || '%' and isDeleted == :isDeleted")
    fun searchProductsByText(text : String, isDeleted: Boolean = false) : List<ProductAndImage>

    @Transaction
    @Query("SELECT * FROM products where isDeleted == :isDeleted ORDER BY productId DESC")
    fun getAllProducts(isDeleted: Boolean = false): List<ProductAndImage>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product : Product)

    @Transaction
    @Query("Update products set isDeleted = :isDeleted where productId == :productId")
    fun markDeleted(productId : Int, isDeleted : Boolean)

    @Transaction
    @Query("Update products set isDeleted = :isDeleted where categoryId == :categoryId")
    fun markDeletedByCategoryId(categoryId : Int, isDeleted : Boolean)


}