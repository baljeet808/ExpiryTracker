package com.baljeet.expirytracker.fragment.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import java.time.DayOfWeek
import java.time.LocalDateTime

class AnalyticsViewModels(app : Application): AndroidViewModel(app) {
    val context = getApplication<CustomApplication>()
    private val repository: TrackerRepository

    init {
        SharedPref.init(context)
        val trackerDao = AppDatabase.getDatabase(app).trackerDao()
        repository = TrackerRepository(trackerDao)
    }
    private var allTrackerLive : LiveData<List<TrackerAndProduct>> = repository.getAllTrackers

    var endDate  : LocalDateTime = getWeekLateDate()
    var startDate : LocalDateTime = getWeekFirstDate()
    var periodFilterLive = MutableLiveData<Int>()

    private var trackersAfterPeriodFilter = MediatorLiveData<List<TrackerAndProduct>>().apply {
        addSource(periodFilterLive){
            this.value = filterByTimePeriod(
                allTrackerLive.value?:ArrayList(),
                it,
                startDate,
                endDate
            )
        }
        addSource(allTrackerLive){
            this.value = filterByTimePeriod(
                it?:ArrayList(),
                periodFilterLive.value?:Constants.PERIOD_WEEKLY,
                startDate,
                endDate
            )
        }
    }

    var favouriteFilter = MutableLiveData<Int>()

    var trackersAfterAllFilters = MediatorLiveData<List<TrackerAndProduct>>().apply{
        addSource(favouriteFilter){
            this.value = filterByFavourites(
                trackersAfterPeriodFilter.value?:ArrayList(),
                it?:Constants.SHOW_ALL
            )
        }
        addSource(trackersAfterPeriodFilter){
            this.value = filterByFavourites(
                it?:ArrayList(),
                favouriteFilter.value?:Constants.SHOW_ALL
            )
        }
    }


    var totalProductsTracked  = 0.0
    var totalProductsUsedNearExpiry  = 0.0
    var totalProductsUsedNearExpiryPercentage  = 0.0
    var totalProductsExpired  = 0.0
    var totalProductsExpiredPercentage  = 0.0
    var totalProductsUsedFresh  = 0.0
    var totalProductsUsedFreshPercentage  = 0.0

    fun calculatedAllFields(trackers : List<TrackerAndProduct>){
        totalProductsTracked = trackers.filter { t ->
            t.tracker.isUsed
        }.size.toDouble()
        totalProductsUsedNearExpiry = trackers.filter { t ->
            t.tracker.usedNearExpiry || t.tracker.usedWhileOk
        }.size.toDouble()
        totalProductsExpired = trackers.filter { t ->
            t.tracker.gotExpired
        }.size.toDouble()
        totalProductsUsedFresh = trackers.filter { t ->
            t.tracker.usedWhileFresh
        }.size.toDouble()

        if(totalProductsTracked != 0.0) {
            totalProductsUsedNearExpiryPercentage = ((totalProductsUsedNearExpiry/ totalProductsTracked) * 100.0)
            totalProductsExpiredPercentage = ((totalProductsExpired / totalProductsTracked) * 100.0)
            totalProductsUsedFreshPercentage = ((totalProductsUsedFresh / totalProductsTracked) * 100.0)
        }else{
            totalProductsUsedNearExpiryPercentage = 0.0
            totalProductsExpiredPercentage = 0.0
            totalProductsUsedFreshPercentage = 0.0
        }
    }

    var showingGraphFor = MutableLiveData(Constants.TOTAL_TRACKED)


    private fun getWeekFirstDate(): LocalDateTime {
        var startDate = LocalDateTime.now()

        while(startDate.dayOfWeek != DayOfWeek.MONDAY){
            startDate = startDate.minusDays(1)
        }
        return startDate
    }

    private fun getWeekLateDate(): LocalDateTime {
        var endDate = LocalDateTime.now()
        while(endDate.dayOfWeek != DayOfWeek.SUNDAY){
            endDate = endDate.plusDays(1)
        }
        return endDate
    }

    private fun filterByFavourites(trackers : List<TrackerAndProduct>, favFilter : Int): List<TrackerAndProduct>{
        return when(favFilter){
            Constants.SHOW_ALL->{
                trackers
            }
            Constants.SHOW_ONLY_FAVOURITE->{
                trackers.filter { t ->  t.tracker.isFavourite }
            }
            Constants.SHOW_ONLY_NON_FAVOURITE->{
                trackers.filter { t -> !t.tracker.isFavourite }
            }
            else-> ArrayList()
        }
    }


    private fun filterByTimePeriod(trackers : List<TrackerAndProduct>, periodFilter : Int,startDate : LocalDateTime, endDate : LocalDateTime) : List<TrackerAndProduct>{
        return  when(periodFilter){
            Constants.PERIOD_DAILY->{
                trackers.filter { t ->
                    t.tracker.expiryDate!!.year == startDate.year &&
                            t.tracker.expiryDate!!.month == startDate.month &&
                            t.tracker.expiryDate!!.dayOfMonth == startDate.dayOfMonth
                }
            }
            else->{
                trackers.filter { t ->
                    (t.tracker.expiryDate!!.toLocalDate().isAfter(startDate.toLocalDate()) || t.tracker.expiryDate!!.toLocalDate() == startDate.toLocalDate())
                            &&
                            (t.tracker.expiryDate!!.toLocalDate().isBefore(endDate.toLocalDate()) || t.tracker.expiryDate!!.toLocalDate() == endDate.toLocalDate())
                }
            }
        }
    }
}