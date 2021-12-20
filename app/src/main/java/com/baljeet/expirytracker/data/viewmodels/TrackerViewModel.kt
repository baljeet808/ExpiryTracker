package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.*
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
    private val trackerDao = AppDatabase.getDatabase(application).trackerDao()
    private val repository : TrackerRepository =  TrackerRepository(trackerDao)
    private var readAllTracker : LiveData<List<TrackerAndProduct>> = repository.readAllTrackers
    var statusFilter : MutableLiveData<String> = MutableLiveData(Constants.PRODUCT_STATUS_ALL)
    var categoryFilter : MutableLiveData<Category> = MutableLiveData(Category(0,"Products",0))

    var noTrackerIsActive = true

    var filteredTrackers  = MediatorLiveData<List<TrackerAndProduct>>().apply {
        this.value = ArrayList()
        addSource(readAllTracker){ allTracker->
            noTrackerIsActive = checkIfNoTrackerIsActive(allTracker)
           this.postValue( filterTrackers(allTracker, statusFilter.value?:Constants.PRODUCT_STATUS_ALL,categoryFilter.value?: Category(0,"Products",0)))
        }
        addSource(statusFilter){ status->
            noTrackerIsActive = checkIfNoTrackerIsActive(readAllTracker.value?:ArrayList())
            this.postValue(filterTrackers(readAllTracker.value?:ArrayList(), status,categoryFilter.value?:Category(0,"Products",0)))
        }
        addSource(categoryFilter){ category->
            noTrackerIsActive = checkIfNoTrackerIsActive(readAllTracker.value?:ArrayList())
            this.postValue(filterTrackers(readAllTracker.value?:ArrayList(), statusFilter.value?:Constants.PRODUCT_STATUS_ALL,category))
        }
    }
    private fun checkIfNoTrackerIsActive(trackers : List<TrackerAndProduct>): Boolean{
        return trackers.isNullOrEmpty()
    }

    fun addTracker(newTracker : Tracker){
        viewModelScope.launch(Dispatchers.IO){
            repository.addTracker(newTracker)
        }
    }

    fun updateTracker(tracker: Tracker){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateTracker(tracker)
        }
    }

    private fun filterTrackers(allTracker : List<TrackerAndProduct>, status : String, category : Category ): List<TrackerAndProduct>? {
        var afterCategoryFilter : List<TrackerAndProduct>? = null
       allTracker.let {
            val afterStatusFiltered = if (status == Constants.PRODUCT_STATUS_ALL) {
                it
            } else {
                it.filter { p -> GetStatus.getStatus(p.tracker) == status }
            }

           afterCategoryFilter = if (category.categoryName == "Products") {
                afterStatusFiltered
            } else {
                afterStatusFiltered.filter { c -> c.productAndCategoryAndImage.categoryAndImage.category.categoryId == category.categoryId }
            }
        }
        return afterCategoryFilter
    }

}