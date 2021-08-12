package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData

class ProductRepository(private val productDao : ProductsDao) {

    val readAllData : LiveData<List<Product>> = productDao.readAllProducts()

    suspend fun addProduct(product : Product){
        productDao.addProduct(product)
    }
}