package com.baljeet.expirytracker.fragment.calendar

import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentCalendarBinding
import com.baljeet.expirytracker.listAdapters.CalendarAdapter
import com.baljeet.expirytracker.model.DayWithProducts
import com.google.android.material.chip.Chip


class CalendarFragment : Fragment(), CalendarAdapter.OnDateSelectedListener {
    private lateinit var bind: FragmentCalendarBinding
    private val viewModel: CalendarViewModel by viewModels()
    private val categoryVM: CategoryViewModel by viewModels()

    private val categories = ArrayList<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCalendarBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            nextButton.setOnClickListener {
                viewModel.setNextMonth()
                monthRecyclerView.adapter =
                    CalendarAdapter(viewModel.getMonth(), requireContext(), this@CalendarFragment)
                monthName.text = viewModel.monthYearTextFromDate()
            }
            previousButton.setOnClickListener {
                viewModel.setPreviousMonth()
                monthRecyclerView.adapter =
                    CalendarAdapter(viewModel.getMonth(), requireContext(), this@CalendarFragment)
                monthName.text = viewModel.monthYearTextFromDate()
            }
            monthName.text = viewModel.monthYearTextFromDate()
            monthRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
            monthRecyclerView.adapter =
                CalendarAdapter(viewModel.getMonth(), requireContext(), this@CalendarFragment)
        }

        bind.productCategoryChip.apply {
            setOnClickListener {
                bind.categoryLayout.apply {
                    if (bind.categoryLayout.isGone) {
                        bind.categoryLayout.fadeVisibility(View.VISIBLE, 500)
                        bind.productCategoryChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))
                        bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.main_background))
                    } else {
                        bind.categoryLayout.fadeVisibility(View.GONE, 500)
                        bind.productCategoryChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                        bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))
                    }
                }
            }
            text = viewModel.selectedCategory.categoryName
        }

        getCategoriesChips()
    }

    private fun View.fadeVisibility(visibility: Int, duration: Long = 500) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }


    private fun getCategoriesChips() {
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
                            addView(chip)
                        }
                    }
                }
            })
        }
        bind.categoriesChoiceList.setOnCheckedChangeListener { _, checkedId ->
            val category = categories.first { it.categoryId == checkedId }
            bind.productCategoryChip.text = category.categoryName

            viewModel.selectedCategory = category
            bind.monthRecyclerView.adapter =
                CalendarAdapter(viewModel.getMonth(), requireContext(), this@CalendarFragment)
            bind.categoryLayout.fadeVisibility(View.GONE, 500)
            bind.productCategoryChip.chipBackgroundColor =
                ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
        }
    }

    override fun openSelectedDate(dayWithProducts: DayWithProducts) {
        Toast.makeText(
            requireContext(),
            "selected date - ${dayWithProducts.date?.dayOfMonth}",
            Toast.LENGTH_SHORT
        ).show()
    }
}