package com.baljeet.expirytracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application){

    private val readAllData : LiveData<List<Product>>
    private val repository : ProductRepository

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
}