package com.baljeet.expirytracker.fragment.dash

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentDashBinding
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.TrackerAdapter
import com.baljeet.expirytracker.util.NotificationReceiver
import com.baljeet.expirytracker.util.ProductStatus
import com.baljeet.expirytracker.util.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


class DashFragment : Fragment() {

    private val productVM: ProductViewModel by activityViewModels()
    private val imageVm: ImageViewModel by activityViewModels()
    private val categoryVM: CategoryViewModel by activityViewModels()
    private val selectVM: SelectFromViewModel by activityViewModels()
    private val trackerVm: TrackerViewModel by activityViewModels()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val calendar = Calendar.getInstance()

    private var handlerAnimation = Handler(Looper.getMainLooper())

    private lateinit var bind: FragmentDashBinding

    @ExperimentalTime
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

        trackerVm.readAllTracker?.let {
            it.observe(viewLifecycleOwner, { its ->
                if (its.isNullOrEmpty()) {
                    noItemView()
                } else {
                    bind.trackerLayout.visibility = View.VISIBLE
                    bind.noTrackerLayout.visibility = View.GONE
                    bind.addProductFab.visibility = View.VISIBLE
                    bind.imageForAnimation.visibility = View.VISIBLE
                    bind.addProductButton.visibility = View.GONE
                    runnable.run()
                    val arrayList = ArrayList<TrackerAndProduct>()
                    arrayList.addAll(its)
                    bind.trackerRecyclerView.adapter = TrackerAdapter(arrayList, requireContext())
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
        createNotificationChannel()

        if(!SharedPref.isAlertEnabled){
            val time = Clock.System.now().plus(Duration.minutes(5)).toLocalDateTime(TimeZone.currentSystemDefault())
            setReminderForProducts(time.hour,time.minute)
            SharedPref.isAlertEnabled = true
        }
        bind.greetingText.setOnClickListener {
            if(SharedPref.isAlertEnabled){
                alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(requireContext(), NotificationReceiver::class.java)
                pendingIntent = PendingIntent.getBroadcast(
                    requireContext(), 0, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager.cancel(pendingIntent)
                SharedPref.isAlertEnabled = false
                Toast.makeText(requireContext(),"Alerts disabled", Toast.LENGTH_SHORT).show()
            }else{
                val time = Clock.System.now().plus(Duration.minutes(5)).toLocalDateTime(TimeZone.currentSystemDefault())
                setReminderForProducts(time.hour,time.minute)
                SharedPref.isAlertEnabled = true
                Toast.makeText(requireContext(),"Alerts enabled", Toast.LENGTH_SHORT).show()
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            bind.statusText.text = ProductStatus.getStatusMessage(requireContext())
        }

        return bind.root
    }

    private fun setReminderForProducts(hour : Int , minutes : Int) {


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
        Toast.makeText(requireContext(), "reminder has been set for  $hour:$minutes", Toast.LENGTH_SHORT).show()
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
        handlerAnimation.removeCallbacks(runnable)
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

    override fun onDestroy() {
        super.onDestroy()
        handlerAnimation.removeCallbacks(runnable)
    }


    private var runnable = object : Runnable {
        override fun run() {
            bind.imageForAnimation.apply {
                animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(1200)
                    .withEndAction {
                        scaleX = 1f
                        scaleY = 1f
                        alpha = 1f
                    }
            }
            handlerAnimation.postDelayed(this, 1500)
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