package com.baljeet.expirytracker.fragment.analytics

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentAnalyticsBinding
import com.baljeet.expirytracker.listAdapters.SummaryDiffAdapter
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.MyColors
import com.google.android.material.chip.Chip
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters.firstDayOfYear
import java.time.temporal.TemporalAdjusters.lastDayOfYear
import java.util.*
import kotlin.math.roundToInt

class Analytics : Fragment() {

    private val viewModel : AnalyticsViewModels by viewModels()
    private val categoryVM: CategoryViewModel by viewModels()
    private val categories = ArrayList<Category>()

    private lateinit var summaryAdapter : SummaryDiffAdapter

    private lateinit var bind : FragmentAnalyticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind  = FragmentAnalyticsBinding.inflate(inflater,container,false)
        bind.apply {

            timePeriodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    dailyRadioButton.id->{
                        viewModel.startDateLive = LocalDateTime.now()
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_DAILY)
                    }
                    weeklyRadioButton.id->{
                        try {
                            var startDate = LocalDateTime.now()
                            var endDate = LocalDateTime.now()
                            while (startDate.dayOfWeek != DayOfWeek.MONDAY) {
                                startDate = startDate.minusDays(1)
                            }
                            while (endDate.dayOfWeek != DayOfWeek.SUNDAY) {
                                endDate = endDate.plusDays(1)
                            }
                            viewModel.startDateLive = startDate
                            viewModel.endDateLive = endDate
                            viewModel.periodFilterLive.postValue(Constants.PERIOD_WEEKLY)
                        }catch (e: Exception){
                        }
                    }
                    monthlyRadioButton.id->{
                        val startDate = LocalDateTime.now().withDayOfMonth(1)
                        val yearMonth = YearMonth.from(LocalDateTime.now())
                        val daysInMonth = yearMonth.lengthOfMonth()
                        val endDate = LocalDateTime.now().withDayOfMonth(daysInMonth)
                        viewModel.startDateLive = startDate
                        viewModel.endDateLive = endDate
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_MONTHLY)
                    }
                    yearlyRadioButton.id->{
                        val yearStartDate = LocalDateTime.now().with(firstDayOfYear())
                        val yearEndDate = LocalDateTime.now().with(lastDayOfYear())
                        viewModel.startDateLive = yearStartDate
                        viewModel.endDateLive = yearEndDate
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_YEARLY)
                    }
                }
            }

            viewModel.allActiveTrackers.observe(viewLifecycleOwner,{
                additionalTrackingInfo1.text = requireContext().getString(R.string.additional_info1,it?.size?:0)
            })

            viewModel.allFinishedTracker.observe(viewLifecycleOwner,{
                additionalTrackingInfo2.text = requireContext().getString(R.string.additional_info2,  it?.size?:0)
            })

            favouriteToggle.apply {
                setOnClickListener {
                    viewModel.favouriteFilter.value?.let { filter ->
                        when (filter) {
                            Constants.SHOW_ALL -> {
                                viewModel.favouriteFilter.postValue(Constants.SHOW_ONLY_FAVOURITE)
                            }
                            Constants.SHOW_ONLY_FAVOURITE -> {
                                viewModel.favouriteFilter.postValue(Constants.SHOW_ONLY_NON_FAVOURITE)
                            }
                            else -> {
                                viewModel.favouriteFilter.postValue(Constants.SHOW_ALL)
                            }
                        }
                    }?: kotlin.run {
                        viewModel.favouriteFilter.postValue(Constants.SHOW_ONLY_FAVOURITE)
                    }
                }
            }
            usedNearExpiryCard.setOnClickListener {
                viewModel.showingGraphFor.postValue(Constants.USED_NEAR_EXPIRY)
            }
            expiredNumCard.setOnClickListener {
                viewModel.showingGraphFor.postValue(Constants.EXPIRED)
            }
            usedFreshCard.setOnClickListener {
                viewModel.showingGraphFor.postValue(Constants.USED_WHEN_FRESH)
            }
            totalTrackedCard.setOnClickListener {
                viewModel.showingGraphFor.postValue(Constants.TOTAL_TRACKED)
            }

            productCategoryChip.apply {
                setOnClickListener {
                    categoryLayout.apply {
                        if (categoryLayout.isGone) {
                            categoryLayout.visibility = View.VISIBLE
                            productCategoryChip.chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.black))
                            productCategoryChip.setTextColor(requireContext().getColor(R.color.main_background))
                        } else {
                            categoryLayout.visibility = View.GONE
                            productCategoryChip.chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
                            productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))
                        }
                    }
                }
                text = viewModel.categoryFilter.value?.categoryName ?: kotlin.run {
                    "Products"
                }
            }
            if(categories.isEmpty()){
                getCategoriesChips()
            }
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            viewModel.favouriteFilter.observe(viewLifecycleOwner, { filter ->
                when (filter) {
                    Constants.SHOW_ALL -> {
                        favouriteToggle.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.ic_star_half_32
                            )
                        )
                    }
                    Constants.SHOW_ONLY_FAVOURITE -> {
                        favouriteToggle.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.ic_round_star_32
                            )
                        )
                    }
                    Constants.SHOW_ONLY_NON_FAVOURITE -> {
                        favouriteToggle.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.ic_star_outline_32
                            )
                        )
                    }
                }
            })

            viewModel.showingGraphFor.observe(viewLifecycleOwner,{
                setGraphValues()
            })

            summaryListView.layoutManager = LinearLayoutManager(requireContext())
            summaryAdapter = SummaryDiffAdapter(requireContext())
            summaryListView.adapter = summaryAdapter

            viewModel.trackersAfterAllFilters.observe(viewLifecycleOwner,{
                    viewModel.calculatedAllFields(it)
                    setGraphValues()
                    summaryAdapter.submitList(it.filter { r -> r.tracker.isUsed })
                })

            viewModel.periodFilterLive.observe(viewLifecycleOwner,{
                  when(it){
                      Constants.PERIOD_DAILY->{
                          monthName.text = requireContext().getString(R.string.date_short_var,
                              viewModel.startDateLive.dayOfMonth,
                              viewModel.startDateLive.month.name.substring(0,3).uppercase()
                          )
                      }
                      Constants.PERIOD_WEEKLY->{
                          monthName.text = requireContext().getString(R.string.week_var,
                              viewModel.startDateLive.dayOfMonth,
                              viewModel.startDateLive.month.name.substring(0,3).uppercase(),
                              viewModel.endDateLive.dayOfMonth,
                              viewModel.endDateLive.month.name.substring(0,3).uppercase()
                          )
                      }
                      Constants.PERIOD_MONTHLY->{
                          monthName.text = requireContext().getString(R.string.month_var,
                                viewModel.startDateLive.month.name.substring(0,3).uppercase(),
                                viewModel.startDateLive.year
                          )
                      }
                      Constants.PERIOD_YEARLY ->{
                          monthName.text = viewModel.startDateLive.year.toString()
                      }
                  }
            })
        }
    }

    private fun setGraphValues(){
        bind.apply {

            val df = DecimalFormat("0.00")

            totalTrackedNum.text = viewModel.totalProductsTracked.toInt().toString()
            usedFreshNum.text = viewModel.totalProductsUsedFresh.toInt().toString()
            usedBeforeExpiryNum.text = viewModel.totalProductsUsedNearExpiry.toInt().toString()
            expiredProductsNum.text = viewModel.totalProductsExpired.toInt().toString()
            freshGraphValue.text = requireContext().getString(R.string.percentage,df.format(viewModel.totalProductsUsedFreshPercentage))
            nearExpiryGraphValue.text = requireContext().getString(R.string.percentage,df.format(viewModel.totalProductsUsedNearExpiryPercentage))
            expiredGraphValue.text = requireContext().getString(R.string.percentage,df.format(viewModel.totalProductsExpiredPercentage))


             when(viewModel.showingGraphFor.value){
                Constants.TOTAL_TRACKED->{
                    okForegroundProgressbar.progress = viewModel.totalProductsUsedNearExpiryPercentage.roundToInt()
                    freshForegroundProgressbar.progress = (viewModel.totalProductsUsedFreshPercentage + viewModel.totalProductsUsedNearExpiryPercentage).roundToInt()
                    expiredForegroundProgressbar.progress = (viewModel.totalProductsUsedFreshPercentage + viewModel.totalProductsUsedNearExpiryPercentage + viewModel.totalProductsExpiredPercentage).roundToInt()
                    expiredForegroundProgressbar.visibility = View.VISIBLE
                    freshForegroundProgressbar.visibility = View.VISIBLE
                    okForegroundProgressbar.visibility = View.VISIBLE

                    freshGraphValue.visibility = View.VISIBLE
                    nearExpiryGraphValue.visibility = View.VISIBLE
                    expiredGraphValue.visibility = View.VISIBLE

                    secondAttributeText.visibility = View.VISIBLE
                    thirdAttributeText.visibility = View.VISIBLE
                    fourthAttributeText.visibility = View.VISIBLE

                    usedNearExpiryCard.isChecked= false
                    expiredNumCard.isChecked= false
                    usedFreshCard.isChecked= false
                    totalTrackedCard.isChecked= true

                }
                Constants.USED_NEAR_EXPIRY->{
                    okForegroundProgressbar.progress = viewModel.totalProductsUsedNearExpiryPercentage.roundToInt()
                    expiredForegroundProgressbar.visibility = View.GONE
                    freshForegroundProgressbar.visibility = View.GONE
                    okForegroundProgressbar.visibility = View.VISIBLE

                    nearExpiryGraphValue.visibility = View.VISIBLE
                    freshGraphValue.visibility = View.INVISIBLE
                    expiredGraphValue.visibility = View.INVISIBLE

                    secondAttributeText.visibility = View.VISIBLE
                    thirdAttributeText.visibility = View.INVISIBLE
                    fourthAttributeText.visibility = View.INVISIBLE

                    usedNearExpiryCard.isChecked= true
                    expiredNumCard.isChecked= false
                    usedFreshCard.isChecked= false
                    totalTrackedCard.isChecked= false
                }
                Constants.USED_WHEN_FRESH->{
                    freshForegroundProgressbar.progress = (viewModel.totalProductsUsedFreshPercentage).roundToInt()
                    expiredForegroundProgressbar.visibility = View.GONE
                    freshForegroundProgressbar.visibility = View.VISIBLE
                    okForegroundProgressbar.visibility = View.GONE

                    nearExpiryGraphValue.visibility = View.INVISIBLE
                    freshGraphValue.visibility = View.VISIBLE
                    expiredGraphValue.visibility = View.INVISIBLE

                    secondAttributeText.visibility = View.INVISIBLE
                    thirdAttributeText.visibility = View.VISIBLE
                    fourthAttributeText.visibility = View.INVISIBLE

                    usedNearExpiryCard.isChecked= false
                    expiredNumCard.isChecked= false
                    usedFreshCard.isChecked= true
                    totalTrackedCard.isChecked= false
                }
                Constants.EXPIRED->{
                    expiredForegroundProgressbar.progress =  viewModel.totalProductsExpiredPercentage.roundToInt()
                    expiredForegroundProgressbar.visibility = View.VISIBLE
                    freshForegroundProgressbar.visibility = View.GONE
                    okForegroundProgressbar.visibility = View.GONE

                    nearExpiryGraphValue.visibility = View.INVISIBLE
                    freshGraphValue.visibility = View.INVISIBLE
                    expiredGraphValue.visibility = View.VISIBLE

                    secondAttributeText.visibility = View.INVISIBLE
                    thirdAttributeText.visibility = View.INVISIBLE
                    fourthAttributeText.visibility = View.VISIBLE

                    usedNearExpiryCard.isChecked= false
                    expiredNumCard.isChecked= true
                    usedFreshCard.isChecked= false
                    totalTrackedCard.isChecked= false
                }
            }
        }
    }

    private fun getCategoriesChips() {
        viewModel.categoryFilter.value?.let {
            bind.productCategoryChip.text = it.categoryName
        }
        categoryVM.readAllCategories?.let {
            it.observe(viewLifecycleOwner, { cats ->
                if (!cats.isNullOrEmpty()) {
                    bind.categoriesChoiceList.apply {
                        categories.clear()
                        categories.addAll(cats)
                        for (category in cats) {
                            val chip = Chip(requireContext())
                            chip.text = category.categoryName
                            chip.id = category.categoryId
                            chip.isCheckedIconVisible = true
                            chip.isChecked =
                                viewModel.categoryFilter.value?.let { cat->
                                    cat.categoryId == category.categoryId
                                } ?: false
                            addView(chip)
                        }
                    }
                }
            })
        }
        bind.categoriesChoiceList.setOnCheckedChangeListener { _, checkedId ->
            val category = categories.firstOrNull { c->c.categoryId == checkedId }
            category?.let {
                bind.productCategoryChip.text = category.categoryName
                viewModel.categoryFilter.postValue(category)
            }?: kotlin.run {
                bind.productCategoryChip.text = resources.getString(R.string.products)
                viewModel.categoryFilter.postValue(Category(0, "Products", 0))
            }

            bind.categoryLayout.visibility = View.GONE
            bind.productCategoryChip.chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
            bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))
        }
    }

}