package com.baljeet.expirytracker.fragment.dash

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentDashBinding
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.TrackerAdapter
import com.baljeet.expirytracker.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.Month
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class DashFragment : Fragment() {

    private lateinit var disposable: Disposable

    private val productVM: ProductViewModel by activityViewModels()
    private val imageVm: ImageViewModel by activityViewModels()
    private val categoryVM: CategoryViewModel by viewModels()
    private val selectVM: SelectFromViewModel by activityViewModels()
    private val trackerVm: TrackerViewModel by viewModels()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val calendar = Calendar.getInstance()

    private val messages = ArrayList<String>()
    private val categories = ArrayList<Category>()

    private lateinit var bind: FragmentDashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = Observable.interval(
            3000, 3000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::setStatus, this::onError)
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
                .navigate(R.id.action_dashFragment_to_addProduct)
        }

        bind.trackerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        trackerVm.readAllTracker.let {
            it.observe(viewLifecycleOwner, { its ->
                if (its.isEmpty()) {
                    noItemView()
                    disposable.dispose()
                } else {
                    setDashList(its)
                }
            })
        }
        seedData()

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

        bind.statusCategoryChip.apply {
            setOnClickListener {
                if (bind.statusLayout.isGone) {
                    bind.statusLayout.fadeVisibility(View.VISIBLE,500)
                    bind.categoryLayout.fadeVisibility(View.GONE,500)
                    chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                    bind.productCategoryChip.chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))
                } else {
                    chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))
                    bind.productCategoryChip.chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                    bind.statusLayout.fadeVisibility(View.GONE,500)
                }
            }


            bind.productCategoryChip.apply {
                setOnClickListener {
                    if (bind.categoryLayout.isGone) {
                        bind.statusLayout.fadeVisibility(View.GONE,500)
                        bind.categoryLayout.fadeVisibility(View.VISIBLE,500)
                        chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                        bind.statusCategoryChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))
                    } else {
                        chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))
                        bind.statusCategoryChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                        bind.categoryLayout.fadeVisibility(View.GONE,500)
                    }
                }
            }

            bind.statusChoiceList.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    bind.choiceAll.id -> {
                        text = Status.ALL.status
                        trackerVm.statusFilter = Constants.PRODUCT_STATUS_ALL
                        setDashList(trackerVm.filterTrackers())
                    }
                    bind.choiceExpired.id -> {
                        text = Status.EXPIRED.status
                        trackerVm.statusFilter = Constants.PRODUCT_STATUS_EXPIRED
                        setDashList(trackerVm.filterTrackers())
                    }
                    bind.choiceExpiring.id -> {
                        text = Status.EXPIRING.status
                        trackerVm.statusFilter = Constants.PRODUCT_STATUS_EXPIRING
                        setDashList(trackerVm.filterTrackers())
                    }
                    bind.choiceFresh.id -> {
                        text = Status.FRESH.status
                        trackerVm.statusFilter = Constants.PRODUCT_STATUS_FRESH
                        setDashList(trackerVm.filterTrackers())
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    bind.statusLayout.fadeVisibility(View.GONE,500)
                    chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                }, 10)
            }

            createNotificationChannel()

            if (!SharedPref.isAlertEnabled) {
                val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                setReminderForProducts(time.hour, time.minute.plus(5))
                SharedPref.isAlertEnabled = true
            }

            bind.greetingText.setOnClickListener {
                if (SharedPref.isAlertEnabled) {
                    alarmManager =
                        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(requireContext(), NotificationReceiver::class.java)
                    pendingIntent = PendingIntent.getBroadcast(
                        requireContext(), 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.cancel(pendingIntent)
                    SharedPref.isAlertEnabled = false
                    Toast.makeText(requireContext(), "Alerts disabled", Toast.LENGTH_SHORT).show()
                } else {
                    val time =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    setReminderForProducts(time.hour, time.minute.plus(5))
                    SharedPref.isAlertEnabled = true
                    Toast.makeText(requireContext(), "Alerts enabled", Toast.LENGTH_SHORT).show()
                }
            }
            getCategoriesChips()
            getStatus()
            return bind.root
        }
    }

    private fun onError(throwable: Throwable) {
        Toast.makeText(
            requireContext(),
            "${throwable.message} error while syncing new updates",
            Toast.LENGTH_SHORT
        ).show()
        disposable.dispose()
    }

    private var messageNum = 0
    private fun setStatus(aLong: Long) {
        aLong.let {
            Log.d("Log for - ", it.toString())
        }
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
            if(messageNum == messages.size-1){
                messageNum = 0
            }else{
                messageNum++
            }
        }
    }
    fun View.fadeVisibility(visibility: Int, duration: Long = 500) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }

    private fun setDashList(list: List<TrackerAndProduct>) {
        if (list.isNullOrEmpty()) {
            bind.noItemText.visibility = View.VISIBLE
            bind.trackerRecyclerView.visibility = View.GONE
            if (trackerVm.statusFilter == Constants.PRODUCT_STATUS_ALL) {
                bind.noItemText.text = resources.getString(
                    R.string.no_item_text1,
                    trackerVm.categoryFilter?.categoryName
                )
            } else {
                bind.noItemText.text = resources.getString(
                    R.string.no_item_text,
                    trackerVm.statusFilter,
                    trackerVm.categoryFilter?.categoryName
                )
            }
        } else {
            bind.trackerLayout.visibility = View.VISIBLE
            bind.trackerRecyclerView.visibility = View.VISIBLE
            bind.noItemText.visibility = View.GONE
            bind.noTrackerLayout.visibility = View.GONE
            bind.addProductFab.visibility = View.VISIBLE
            bind.imageForAnimation.visibility = View.VISIBLE
            bind.addProductButton.visibility = View.GONE

            val arrayList = ArrayList<TrackerAndProduct>()
            arrayList.addAll(list)
            bind.trackerRecyclerView.adapter = TrackerAdapter(arrayList, requireContext())
        }
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

            trackerVm.categoryFilter = category
            setDashList(trackerVm.filterTrackers())

            Handler(Looper.getMainLooper()).postDelayed({
                bind.categoryLayout.fadeVisibility(View.GONE,500)
                bind.productCategoryChip.chipBackgroundColor =
                    ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
            }, 10)
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

    private fun setReminderForProducts(hour: Int, minutes: Int) {

        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent
        )
        Toast.makeText(
            requireContext(),
            "reminder has been set for  $hour:$minutes",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun createNotificationChannel() {
        val name = "ExpiryTrackerReminderBaljeet"
        val description = "Channel for ExpiryTracker Reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("expiryTrackerBaljeet", name, importance)
        channel.description = description
        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun noItemView() {
        bind.noTrackerLayout.visibility = View.VISIBLE
        bind.trackerLayout.visibility = View.GONE
        bind.addProductFab.visibility = View.GONE
        bind.imageForAnimation.visibility = View.GONE
        bind.addProductButton.visibility = View.VISIBLE
        disposable.dispose()
    }

    private fun seedData() {
        addAllImages()
        addAllCategories()
        addAllProducts()
    }

    private fun addAllCategories() {
        for (category in selectVM.getDefaultCategories()) {
            categoryVM.addCategory(category)
        }
    }

    private fun addAllProducts() {
        for (product in selectVM.getAllProducts()) {
            productVM.addProduct(product)
        }
    }

    private fun addAllImages() {
        for (image in selectVM.getImages()) {
            imageVm.addImage(image)
        }
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    override fun onResume() {
        super.onResume()
        if (disposable.isDisposed) {
            disposable = Observable.interval(
                3000, 3000,
                TimeUnit.MILLISECONDS
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setStatus, this::onError)
        }
    }

    private fun setTimeAndGreetings() {
        val dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
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
}

