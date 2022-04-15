package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application){

    private val readAllData : LiveData<List<Product>>
    private val repository : ProductRepository
    var productsByCategoryWithImage : MutableLiveData<List<ProductAndImage>> = MutableLiveData()


    init {
        val productsDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productsDao)
        readAllData = repository.readAllData
    }

    fun addProduct(product: Product){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProduct(product)
        }
    }



    fun readProductByName(name : String): List<ProductAndImage>{
      return repository.readProductByName(name)
    }
    fun readProductWithImageById(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            productsByCategoryWithImage.postValue(repository.readProductWithImagesById(id))
        }
    }

    val searchResults  = MutableLiveData<List<ProductAndImage>>(ArrayList())

    fun searchByText(text : String ){
        searchResults.value =  repository.searchProductByText(text)
    }

    fun searchByTextInCategory(text : String , categoryId: Int){
        searchResults.value =  repository.searchProductByTextInCategory(text, categoryId)
    }

    fun getAllProductsInCategory(categoryId: Int){
        searchResults.value = repository.getAllProductsInCategory(categoryId)
    }

    fun getAllProducts(){
        searchResults.value = repository.getAllProducts()
    }

    fun updateProduct(product : Product){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product : Product){
        repository.markProductDeleted(product)
    }

    fun deleteAllByCategoryId( categoryId : Int){
        repository.markProductsDeletedByProjectId(categoryId)
    }

}