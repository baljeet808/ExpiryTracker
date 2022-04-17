package com.baljeet.expirytracker.data.repository


import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.daos.ProductsDao

@Keep
class ProductRepository(private val productDao : ProductsDao) {

    val readAllData : LiveData<List<Product>> = productDao.readAllProducts()


    suspend fun addProduct(product : Product){
        productDao.addProduct(product)
    }

    fun readProductByName(name : String): List<Product>{
        return productDao.readProductByName(name)
    }

    fun readProductWithImagesById(id : Int) : List<Product>{
        return productDao.readProductWithImagesById(id)
    }

    fun searchProductByText(text : String ): List<Product>{
        return productDao.searchProductsByText(text)
    }

    fun searchProductByTextInCategory(text : String , categoryId: Int): List<Product>{
        return productDao.searchProductByTextInCategory(text, categoryId)
    }

    fun getAllProductsInCategory(categoryId: Int):List<Product>{
        return productDao.getAllProductsInCategory(categoryId)
    }

    fun getAllProducts():List<Product>{
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