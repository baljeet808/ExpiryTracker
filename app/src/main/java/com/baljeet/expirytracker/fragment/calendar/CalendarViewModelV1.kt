package com.baljeet.expirytracker.fragment.calendar

import android.app.Application
import androidx.lifecycle.*
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.model.DayWithProducts
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewModelV1(app : Application) : AndroidViewModel(app) {

    val context = getApplication<CustomApplication>()
    private val repository: TrackerRepository

    init {
        SharedPref.init(context)
        val trackerDao = AppDatabase.getDatabase(app).trackerDao()
        repository = TrackerRepository(trackerDao)
    }

    private var selectedDateLive = MutableLiveData(LocalDateTime.now())
    val selectedDayOfMonth = MutableLiveData<kotlinx.datetime.LocalDateTime>()
    private var allTrackerLive : LiveData<List<TrackerAndProduct>> = repository.readAllTrackers
    var favouriteFilter = MutableLiveData<Int>()
    var categoryFilter = MutableLiveData(Category(0, "Products", 0,false))
    private var noTrackerIsActive = false

    private var filteredTrackers = MediatorLiveData<List<TrackerAndProduct>>().apply {
        addSource(allTrackerLive){ allTrackers->
            noTrackerIsActive = checkIfNoTrackerIsActive(allTrackers?:ArrayList())
            this.value =filterTrackers(
                favFilter = favouriteFilter.value?:Constants.SHOW_ALL,
                allTracker = allTrackers,
                category = categoryFilter.value?:Category(0,"Products",0,false),
                monthDate = selectedDateLive.value?: LocalDateTime.now()
            )
        }
        addSource(selectedDateLive){ monthDate->
            noTrackerIsActive = checkIfNoTrackerIsActive(allTrackerLive.value?:ArrayList())
            this.value =filterTrackers(
                favFilter = favouriteFilter.value?:Constants.SHOW_ALL,
                allTracker = allTrackerLive.value?:ArrayList(),
                category = categoryFilter.value?:Category(0,"Products",0,false),
                monthDate = monthDate?: LocalDateTime.now()
            )
        }
        addSource(favouriteFilter){ favouriteState->
            noTrackerIsActive = checkIfNoTrackerIsActive(allTrackerLive.value?:ArrayList())
            this.value =filterTrackers(
                favFilter = favouriteState?:Constants.SHOW_ALL,
                allTracker = allTrackerLive.value?:ArrayList(),
                category = categoryFilter.value?:Category(0,"Products",0,false),
                monthDate = selectedDateLive.value?: LocalDateTime.now()
            )
        }
        addSource(categoryFilter){ category->
            noTrackerIsActive = checkIfNoTrackerIsActive(allTrackerLive.value?:ArrayList())
            this.value =filterTrackers(
                favFilter = favouriteFilter.value?:Constants.SHOW_ALL,
                allTracker = allTrackerLive.value?:ArrayList(),
                category = category?:Category(0,"Products",0,false),
                monthDate = selectedDateLive.value?: LocalDateTime.now()
            )
        }
    }



    var trackersForCalendar = MediatorLiveData<ArrayList<DayWithProducts>>().apply {
        addSource(filteredTrackers){ trackers->
            this.value = getMonth(selectedDateLive.value?: LocalDateTime.now(),trackers)
        }
        addSource(selectedDayOfMonth){
            this.value = getMonth(selectedDateLive.value?: LocalDateTime.now(),filteredTrackers.value?:ArrayList())
        }
    }

    private fun filterForDay(selectedDay : kotlinx.datetime.LocalDateTime, allTracker : List<TrackerAndProduct>): List<TrackerAndProduct>{
        return allTracker.filter { d-> d.tracker.expiryDate!!.dayOfMonth == selectedDay.dayOfMonth }
    }

    var trackersForRecycler = MediatorLiveData<List<TrackerAndProduct>>().apply {
        addSource(filteredTrackers){
            this.value = filterForDay(selectedDayOfMonth.value?:LocalDateTime.now().toKotlinLocalDateTime(),it)
        }
        addSource(selectedDayOfMonth){
            this.value = filterForDay(it,filteredTrackers.value?:ArrayList())
        }
    }


    private fun checkIfNoTrackerIsActive(trackers : List<TrackerAndProduct>): Boolean{
        return trackers.isNullOrEmpty()
    }

    private fun filterTrackers(favFilter : Int,allTracker : List<TrackerAndProduct>, category: Category, monthDate : LocalDateTime ): List<TrackerAndProduct>? {
        var afterAllFilters : List<TrackerAndProduct>? = null
        allTracker.let {
            val afterMonthFilter = it.filter { m -> m.tracker.expiryDate!!.year == monthDate.year && m.tracker.expiryDate!!.month == monthDate.month }

            val afterCategoryFilter = if (category.categoryName == "Products") {
                afterMonthFilter
            } else {
                afterMonthFilter.filter { c -> c.productAndCategoryAndImage.categoryAndImage.category.categoryId == category.categoryId }
            }

            afterAllFilters = when (favFilter) {
                Constants.SHOW_ALL -> {
                    afterCategoryFilter
                }
                Constants.SHOW_ONLY_FAVOURITE -> {
                    afterCategoryFilter.filter { f -> f.tracker.isFavourite }
                }
                Constants.SHOW_ONLY_NON_FAVOURITE -> {
                    afterCategoryFilter.filter { f -> !f.tracker.isFavourite }
                }
                else->afterCategoryFilter
            }
        }
        return afterAllFilters
    }


   private fun getMonth(selectedDate : LocalDateTime, allTrackers : List<TrackerAndProduct>): ArrayList<DayWithProducts> {
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
                val date = kotlinx.datetime.LocalDateTime(selectedDate.year,selectedDate.monthValue,dateCounter,0,0,0,0)
                val products = allTrackers.filter { t-> t.tracker.expiryDate!!.dayOfMonth == date.dayOfMonth }

                val isCurrentDate = date.date == LocalDateTime.now().toKotlinLocalDateTime().date
                val isSelected = selectedDayOfMonth.value?.let {
                    it.date == date.date
                }?: kotlin.run {
                    if(isCurrentDate){
                        selectedDayOfMonth.postValue(date)
                    }
                    isCurrentDate
                }
                daysInMonthArray.add(DayWithProducts(date, isCurrentDate ,isSelected, products.toCollection(ArrayList())))
                dateCounter++
            }
        }
        return daysInMonthArray
    }

    fun setNextMonth(){
        selectedDateLive.postValue(selectedDateLive.value?.plusMonths(1))
    }
    fun setPreviousMonth(){
        selectedDateLive.postValue(selectedDateLive.value?.minusMonths(1))
    }

    fun monthYearTextFromDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return selectedDateLive.value?.format(formatter)
    }
    fun dayYearTextFromDate(): String?{
        val formatter = DateTimeFormatter.ofPattern("d MMMM")
        return selectedDayOfMonth.value?.toJavaLocalDateTime()?.format(formatter)
    }

    fun updateTracker(tracker: Tracker){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateTracker(tracker)
        }
    }

}