package com.baljeet.expirytracker.fragment.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import kotlinx.datetime.toKotlinLocalDateTime
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
    var allTrackerLive : LiveData<List<TrackerAndProduct>> = repository.getAllTrackers
    var allFinishedTracker : LiveData<List<TrackerAndProduct>> = repository.getAllFinishedTrackers
    var allActiveTrackers : LiveData<List<TrackerAndProduct>> = repository.readAllTrackers

    var endDateLive  : LocalDateTime = getWeekLateDate()
    var startDateLive : LocalDateTime = getWeekFirstDate()
    var periodFilterLive = MutableLiveData<Int>()

    private var trackersAfterPeriodFilter = MediatorLiveData<List<TrackerAndProduct>>().apply {
        addSource(periodFilterLive){
            this.value = filterByTimePeriod(
                allTrackerLive.value?:ArrayList(),
                it,
                startDateLive,
                endDateLive
            )
        }
        addSource(allTrackerLive){
            this.value = filterByTimePeriod(
                it?:ArrayList(),
                periodFilterLive.value?:Constants.PERIOD_WEEKLY,
                startDateLive,
                endDateLive
            )
        }
    }

    var favouriteFilter = MutableLiveData<Int>()
    var categoryFilter = MutableLiveData<Category>()

    var trackersAfterAllFilters = MediatorLiveData<List<TrackerAndProduct>>().apply{
        addSource(favouriteFilter){
            this.value = filterByCategoryAndFavourites(
                trackersAfterPeriodFilter.value?:ArrayList(),
                categoryFilter.value?: Category(0,"Products",0),
                it?:Constants.SHOW_ALL
            )
        }
        addSource(categoryFilter){
            this.value = filterByCategoryAndFavourites(
                trackersAfterPeriodFilter.value?:ArrayList(),
                it?: Category(0,"Products",0),
                favouriteFilter.value?:Constants.SHOW_ALL
            )

        }
        addSource(trackersAfterPeriodFilter){
            this.value = filterByCategoryAndFavourites(
                it?:ArrayList(),
                categoryFilter.value?: Category(0,"Products",0),
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
            t.tracker.usedNearExpiry
        }.size.toDouble()
        totalProductsExpired = trackers.filter { t ->
            t.tracker.gotExpired
        }.size.toDouble()
        totalProductsUsedFresh = trackers.filter { t ->
            t.tracker.usedWhileFresh || t.tracker.usedWhileOk
        }.size.toDouble()

        if(totalProductsTracked != 0.0) {
            totalProductsUsedNearExpiryPercentage = ((totalProductsUsedNearExpiry/ totalProductsTracked) * 100.0)
            totalProductsExpiredPercentage = ((totalProductsExpired / totalProductsTracked) * 100.0)
            totalProductsUsedFreshPercentage = ((totalProductsUsedFresh / totalProductsTracked) * 100.0)
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

    private fun filterByCategoryAndFavourites(trackers : List<TrackerAndProduct>, category : Category, favFilter : Int): List<TrackerAndProduct>{
        return when(favFilter){
            Constants.SHOW_ALL->{
                trackers
            }
            Constants.SHOW_ONLY_FAVOURITE->{
                trackers.filter { t -> t.productAndCategoryAndImage.product.categoryId == category.categoryId && t.tracker.isFavourite }
            }
            Constants.SHOW_ONLY_NON_FAVOURITE->{
                trackers.filter { t -> t.productAndCategoryAndImage.product.categoryId == category.categoryId && !t.tracker.isFavourite }
            }
            else-> ArrayList()
        }
    }


    private fun filterByTimePeriod(trackers : List<TrackerAndProduct>, periodFilter : Int,startDate : LocalDateTime, endDate : LocalDateTime) : List<TrackerAndProduct>{
        return  when(periodFilter){
            Constants.PERIOD_DAILY->{
                trackers.filter { t -> t.tracker.expiryDate.date == startDate.toKotlinLocalDateTime().date }
            }
            else->{
                trackers.filter { t -> t.tracker.expiryDate.date >= startDate.toKotlinLocalDateTime().date && t.tracker.expiryDate.date <= endDate.toKotlinLocalDateTime().date }
            }
        }
    }
}