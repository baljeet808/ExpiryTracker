package com.baljeet.expirytracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.relations.ProductAndImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application){

    val readAllData : LiveData<List<Product>>
    private val repository : ProductRepository
    var productsByCategory : MutableLiveData<List<Product>> = MutableLiveData()
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

    fun getProductByCategoryId(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            productsByCategory.postValue(repository.getProductsByCategoryId(id))
        }
    }

    fun readProductWithImageById(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            productsByCategoryWithImage.postValue(repository.readProductWithImagesById(id))
        }
    }
}