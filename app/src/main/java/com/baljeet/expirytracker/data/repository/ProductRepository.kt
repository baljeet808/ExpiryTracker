package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.daos.ProductsDao
import com.baljeet.expirytracker.data.relations.ProductAndImage

class ProductRepository(private val productDao : ProductsDao) {

    val readAllData : LiveData<List<Product>> = productDao.readAllProducts()


    suspend fun addProduct(product : Product){
        productDao.addProduct(product)
    }

    fun readProductByName(name : String): List<ProductAndImage>{
        return productDao.readProductByName(name)
    }

    fun readProductWithImagesById(id : Int) : List<ProductAndImage>{
        return productDao.readProductWithImagesById(id)
    }
}