package com.baljeet.expirytracker.fragment.analytics

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentAnalyticsBinding
import com.baljeet.expirytracker.fragment.dash.DashFragmentDirections
import com.baljeet.expirytracker.interfaces.ShowImagePreview
import com.baljeet.expirytracker.listAdapters.SummaryDiffAdapter
import com.baljeet.expirytracker.model.*
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.MyColors
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.chip.Chip
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters.firstDayOfYear
import java.time.temporal.TemporalAdjusters.lastDayOfYear
import kotlin.math.roundToInt

class Analytics : Fragment(), ShowImagePreview {

    private var mRewardedAd: RewardedAd? = null


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

             SharedPref.init(requireContext())
            if(SharedPref.isUserAPro){
                buyProButton.isGone = true
                adIcon.isGone = true
            }
            else{
                val adRequest = AdRequest.Builder().build()
                RewardedAd.load(requireContext(),Constants.PDF_REWARDED_AD_ID, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        mRewardedAd = rewardedAd

                    }
                })
            }

            
            downloadPdfButton.setOnClickListener {
                if(true){
                    prepPDFRequest()
                }else{
                    if (mRewardedAd != null) {
                        mRewardedAd?.show(requireActivity()) {
                            prepPDFRequest()
                        }
                    } else {
                        Log.d("MainActivity", "The rewarded ad wasn't ready yet.")
                    }
                }
            }

            val sDate = LocalDateTime.now().withDayOfMonth(1)
            val yMonth = YearMonth.from(LocalDateTime.now())
            val dInMonth = yMonth.lengthOfMonth()
            val eDate = LocalDateTime.now().withDayOfMonth(dInMonth)
            viewModel.startDate = sDate
            viewModel.endDate = eDate
            viewModel.periodFilterLive.postValue(Constants.PERIOD_MONTHLY)

            timePeriodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    dailyRadioButton.id->{
                        viewModel.startDate = LocalDateTime.now()
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_DAILY)
                    }
                    weeklyRadioButton.id->{
                        try {
                            var start = LocalDateTime.now()
                            var end = LocalDateTime.now()
                            while (start.dayOfWeek != DayOfWeek.MONDAY) {
                                start = start.minusDays(1)
                            }
                            while (end.dayOfWeek != DayOfWeek.SUNDAY) {
                                end = end.plusDays(1)
                            }
                            viewModel.startDate = start
                            viewModel.endDate = end
                            viewModel.periodFilterLive.postValue(Constants.PERIOD_WEEKLY)
                        }catch (e: Exception){
                        }
                    }
                    monthlyRadioButton.id->{
                        val start = LocalDateTime.now().withDayOfMonth(1)
                        val mYearMonth = YearMonth.from(LocalDateTime.now())
                        val mDaysInMonth = mYearMonth.lengthOfMonth()
                        val end = LocalDateTime.now().withDayOfMonth(mDaysInMonth)
                        viewModel.startDate = start
                        viewModel.endDate = end
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_MONTHLY)
                    }
                    yearlyRadioButton.id->{
                        val yearStartDate = LocalDateTime.now().with(firstDayOfYear())
                        val yearEndDate = LocalDateTime.now().with(lastDayOfYear())
                        viewModel.startDate = yearStartDate
                        viewModel.endDate = yearEndDate
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_YEARLY)
                    }
                }
            }

            nextButton.setOnClickListener {
                 when(timePeriodRadioGroup.checkedRadioButtonId){
                     dailyRadioButton.id->{
                         viewModel.startDate = viewModel.startDate.plusDays(1)
                         viewModel.periodFilterLive.postValue(Constants.PERIOD_DAILY)
                     }
                     weeklyRadioButton.id->{
                         var start = viewModel.endDate.plusDays(1)
                         var end = viewModel.endDate.plusDays(1)
                         while (start.dayOfWeek != DayOfWeek.MONDAY) {
                             start = start.minusDays(1)
                         }
                         while (end.dayOfWeek != DayOfWeek.SUNDAY) {
                             end = end.plusDays(1)
                         }
                         viewModel.startDate = start
                         viewModel.endDate = end
                         viewModel.periodFilterLive.postValue(Constants.PERIOD_WEEKLY)
                     }
                     monthlyRadioButton.id->{
                         val start = viewModel.startDate.plusMonths(1).withDayOfMonth(1)
                         val mYearMonth = YearMonth.from(viewModel.startDate.plusMonths(1))
                         val mDaysInMonth = mYearMonth.lengthOfMonth()
                         val end = viewModel.startDate.plusMonths(1).withDayOfMonth(mDaysInMonth)
                         viewModel.startDate = start
                         viewModel.endDate = end
                         viewModel.periodFilterLive.postValue(Constants.PERIOD_MONTHLY)
                     }
                     yearlyRadioButton.id->{
                         val yearStartDate = viewModel.startDate.plusYears(1).with(firstDayOfYear())
                         val yearEndDate = viewModel.startDate.plusYears(1).with(lastDayOfYear())
                         viewModel.startDate = yearStartDate
                         viewModel.endDate = yearEndDate
                         viewModel.periodFilterLive.postValue(Constants.PERIOD_YEARLY)
                     }
                 }
            }

            previousButton.setOnClickListener {
                when(timePeriodRadioGroup.checkedRadioButtonId){
                    dailyRadioButton.id->{
                        viewModel.startDate = viewModel.startDate.minusDays(1)
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_DAILY)
                    }
                    weeklyRadioButton.id->{
                        var start = viewModel.startDate.minusDays(1)
                        var end = viewModel.startDate.minusDays(1)
                        while (start.dayOfWeek != DayOfWeek.MONDAY) {
                            start = start.minusDays(1)
                        }
                        while (end.dayOfWeek != DayOfWeek.SUNDAY) {
                            end = end.plusDays(1)
                        }
                        viewModel.startDate = start
                        viewModel.endDate = end
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_WEEKLY)
                    }
                    monthlyRadioButton.id->{
                        val start = viewModel.startDate.minusMonths(1).withDayOfMonth(1)
                        val mYearMonth = YearMonth.from(viewModel.startDate.minusMonths(1))
                        val mDaysInMonth = mYearMonth.lengthOfMonth()
                        val end = viewModel.startDate.minusMonths(1).withDayOfMonth(mDaysInMonth)
                        viewModel.startDate = start
                        viewModel.endDate = end
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_MONTHLY)
                    }
                    yearlyRadioButton.id->{
                        val yearStartDate = viewModel.startDate.minusYears(1).with(firstDayOfYear())
                        val yearEndDate = viewModel.startDate.minusYears(1).with(lastDayOfYear())
                        viewModel.startDate = yearStartDate
                        viewModel.endDate = yearEndDate
                        viewModel.periodFilterLive.postValue(Constants.PERIOD_YEARLY)
                    }
                }
            }

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
                        } else {
                            categoryLayout.visibility = View.GONE
                            productCategoryChip.chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
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

            buyProButton.setOnClickListener {
                 Navigation.findNavController(requireView()).navigate(AnalyticsDirections.actionAnalyticsToBePro())
            }
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            viewModel.favouriteFilter.observe(viewLifecycleOwner) { filter ->
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
            }

            viewModel.showingGraphFor.observe(viewLifecycleOwner) {
                setGraphValues()
            }

            summaryListView.layoutManager = LinearLayoutManager(requireContext())
            summaryAdapter = SummaryDiffAdapter(requireContext(), this@Analytics)
            summaryListView.adapter = summaryAdapter

            viewModel.trackersAfterAllFilters.observe(viewLifecycleOwner) {
                viewModel.calculatedAllFields(it)
                setGraphValues()
                it?.let {
                    noItemLayout.isGone = it.any { r -> r.tracker.isUsed }
                    summaryAdapter.submitList(it.filter { r -> r.tracker.isUsed })
                }
            }

            viewModel.periodFilterLive.observe(viewLifecycleOwner) {
                when (it) {
                    Constants.PERIOD_DAILY -> {
                        monthName.text = requireContext().getString(
                            R.string.date_short_var,
                            viewModel.startDate.dayOfMonth,
                            viewModel.startDate.month.name.substring(0, 3).uppercase()
                        )
                    }
                    Constants.PERIOD_WEEKLY -> {
                        monthName.text = requireContext().getString(
                            R.string.week_var,
                            viewModel.startDate.dayOfMonth,
                            viewModel.startDate.month.name.substring(0, 3).uppercase(),
                            viewModel.endDate.dayOfMonth,
                            viewModel.endDate.month.name.substring(0, 3).uppercase()
                        )
                    }
                    Constants.PERIOD_MONTHLY -> {
                        monthName.text = requireContext().getString(
                            R.string.month_var,
                            viewModel.startDate.month.name.substring(0, 3).uppercase(),
                            viewModel.startDate.year
                        )
                    }
                    Constants.PERIOD_YEARLY -> {
                        monthName.text = viewModel.startDate.year.toString()
                    }
                }
            }

            if(SharedPref.isUserAPro){
                buyProButton.isGone = true
                adIcon.isGone = true
            }
        }
    }

    private fun prepPDFRequest(){
        viewModel.trackersAfterAllFilters.value?.let {
            if(it.isNotEmpty()){
                val request = RequestPDF(
                    trackers =it.toCollection(ArrayList()),
                    periodStartDate =  viewModel.startDate,
                    periodEndDate = viewModel.endDate,
                    totalTracked =  viewModel.totalProductsTracked.toInt(),
                    totalFresh = viewModel.totalProductsUsedFresh.toInt(),
                    totalNearExpiry = viewModel.totalProductsUsedNearExpiry.toInt(),
                    totalExpired = viewModel.totalProductsExpired.toInt(),
                    resultCase = when {
                        viewModel.totalProductsExpired.toInt() > (viewModel.totalProductsUsedFresh.toInt() + viewModel.totalProductsUsedNearExpiry.toInt()) -> {
                            ResultCase.MORE_EXPIRED
                        }
                        viewModel.totalProductsUsedNearExpiry.toInt()> viewModel.totalProductsUsedFresh.toInt() -> {
                            ResultCase.NEED_TO_IMPROVE
                        }
                        viewModel.totalProductsExpired.toInt() + viewModel.totalProductsUsedNearExpiry.toInt() < viewModel.totalProductsUsedFresh -> {
                            ResultCase.GOOD_JOB
                        }
                        else -> {
                            ResultCase.KEEP_IT_UP
                        }
                    },
                    periodType = when(viewModel.periodFilterLive.value){
                        Constants.PERIOD_DAILY->{
                            PeriodType.DAILY
                        }
                        Constants.PERIOD_WEEKLY->{
                            PeriodType.WEEKLY
                        }
                        Constants.PERIOD_MONTHLY->{
                            PeriodType.MONTHLY
                        }
                        else->{
                            PeriodType.YEARLY
                        }
                    },
                    groupBy = GroupBy.RESULTS,
                    useOfImages = UseImages.ON,
                    textColor = SelectedTextColor.BLACK,
                    backgroundColor = BackgroundColor.WHITE
                )
                moveToPdfPreview(request)
            }else{
                Toast.makeText(requireContext(),"Not enough data to generate a report.", Toast.LENGTH_SHORT).show()
            }
        }?: kotlin.run {
            Toast.makeText(requireContext(),"Not enough data to generate a report.", Toast.LENGTH_SHORT).show()
        }
    }




    private fun moveToPdfPreview(request : RequestPDF){
        Navigation.findNavController(requireView()).navigate(AnalyticsDirections.actionAnalyticsToPdfPreview(request))
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
            it.observe(viewLifecycleOwner) { cats ->
                if (!cats.isNullOrEmpty()) {
                    bind.categoriesChoiceList.apply {
                        categories.clear()
                        categories.add(Category(0, "Products", 0,false))
                        categories.addAll(cats)
                        for (category in categories) {
                            val chip = Chip(requireContext())
                            chip.text = category.categoryName
                            chip.id = category.categoryId
                            chip.isCheckedIconVisible = true
                            chip.isChecked =
                                viewModel.categoryFilter.value?.let { cat ->
                                    cat.categoryId == category.categoryId
                                } ?: false
                            addView(chip)
                        }
                    }
                }
            }
        }
        bind.categoriesChoiceList.setOnCheckedChangeListener { _, checkedId ->
            val category = categories.firstOrNull { c->c.categoryId == checkedId }
            category?.let {
                bind.productCategoryChip.text = category.categoryName
                viewModel.categoryFilter.postValue(category)
            }?: kotlin.run {
                bind.productCategoryChip.text = resources.getString(R.string.products)
                viewModel.categoryFilter.postValue(Category(0, "Products", 0,false))
            }

            bind.categoryLayout.visibility = View.GONE
            bind.productCategoryChip.chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
        }
    }

    override fun openImagePreviewFor(image: Image) {
        Navigation.findNavController(requireView()).navigate(DashFragmentDirections.actionGlobalImagePreview(image))
    }

}