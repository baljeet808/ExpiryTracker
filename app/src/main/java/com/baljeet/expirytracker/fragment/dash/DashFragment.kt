package com.baljeet.expirytracker.fragment.dash

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentDashBinding
import com.baljeet.expirytracker.interfaces.OnTrackerOpenListener
import com.baljeet.expirytracker.interfaces.UpdateTrackerListener
import com.baljeet.expirytracker.listAdapters.TrackerDiffAdapter
import com.baljeet.expirytracker.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import java.time.Month


class DashFragment : Fragment(), UpdateTrackerListener, OnTrackerOpenListener {

    private lateinit var trackerAdapter : TrackerDiffAdapter
    private val categoryVM: CategoryViewModel by viewModels()
    private val trackerVm: TrackerViewModel by activityViewModels()

    private val messages = ArrayList<String>()
    private val categories = ArrayList<Category>()

    private lateinit var bind: FragmentDashBinding

    lateinit var loopHandler : Handler

    private val updateStatus = object : Runnable {
        override fun run() {
            setStatus()
            loopHandler.postDelayed(this,3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loopHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentDashBinding.inflate(inflater, container, false)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
            View.VISIBLE
        SharedPref.init(requireContext())
        setTimeAndGreetings()

        bind.addProductFab.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_dashFragment_to_addProduct)
        }
        bind.addProductButton.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(DashFragmentDirections.actionDashFragmentToAddProduct())
        }

        bind.trackerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        trackerAdapter = TrackerDiffAdapter(requireContext(),this, this)
        bind.trackerRecyclerView.adapter = trackerAdapter

        trackerVm.filteredTrackers.let {
            it.observe(viewLifecycleOwner) { its ->
                if (trackerVm.noTrackerIsActive) {
                    noItemView()
                    loopHandler.removeCallbacks(updateStatus)
                } else {
                    setDashList(its)
                }
            }
        }

