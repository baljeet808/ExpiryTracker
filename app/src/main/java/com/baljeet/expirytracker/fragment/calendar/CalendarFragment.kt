package com.baljeet.expirytracker.fragment.calendar

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentCalendarV1Binding
import com.baljeet.expirytracker.interfaces.OnDateSelectedListener
import com.baljeet.expirytracker.interfaces.UpdateTrackerListener
import com.baljeet.expirytracker.listAdapters.CalendarAdapter
import com.baljeet.expirytracker.listAdapters.TrackerDiffAdapter
import com.baljeet.expirytracker.model.DayWithProducts
import com.baljeet.expirytracker.util.Constants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import kotlinx.datetime.toJavaLocalDateTime


class CalendarFragment : Fragment(), OnDateSelectedListener , UpdateTrackerListener{
    private lateinit var bind: FragmentCalendarV1Binding
    private val viewModel: CalendarViewModel by viewModels()
    private val categoryVM: CategoryViewModel by viewModels()
    private val daysInMonthArray = ArrayList<DayWithProducts>()
    private val categories = ArrayList<Category>()

    private lateinit var  trackerAdapter : TrackerDiffAdapter

    private lateinit var calendarAdapter : CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCalendarV1Binding.inflate(inflater, container, false)

        viewModel.favouriteFilter.observe(viewLifecycleOwner, { filter->
            when(filter){
                Constants.SHOW_ALL ->{
                    bind.favouriteToggle.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_star_half_32))
                }
                Constants.SHOW_ONLY_FAVOURITE->{
                    bind.favouriteToggle.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_round_star_32))
                }
                Constants.SHOW_ONLY_NON_FAVOURITE->{
                    bind.favouriteToggle.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_star_outline_32))
                }
            }
        })

        bind.favouriteToggle.apply {
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

        bind.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    val value = ((-1F)*verticalOffset) / 1000
                    bind.secondTopMostLine.scaleX = value
        })

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            nextButton.setOnClickListener {
                viewModel.setNextMonth()
                daysInMonthArray.clear()
                daysInMonthArray.addAll(viewModel.getMonth())
                calendarAdapter =CalendarAdapter(daysInMonthArray, requireContext(), this@CalendarFragment)
                monthRecyclerView.adapter = calendarAdapter
                monthName.text = viewModel.monthYearTextFromDate()
            }
            previousButton.setOnClickListener {
                viewModel.setPreviousMonth()
                daysInMonthArray.clear()
                daysInMonthArray.addAll(viewModel.getMonth())
                calendarAdapter =CalendarAdapter(daysInMonthArray, requireContext(), this@CalendarFragment)
                monthRecyclerView.adapter =calendarAdapter
                monthName.text = viewModel.monthYearTextFromDate()
            }
            monthName.text = viewModel.monthYearTextFromDate()
            monthRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
            daysInMonthArray.clear()
            daysInMonthArray.addAll(viewModel.getMonth())
            calendarAdapter =CalendarAdapter(daysInMonthArray, requireContext(), this@CalendarFragment)
            monthRecyclerView.adapter =calendarAdapter

            trackerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            trackerAdapter = TrackerDiffAdapter(requireContext(),this@CalendarFragment)
            trackerRecyclerView.adapter = trackerAdapter

        }

        bind.productCategoryChip.apply {
            setOnClickListener {
                bind.categoryLayout.apply {
                    if (bind.categoryLayout.isGone) {
                        bind.categoryLayout.visibility = View.VISIBLE
                        bind.productCategoryChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))
                        bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.main_background))
                    } else {
                        bind.categoryLayout.visibility = View.GONE
                        bind.productCategoryChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                        bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))
                    }
                }
            }
            text = viewModel.selectedCategory.categoryName
        }

        viewModel.filteredTrackers.observe(viewLifecycleOwner,{ trackers ->
            trackerAdapter.submitList(trackers)
        })

        getCategoriesChips()
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
                            val chip = Chip(
                                requireContext(),
                                null,
                                R.style.Widget_MaterialComponents_Chip_Choice
                            )
                            chip.text = category.categoryName
                            chip.id = category.categoryId
                            chip.isCheckable = true
                            chip.isClickable = true
                            chip.chipBackgroundColor =
                                ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                            chip.setTextColor(requireContext().getColor(R.color.always_white))
                            chip.checkedIcon = AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.check_circle_24
                            )
                            chip.isCheckedIconVisible = true
                            chip.checkedIconTint =
                                ColorStateList.valueOf(requireContext().getColor(R.color.always_white))
                            chip.chipMinHeight = 70f
                            chip.minWidth = 50
                            chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
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

            daysInMonthArray.clear()
            daysInMonthArray.addAll(viewModel.getMonth())
            calendarAdapter =CalendarAdapter(daysInMonthArray, requireContext(), this@CalendarFragment)
            bind.monthRecyclerView.adapter =calendarAdapter

            bind.categoryLayout.visibility = View.GONE
            bind.productCategoryChip.chipBackgroundColor =
                ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
        }
    }

    override fun openSelectedDate(dayWithProducts: DayWithProducts, selectedDayIndex: Int) {
        daysInMonthArray[selectedDayIndex].isSelected = true
        daysInMonthArray[viewModel.selectedDayIndex].isSelected = false
        calendarAdapter.notifyItemChanged(selectedDayIndex)
        calendarAdapter.notifyItemChanged(viewModel.selectedDayIndex)
        viewModel.selectedDayIndex = selectedDayIndex
        viewModel.selectedDayOfMonth = dayWithProducts.date
        viewModel.trackersInDay.postValue(dayWithProducts.products)
        bind.dayName.text = viewModel.dayYearTextFromDate(dayWithProducts.date!!.toJavaLocalDateTime())
    }

    override fun updateTracker(updatedTracker: Tracker) {
        viewModel.updateTracker(updatedTracker)
    }
}