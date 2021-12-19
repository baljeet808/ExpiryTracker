package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.GetStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application){

    var readAllTracker : LiveData<List<TrackerAndProduct>>

    var filteredProducts = ArrayList<TrackerAndProduct>()

    var statusFilter : String
    var categoryFilter : Category? = null

    private val repository : TrackerRepository

    init {
        val trackerDao = AppDatabase.getDatabase(application).trackerDao()
        repository = TrackerRepository(trackerDao)
        readAllTracker = repository.readAllTrackers
        statusFilter  = Constants.PRODUCT_STATUS_ALL
        categoryFilter = Category(0,"Products",0)
    }

    fun addTracker(tracker : Tracker){
        viewModelScope.launch(Dispatchers.IO){
            repository.addTracker(tracker)
        }
    }

    fun updateTracker(tracker: Tracker){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateTracker(tracker)
        }
    }

    fun filterTrackers(): ArrayList<TrackerAndProduct> {

        readAllTracker = repository.readAllTrackers
        var afterCategoryFilter : List<TrackerAndProduct>? = null
        readAllTracker.value?.let {
            filteredProducts.clear()
            val afterStatusFiltered = if (statusFilter == Constants.PRODUCT_STATUS_ALL) {
                it
            } else {
                it.filter { p -> GetStatus.getStatus(p.tracker) == statusFilter }
            }

             afterCategoryFilter = if (categoryFilter?.categoryName == "Products") {
                afterStatusFiltered
            } else {
                afterStatusFiltered.filter { c -> c.productAndCategoryAndImage.categoryAndImage.category.categoryId == categoryFilter?.categoryId }
            }
        }
         afterCategoryFilter?.let {
            filteredProducts.addAll(it)

        }?: kotlin.run {
            filteredProducts.clear()
        }
        return filteredProducts
    }

}