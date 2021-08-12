package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData

class ProductRepository(private val productDao : ProductsDao) {

    val readAllData : LiveData<List<Product>> = productDao.readAllProducts()

    private lateinit var  productByCategoryId : LiveData<List<Product>>

    suspend fun addProduct(product : Product){
        productDao.addProduct(product)
    }

    fun getProductsByCategoryId(id : Int): List<Product>{
       return  productDao.getProductByCategoryId(id)
    }
}