package com.baljeet.expirytracker.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application){

    val readAllTracker : LiveData<List<TrackerAndProduct>>?
    private val repository : TrackerRepository
    var trackerById : MutableLiveData<TrackerAndProduct> = MutableLiveData()

    init {
        val trackerDao = AppDatabase.getDatabase(application).trackerDao()
        repository = TrackerRepository(trackerDao)
        readAllTracker = repository.readAllTrackers
    }

    fun addTracker(tracker : Tracker){
        viewModelScope.launch(Dispatchers.IO){
            repository.addTracker(tracker)
        }
    }

    fun getTrackerById(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            trackerById.postValue(repository.readTrackerById(id))
        }
    }

}