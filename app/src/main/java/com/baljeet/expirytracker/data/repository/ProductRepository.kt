package com.baljeet.expirytracker.data.repository

import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Category
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

    fun searchProductByText(text : String ): List<ProductAndImage>{
        return productDao.searchProductsByText(text)
    }

    fun getAllProducts():List<ProductAndImage>{
        return productDao.getAllProducts()
    }

    suspend fun updateProduct(product : Product){
        productDao.updateProduct(product)
    }

    fun markProductDeleted(product: Product){
        productDao.markDeleted(product.productId, true)
    }

    fun markProductsDeletedByProjectId(categoryId : Int){
        productDao.markDeletedByCategoryId(categoryId, true)
    }
}