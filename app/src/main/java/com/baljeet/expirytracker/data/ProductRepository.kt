package com.baljeet.expirytracker.data

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.relations.ProductAndImage

class ProductRepository(private val productDao : ProductsDao) {

    val readAllData : LiveData<List<Product>> = productDao.readAllProducts()


    suspend fun addProduct(product : Product){
        productDao.addProduct(product)
    }


    fun readProductWithImagesById(id : Int) : List<ProductAndImage>{
        return productDao.readProductWithImagesById(id)
    }
}