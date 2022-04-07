package com.baljeet.expirytracker.fragment.product

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentAddTrackerBinding
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.OptionsAdapter
import com.baljeet.expirytracker.util.Constants
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.NotificationUtil
import com.baljeet.expirytracker.util.SharedPref
import com.dwellify.contractorportal.util.TimeConvertor
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.time.LocalDateTime
import java.util.*


enum class LocalDateTimeFor{
    MFG,EXPIRY,REMINDER
}

class AddTracker : Fragment(), OptionsAdapter.OnOptionSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private val categoriesWithImages = ArrayList<CategoryAndImage>()
    private val productsWithImages = ArrayList<ProductAndImage>()



    private val viewModel: SelectFromViewModel by activityViewModels()
    private val categoryVM: CategoryViewModel by activityViewModels()
    private val productVM: ProductViewModel by activityViewModels()
    private val trackerViewModel: TrackerViewModel by activityViewModels()

    private lateinit var adapter: OptionsAdapter
    private lateinit var nameAdapter: OptionsAdapter

    private lateinit var bind: FragmentAddTrackerBinding

    private var pickingDateTimeFor : LocalDateTimeFor = LocalDateTimeFor.MFG


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.incCount()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentAddTrackerBinding.inflate(inflater, container, false)
        bind.reminderDateClickView.isEnabled = false
        bind.closeBtn.setOnClickListener { activity?.onBackPressed() }

        bind.optionsRecycler.layoutManager = GridLayoutManager(requireContext(),
            if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4
            )
        bind.nameOptionsRecycler.layoutManager = GridLayoutManager(requireContext(),
            if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4
            )

        bind.nameLayout.visibility = View.GONE
        bind.completedCheck.visibility = View.GONE
        bind.optionsRecycler.visibility = View.VISIBLE

        adapter = OptionsAdapter(categoriesWithImages, requireContext(), null, this, null)
        bind.optionsRecycler.adapter = adapter
        categoryVM.readAllCategoriesWithImages.observe(viewLifecycleOwner) {
            categoriesWithImages.clear()
            categoriesWithImages.addAll(it)
            adapter.setCategoriesWithImages(it)
        }

        nameAdapter = OptionsAdapter(null, requireContext(), null, this, productsWithImages)
        bind.nameOptionsRecycler.adapter = nameAdapter


        bind.expiryClickView.setOnClickListener {
            pickingDateTimeFor = LocalDateTimeFor.EXPIRY
            val cal = Calendar.getInstance()
            viewModel.getExpiryDate()?.let { expiry->
                DatePickerDialog(requireContext(),R.style.datePickerTheme,this,expiry.year,expiry.month.value-1,expiry.dayOfMonth).show()
            }?: kotlin.run {
                DatePickerDialog(requireContext(),R.style.datePickerTheme,this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
        bind.mfgClickView.setOnClickListener {
            pickingDateTimeFor = LocalDateTimeFor.MFG
            val cal = Calendar.getInstance()
            viewModel.getMfgDate()?.let { mfg->
                DatePickerDialog(requireContext(),R.style.datePickerTheme,this,mfg.year,mfg.month.value-1,mfg.dayOfMonth).show()
            }?: kotlin.run {
                DatePickerDialog(requireContext(),R.style.datePickerTheme,this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
        bind.reminderDateClickView.setOnClickListener {
            pickingDateTimeFor = LocalDateTimeFor.REMINDER
            val cal = Calendar.getInstance()
            viewModel.reminderDate?.let { reminder->
                DatePickerDialog(requireContext(),R.style.datePickerTheme,this,reminder.year,reminder.month.value-1,reminder.dayOfMonth).show()
            }?: kotlin.run {
                DatePickerDialog(requireContext(),R.style.datePickerTheme,this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        bind.categoryClickView.setOnClickListener {
            bind.completedCheck.visibility = View.GONE
            bind.optionsRecycler.visibility = View.VISIBLE
        }
        bind.nameClickView.setOnClickListener {
            bind.completed2Check.visibility = View.GONE
            bind.nameOptionsRecycler.visibility = View.VISIBLE
        }
        var doRemind = false
        bind.reminderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                bind.remindDayBeforeCheckbox.id -> {
                    bind.reminderDateClickView.isEnabled = false
                    val beforeOneDay = viewModel.getExpiryDate()?.minusDays(1)
                    beforeOneDay?.let{
                        viewModel.reminderDate=  LocalDateTime.of(it.year, it.month, it.dayOfMonth, 9, 0, 0)
                    }

                    viewModel.reminderDate?.let {
                        bind.reminderDateEdittext.setText(
                            resources.getString(
                                R.string.date_string_with_month_name_and_time,
                                it.month.name.substring(0, 3),
                                it.dayOfMonth,
                                it.year,
                                TimeConvertor.getTime(it.hour,it.minute,true)
                            )
                        )
                    }
                    doRemind = true
                }
                bind.remindOnCustomDateCheckbox.id -> {
                    bind.reminderDateClickView.isEnabled = true
                    viewModel.reminderDate?.let {
                        bind.reminderDateEdittext.setText(
                            resources.getString(
                                R.string.date_string_with_month_name_and_time,
                                it.month.name.substring(0, 3),
                                it.dayOfMonth,
                                it.year,
                                TimeConvertor.getTime(it.hour,it.minute,true)
                            )
                        )
                    } ?: kotlin.run {
                        viewModel.getExpiryDate()?.let {
                            bind.reminderDateEdittext.setText(
                                resources.getString(
                                    R.string.date_string_with_month_name_and_time,
                                    it.month.name.substring(0, 3),
                                    it.dayOfMonth,
                                    it.year ,
                                    TimeConvertor.getTime(it.hour,it.minute,true)
                                )
                            )
                        }
                    }
                    doRemind = true
                }
                bind.doNotRemindCheckbox.id -> {
                    bind.reminderDateClickView.isEnabled = false
                    bind.reminderDateEdittext.setText(R.string.do_not_remind)
                    doRemind = false
                }
            }
            bind.reminderSetCheck.visibility = View.VISIBLE
            bind.addProductButton.visibility = View.VISIBLE
        }

        bind.addProductButton.setOnClickListener {
            if(!doRemind){
                viewModel.reminderDate = null
            }

            val tracker = Tracker(
                productId = viewModel.getSelectedProduct()?.product?.productId!!,
                mfgDate = viewModel.getMfgDate(),
                expiryDate = viewModel.getExpiryDate(),
                reminderDate = viewModel.reminderDate,
                reminderOn = SharedPref.isAlertEnabled && doRemind,
                usedWhileOk = false,
                usedWhileFresh = false,
                usedNearExpiry = false,
                gotExpired = false,
                quantity = null,
                measuringUnit = null,
                note = null,
                isFavourite = false,
                isArchived = false,
                isUsed = false
            )
            trackerViewModel.addTracker(tracker)
            val latestTracker = trackerViewModel.getLatestAddedTracker().tracker
            Log.d("Log for - ","latest tracker id = ${latestTracker.trackerId}")
            viewModel.reminderDate?.let {
                if(SharedPref.isAlertEnabled && doRemind) {
                    Log.d("Log for - reminder time","\n\n\n\n\n\n${it}\n\n\n\n\n\n\n\n\n")
                    NotificationUtil.setReminderForProducts(
                        it,
                        requireContext(),
                        latestTracker.trackerId
                    )

                }
            }
            activity?.onBackPressed()
        }

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showAd()
    }

    private fun showAd(){
        viewModel.mInterstitialAd.observe(viewLifecycleOwner){
            it?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdShowedFullScreenContent() {
                    viewModel.mInterstitialAd.postValue(null)
                }
            }
            it?.show(requireActivity())
        }
    }

    override fun onOptionSelected(position: Int, checkVisibility: Int, optionIsCategory: Boolean) {

        if (position == -1) {
            Navigation.findNavController(requireView()).navigate(
                AddTrackerDirections.actionAddProductToCreateCustom(
                    itemType = if (optionIsCategory) "Category" else "Product",
                    selectedCategory = if (optionIsCategory) null else viewModel.getSelectedCategory()?.category
                )
            )
        } else {
            if (optionIsCategory) {
                if (checkVisibility == View.GONE) {
                    bind.customEdittext.setText(categoriesWithImages[position].category.categoryName)
                    viewModel.setSelectedCategory(categoriesWithImages[position])
                    bind.selectedCategoryIcon.visibility = View.VISIBLE
                    bind.customBoxLayout.isEndIconVisible = false
                    bind.customNameBoxLayout.isEndIconVisible = false
                    when (categoriesWithImages[position].image.mimeType) {
                        "asset" -> {
                            bind.selectedCategoryIcon.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    requireContext(),
                                    resources.getIdentifier(
                                        categoriesWithImages[position].image.imageUrl,
                                        "drawable",
                                        requireContext().packageName
                                    )
                                )
                            )
                        }
                        else -> {
                            bind.selectedCategoryIcon.setImageBitmap(
                                ImageConvertor.stringToBitmap(categoriesWithImages[position].image.bitmap)
                            )
                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        bind.optionsRecycler.visibility = View.GONE
                        bind.nameLayout.visibility = View.VISIBLE
                        bind.nameOptionsRecycler.visibility = View.VISIBLE
                        productVM.readProductWithImageById(viewModel.getSelectedCategory()?.category?.categoryId!!)
                        productVM.productsByCategoryWithImage.observe(viewLifecycleOwner) {
                            productsWithImages.clear()
                            productsWithImages.addAll(it)
                            nameAdapter.setProducts(it)
                        }
                        nameAdapter.refreshAll(null)
                        bind.completedCheck.visibility = View.VISIBLE
                        bind.completed2Check.visibility = View.GONE
                        bind.completed3Check.visibility = View.GONE
                        bind.customNameEdittext.text?.clear()
                        bind.selectedNameIcon.visibility = View.GONE
                    }, 200)
                } else {
                    bind.customEdittext.text?.clear()
                    viewModel.setSelectedCategory(null)
                    bind.selectedCategoryIcon.visibility = View.GONE
                    bind.completedCheck.visibility = ViewAnimator.GONE
                }
            } else {
                if (checkVisibility == View.GONE) {
                    bind.customNameEdittext.setText(productsWithImages[position].product.name)
                    viewModel.setSelectedProduct(productsWithImages[position])
                    bind.selectedNameIcon.visibility = View.VISIBLE
                    bind.customNameBoxLayout.isEndIconVisible = false

                    when (productsWithImages[position].image.mimeType) {
                        "asset" -> {
                            bind.selectedNameIcon.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    requireContext(),
                                    resources.getIdentifier(
                                        productsWithImages[position].image.imageUrl,
                                        "drawable",
                                        requireContext().packageName
                                    )
                                )
                            )
                        }
                        else -> {
                            bind.selectedNameIcon.setImageBitmap(
                                ImageConvertor.stringToBitmap(productsWithImages[position].image.bitmap)
                            )
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        bind.nameOptionsRecycler.visibility = View.GONE
                        bind.completed2Check.visibility = View.VISIBLE
                        bind.completed3Check.visibility = View.GONE
                        bind.dateLayout.visibility = View.VISIBLE
                    }, 200)
                } else {
                    bind.customNameEdittext.text?.clear()
                    viewModel.setSelectedProduct(null)
                    bind.selectedNameIcon.visibility = View.GONE
                    bind.completed2Check.visibility = ViewAnimator.GONE
                }
            }
        }
    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.reminderDate = viewModel.reminderDate?.let {
             LocalDateTime.of(it.year,it.month,it.dayOfMonth,hourOfDay,minute)
        }
        viewModel.reminderDate?.let {
            bind.reminderDateEdittext.setText(
                resources.getString(
                    R.string.date_string_with_month_name_and_time,
                    it.month.name.substring(0, 3),
                    it.dayOfMonth,
                    it.year,
                    TimeConvertor.getTime(it.hour,it.minute,true)
                )
            )
        }
    }

    override fun onDateSet(view : DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
         when(pickingDateTimeFor){
             LocalDateTimeFor.MFG->{
                     viewModel.setMfgDate(
                         LocalDateTime.of(
                             year,
                             month+1,
                             dayOfMonth,
                             0,
                             1
                         )
                     )
                   viewModel.getMfgDate()?.let {
                     bind.mfgDateEdittext.setText(
                         resources.getString(
                             R.string.date_string_with_month_name,
                             it.month.name.substring(0, 3),
                             it.dayOfMonth,
                             it.year
                         )
                     )
                 }
                 bind.expiryDateEdittext.text?.let {
                     if (it.isNotEmpty()) {
                         bind.completed3Check.visibility = View.VISIBLE
                         bind.addProductButton.visibility = View.VISIBLE
                     }
                 }
             }
             LocalDateTimeFor.EXPIRY->{
                 viewModel.setExpiryDate(
                     LocalDateTime.of(
                         year,
                         month+1,
                         dayOfMonth,
                         23,
                         58
                     )
                 )
                 viewModel.getExpiryDate()?.let {
                     bind.expiryDateEdittext.setText(
                         resources.getString(
                             R.string.date_string_with_month_name,
                             it.month.name.substring(0, 3),
                             it.dayOfMonth,
                             it.year
                         )
                     )
                 }

                 bind.mfgDateEdittext.text?.let {
                     if (it.isNotEmpty()) {
                         bind.completed2Check.visibility = View.VISIBLE
                         bind.editReminderLayout.visibility = View.VISIBLE
                     }
                 }
             }
             LocalDateTimeFor.REMINDER->{
                 val previousDate = viewModel.reminderDate
                 viewModel.reminderDate = LocalDateTime.of(
                     year,
                     month+1,
                     dayOfMonth,
                     9,
                     0
                 )
                 bind.reminderDateEdittext.text?.let {
                     if (it.isNotEmpty()) {
                         bind.reminderSetCheck.visibility = View.VISIBLE
                         bind.addProductButton.visibility = View.VISIBLE
                     }
                 }
                 val currentTime = LocalDateTime.now()
                 previousDate?.let {
                     TimePickerDialog(requireContext(),this,previousDate.hour,previousDate.minute,false).show()
                 }?: kotlin.run {
                     TimePickerDialog(requireContext(),this,currentTime.hour,currentTime.minute,false).show()
                 }
             }
         }
    }
}