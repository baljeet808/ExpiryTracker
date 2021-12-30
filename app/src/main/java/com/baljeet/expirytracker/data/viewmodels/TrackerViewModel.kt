package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.baljeet.expirytracker.CustomApplication
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
    val context = getApplication<CustomApplication>()

    private val trackerDao = AppDatabase.getDatabase(application).trackerDao()
    private val repository : TrackerRepository =  TrackerRepository(trackerDao)
    private var readAllTracker : LiveData<List<TrackerAndProduct>> = repository.readAllTrackers
    var statusFilter : MutableLiveData<String> = MutableLiveData<String>()
    var categoryFilter : MutableLiveData<Category> = MutableLiveData<Category>()
    var favouriteFilter : MutableLiveData<Int> = MutableLiveData()

    var noTrackerIsActive = false

    var filteredTrackers  = MediatorLiveData<List<TrackerAndProduct>>().apply {
        addSource(readAllTracker){ allTracker->
            noTrackerIsActive = checkIfNoTrackerIsActive(allTracker)
            this.postValue( filterTrackers(favouriteFilter.value?:1,allTracker, statusFilter.value?:Constants.PRODUCT_STATUS_ALL,categoryFilter.value?: Category(0,"Products",0)))
        }
        addSource(statusFilter){ status->
            noTrackerIsActive = checkIfNoTrackerIsActive(readAllTracker.value?:ArrayList())
            this.postValue(filterTrackers(favouriteFilter.value?:1,readAllTracker.value?:ArrayList(), status,categoryFilter.value?:Category(0,"Products",0)))
        }
        addSource(categoryFilter){ category->
            noTrackerIsActive = checkIfNoTrackerIsActive(readAllTracker.value?:ArrayList())
            this.postValue(filterTrackers(favouriteFilter.value?:1,readAllTracker.value?:ArrayList(), statusFilter.value?:Constants.PRODUCT_STATUS_ALL,category))
        }
        addSource(favouriteFilter){ favFilter->
            noTrackerIsActive = checkIfNoTrackerIsActive(readAllTracker.value?:ArrayList())
            this.postValue(filterTrackers(favFilter,readAllTracker.value?:ArrayList(), statusFilter.value?:Constants.PRODUCT_STATUS_ALL,categoryFilter.value?:Category(0,"Products",0)))
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

    private fun filterTrackers(favFilter : Int,allTracker : List<TrackerAndProduct>, status : String, category : Category ): List<TrackerAndProduct>? {
        var afterAllFilters : List<TrackerAndProduct>? = null
       allTracker.let {
            val afterStatusFiltered = if (status == Constants.PRODUCT_STATUS_ALL) {
                it
            } else {
                it.filter { p -> GetStatus.getStatus(p.tracker) == status }
            }

           val afterCategoryFilter = if (category.categoryName == "Products") {
                afterStatusFiltered
            } else {
                afterStatusFiltered.filter { c -> c.productAndCategoryAndImage.categoryAndImage.category.categoryId == category.categoryId }
            }

           afterAllFilters = when (favFilter) {
               Constants.SHOW_ALL -> {
                   afterCategoryFilter
               }
               Constants.SHOW_ONLY_FAVOURITE -> {
                   afterCategoryFilter.filter { f -> f.tracker.isFavourite == true }
               }
               Constants.SHOW_ONLY_NON_FAVOURITE -> {
                   afterCategoryFilter.filter { f -> f.tracker.isFavourite == false }
               }
               else->afterCategoryFilter
           }
        }
        return afterAllFilters
    }

    fun getActiveTrackersLive(): LiveData<List<TrackerAndProduct>>{
        return repository.getActiveTrackersLive()
    }

}