        bind.trackerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                bind.addProductFab.apply {
                    //scroll down
                    if (dy > 30 && isExtended) {
                        shrink()
                    }
                    //reached the top of list
                    if (!recyclerView.canScrollVertically(-1)) {
                        extend()
                    }
                }

            }
        })

        bind.statusChip.apply {
            setOnClickListener {
                if (bind.statusLayout.isGone) {
                    bind.statusLayout.visibility = View.VISIBLE
                    bind.categoryLayout.visibility = View.GONE
                    chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.black))

                    setTextColor(requireContext().getColor(R.color.main_background))

                    bind.productCategoryChip.chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
                    bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))

                } else {
                    chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
                    setTextColor(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.always_white))
                    bind.statusLayout.visibility = View.GONE
                }
            }


            bind.productCategoryChip.apply {
                setOnClickListener {
                    if (bind.categoryLayout.isGone) {
                        bind.statusLayout.visibility = View.GONE
                        bind.categoryLayout.visibility = View.VISIBLE
                        chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.black))

                        setTextColor(requireContext().getColor(R.color.main_background))

                        bind.statusChip. chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
                        bind.statusChip.setTextColor(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.always_white))
                    } else {
                        chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
                        setTextColor(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.always_white))
                        bind.categoryLayout.visibility = View.GONE
                    }
                }
            }

            bind.statusChoiceList.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    bind.choiceAll.id -> {
                        text = Status.ALL.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_ALL)
                    }
                    bind.choiceExpired.id -> {
                        text = Status.EXPIRED.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_EXPIRED)
                    }
                    bind.choiceExpiring.id -> {
                        text = Status.EXPIRING.status
                        trackerVm.statusFilter .postValue(Constants.PRODUCT_STATUS_EXPIRING)
                    }
                    bind.choiceFresh.id -> {
                        text = Status.FRESH.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_FRESH)
                    }
                    else -> {
                        text = Status.ALL.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_ALL)
                    }
                }
                bind.statusLayout.visibility = View.GONE
                chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
                setTextColor(requireContext().getColor(R.color.always_white))
            }

            bind.greetingText.setOnClickListener {

            }
            getCategoriesChips()
            getStatus()

        }
        trackerVm.favouriteFilter.observe(viewLifecycleOwner) { filter ->
            when (filter) {
                Constants.SHOW_ALL -> {
                    bind.favouriteToggle.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_star_half_32
                        )
                    )
                }
                Constants.SHOW_ONLY_FAVOURITE -> {
                    bind.favouriteToggle.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_round_star_32
                        )
                    )
                }
                Constants.SHOW_ONLY_NON_FAVOURITE -> {
                    bind.favouriteToggle.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_star_outline_32
                        )
                    )
                }
            }
        }

        bind.favouriteToggle.apply {
            setOnClickListener {
                trackerVm.favouriteFilter.value?.let { filter ->
                    when (filter) {
                        Constants.SHOW_ALL -> {
                            trackerVm.favouriteFilter.postValue(Constants.SHOW_ONLY_FAVOURITE)
                        }
                        Constants.SHOW_ONLY_FAVOURITE -> {
                            trackerVm.favouriteFilter.postValue(Constants.SHOW_ONLY_NON_FAVOURITE)
                        }
                        else -> {
                            trackerVm.favouriteFilter.postValue(Constants.SHOW_ALL)
                        }
                    }
                }?: kotlin.run {
                    trackerVm.favouriteFilter.postValue(Constants.SHOW_ONLY_FAVOURITE)
                }
            }
        }
        loopHandler = Handler(Looper.getMainLooper())
        return bind.root
    }

    private fun setDashList(list: List<TrackerAndProduct>) {
        if (list.isNullOrEmpty()) {
            Log.d("Log for - dash","no tracker view called")
            bind.noItemText.visibility = View.VISIBLE
            bind.trackerRecyclerView.visibility = View.GONE
            if (trackerVm.statusFilter.value == Constants.PRODUCT_STATUS_ALL) {
                bind.noItemText.text = resources.getString(
                    R.string.no_item_text1,
                    trackerVm.categoryFilter.value?.categoryName
                )
            } else {
                bind.noItemText.text = resources.getString(
                    R.string.no_item_text,
                    trackerVm.statusFilter.value?:Constants.PRODUCT_STATUS_ALL,
                    trackerVm.categoryFilter.value?.categoryName
                )
            }
        } else {
            Log.d("Log for - dash","tracker list showing")
            bind.trackerLayout.visibility = View.VISIBLE
            bind.trackerRecyclerView.visibility = View.VISIBLE
            bind.noItemText.visibility = View.GONE
            bind.noTrackerLayout.visibility = View.GONE
            bind.addProductFab.visibility = View.VISIBLE
            bind.imageForAnimation.visibility = View.VISIBLE
            bind.addProductButton.visibility = View.GONE

            trackerAdapter.submitList(list)

        }
    }

    private fun noItemView() {
        bind.noTrackerLayout.visibility = View.VISIBLE
        bind.trackerLayout.visibility = View.GONE
        bind.addProductFab.visibility = View.GONE
        bind.imageForAnimation.visibility = View.GONE
        bind.addProductButton.visibility = View.VISIBLE
        loopHandler.removeCallbacks(updateStatus)

    }

    private var messageNum = 0
    private fun setStatus() {
        bind.imageForAnimation.apply {
            animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(1200)
                .withEndAction {
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 1f
                }
        }
        if (messages.size > 0) {
            bind.statusText.apply {
                animate().alpha(0f).setDuration(1200)
                    .withEndAction {
                        alpha = 1f
                        text = messages[messageNum]
                    }
            }
            if (messageNum == messages.size - 1) {
                messageNum = 0
            } else {
                messageNum++
            }
        }
    }

    private fun getCategoriesChips() {
        trackerVm.categoryFilter.value?.let {
            bind.productCategoryChip.text = it.categoryName
        }
        categoryVM.readAllCategories?.let {
            it.observe(viewLifecycleOwner) { cats ->
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
                                trackerVm.categoryFilter.value?.let { cat ->
                                    cat.categoryId == category.categoryId
                                } ?: false
                            addView(chip)
                        }
                    }
                }
            }
        }
        bind.categoriesChoiceList.setOnCheckedChangeListener { _, checkedId ->
            val category = categories.firstOrNull { c -> c.categoryId == checkedId }
            category?.let {
                bind.productCategoryChip.text = category.categoryName
                trackerVm.categoryFilter.postValue(category)
            } ?: kotlin.run {
                bind.productCategoryChip.text = resources.getString(R.string.products)
                trackerVm.categoryFilter.postValue(Category(0, "Products", 0,false))
            }
            bind.categoryLayout.visibility = View.GONE
            bind.productCategoryChip. chipBackgroundColor  = ColorStateList.valueOf(MyColors.getColorByAttr(requireContext(),R.attr.window_top_bar,R.color.black))
            bind.productCategoryChip.setTextColor(MyColors.getColorByAttr(requireContext(),R.attr.text_dialog_color,R.color.always_white))
        }
    }
    private var getStatusJob: Job? = null

    private fun getStatus() {
        getStatusJob?.let {
            if (it.isActive) {
                it.cancel()
            }
        }
        getStatusJob = lifecycleScope.launchWhenCreated {
            messages.addAll(ProductStatus.getStatusMessage(requireContext()))
        }
    }



    override fun onPause() {
        super.onPause()
        loopHandler.removeCallbacks(updateStatus)
    }

    override fun onDestroy() {
        super.onDestroy()
        loopHandler.removeCallbacks(updateStatus)
    }

    override fun onStop() {
        super.onStop()
        loopHandler.removeCallbacks(updateStatus)
    }

    override fun onResume() {
        super.onResume()
        loopHandler.post(updateStatus)
    }

    private fun setTimeAndGreetings() {
        val dateTime = Clock.System.now().toLocalDateTime(Constants.TIMEZONE)
        bind.currentDate.text = resources.getString(
            R.string.date_var,
            Month.of(dateTime.monthNumber).name.substring(0, 3),
            dateTime.dayOfMonth,
            dateTime.year
        )
        // bind.currentTime.text = TimeConvertor.getTime(dateTime.hour,dateTime.minute,true)
        bind.greetingText.apply {
            when {
                dateTime.hour < 12 -> {
                    text = resources.getString(R.string.morning_greeting_text)
                }
                dateTime.hour in 12..15 -> {
                    text = resources.getString(R.string.afternoon_greeting_text)
                }
                dateTime.hour >= 16 -> {
                    text = resources.getString(R.string.evening_greeting_text)
                }
                else -> Unit
            }
        }
    }

    override fun updateTracker(updatedTracker: Tracker) {
        trackerVm.updateTracker(updatedTracker)
    }

    override fun openTrackerInfo(tracker: TrackerAndProduct) {
        Navigation.findNavController(requireView()).navigate(DashFragmentDirections.actionDashFragmentToTrackerDetails(tracker.tracker.trackerId))
    }
}

