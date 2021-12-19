package com.baljeet.expirytracker.fragment.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.baljeet.expirytracker.CustomApplication
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.repository.TrackerRepository
import com.baljeet.expirytracker.model.DayWithProducts
import com.baljeet.expirytracker.util.SharedPref
import kotlinx.datetime.LocalDateTime
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


    private var selectedDate = java.time.LocalDateTime.now()


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
                daysInMonthArray.add(DayWithProducts(null, null))
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
                daysInMonthArray.add(DayWithProducts(date, filteredProducts))
                dateCounter++
            }
        }
        return daysInMonthArray
    }

    fun monthYearTextFromDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return selectedDate.format(formatter)
    }
}

