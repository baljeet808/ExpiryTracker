package com.baljeet.expirytracker.fragment.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.model.DayWithProducts
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.GetStatus
import com.baljeet.expirytracker.util.SharedPref
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewModel(app: Application) : AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()
    var selectedCategory = Category(0, "Products", 0)
    private val repository: TrackerRepository

    init {
        SharedPref.init(context)
        val trackerDao = AppDatabase.getDatabase(app).trackerDao()
        repository = TrackerRepository(trackerDao)
    }

    val trackersInDay = MutableLiveData<ArrayList<TrackerAndProduct>>()

    var favouriteFilter : MutableLiveData<Int> = MutableLiveData()

    var noTrackerIsActive = false

    var filteredTrackers  = MediatorLiveData<List<TrackerAndProduct>>().apply {
        addSource(trackersInDay){ trackers->
            noTrackerIsActive = checkIfNoTrackerIsActive(trackers?:ArrayList())
            this.postValue(filterTrackers(favouriteFilter.value?:1,trackers))
        }
        addSource(favouriteFilter){ favFilter->
            noTrackerIsActive = checkIfNoTrackerIsActive(trackersInDay.value?:ArrayList())
            this.postValue(filterTrackers(favFilter,trackersInDay.value?:ArrayList()))
        }
    }
    private fun checkIfNoTrackerIsActive(trackers : List<TrackerAndProduct>): Boolean{
        return trackers.isNullOrEmpty()
    }

    private fun filterTrackers(favFilter : Int,allTracker : List<TrackerAndProduct>): List<TrackerAndProduct>? {
        var afterAllFilters : List<TrackerAndProduct>? = null
        allTracker.let {
            afterAllFilters = when (favFilter) {
                Constants.SHOW_ALL -> {
                    it
                }
                Constants.SHOW_ONLY_FAVOURITE -> {
                    it.filter { f -> f.tracker.isFavourite == true }
                }
                Constants.SHOW_ONLY_NON_FAVOURITE -> {
                    it.filter { f -> f.tracker.isFavourite == false }
                }
                else->it
            }
        }
        return afterAllFilters
    }


    private var selectedDate = java.time.LocalDateTime.now()
    private var currentDate = java.time.LocalDateTime.now()
    var selectedDayOfMonth : LocalDateTime? = null
    var selectedDayIndex = 0

    fun setNextMonth(){
        selectedDate = selectedDate.plusMonths(1)
    }
    fun setPreviousMonth(){
        selectedDate = selectedDate.minusMonths(1)
    }



    fun getMonth(): ArrayList<DayWithProducts> {
        val daysInMonthArray = ArrayList<DayWithProducts>()
        val yearMonth = YearMonth.from(selectedDate)

        val daysInMonth = yearMonth.lengthOfMonth()

        val firstOfMonth = selectedDate.withDayOfMonth(1)
        var dayOfWeek = firstOfMonth.dayOfWeek.value
        dayOfWeek = if(dayOfWeek == 7){
            0
        }else{
            dayOfWeek
        }


        var dateCounter = 1
        for (i in 0..(daysInMonth + (dayOfWeek-1))) {
            if (i < dayOfWeek || dateCounter > daysInMonth) {
                daysInMonthArray.add(DayWithProducts(
                    date =null,
                    isCurrentDate = false,
                    isSelected = false,
                    products = null
                ))
            } else {
                val date = LocalDateTime(selectedDate.year,selectedDate.monthValue,dateCounter,0,0,0,0)
                val filteredProducts = ArrayList<TrackerAndProduct>()
                val products = repository.readTrackerByExpiryDate(date)
                val trackerProducts = if (selectedCategory.categoryName != "Products") {
                    products.filter { t -> t.productAndCategoryAndImage.categoryAndImage.category.categoryId == selectedCategory.categoryId }
                } else {
                    products
                }
                filteredProducts.addAll(trackerProducts)
                val isCurrentDate = date.date == currentDate.toKotlinLocalDateTime().date
                val isSelected = selectedDayOfMonth?.let {
                    it.date == date.date
                }?: kotlin.run {
                    if(isCurrentDate){
                        selectedDayOfMonth = date
                        selectedDayIndex = i
                    }
                    isCurrentDate
                }
                daysInMonthArray.add(DayWithProducts(date, isCurrentDate ,isSelected, filteredProducts))
                dateCounter++
            }
        }
        return daysInMonthArray
    }

    fun monthYearTextFromDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return selectedDate.format(formatter)
    }
    fun dayYearTextFromDate(givenDate : java.time.LocalDateTime): String?{
        val formatter = DateTimeFormatter.ofPattern("d MMMM")
        return givenDate.format(formatter)
    }
}

