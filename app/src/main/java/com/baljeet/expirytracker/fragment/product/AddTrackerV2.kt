package com.baljeet.expirytracker.fragment.product

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentAddTrackerV2Binding
import com.baljeet.expirytracker.interfaces.OnCategorySelected
import com.baljeet.expirytracker.interfaces.OnProductSelected
import com.baljeet.expirytracker.listAdapters.ProductResultsAdapter
import com.baljeet.expirytracker.listAdapters.SearchResultsAdapter
import com.baljeet.expirytracker.util.NotificationUtil
import com.baljeet.expirytracker.util.SharedPref
import com.baljeet.expirytracker.util.setImage
import com.dwellify.contractorportal.util.TimeConvertor
import java.time.LocalDateTime
import java.util.*


enum class LocalDateTimeFor{
    MFG,EXPIRY,REMINDER
}


class AddTrackerV2 : Fragment(), OnCategorySelected, OnProductSelected,
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var bind: FragmentAddTrackerV2Binding

    private lateinit var categoryResultAdapter: SearchResultsAdapter
    private lateinit var productResultAdapter: ProductResultsAdapter
    private val catViewModel: CategoryViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val viewModel: AddTrackerViewModel by activityViewModels()
    private val trackerViewModel: TrackerViewModel by viewModels()

    private var pickingDateTimeFor : LocalDateTimeFor = LocalDateTimeFor.MFG



    private var formCompleted = MediatorLiveData<Boolean>().apply {
            addSource(viewModel.categoryGiven) {
                this.value = if (it) {
                    viewModel.productGiven.value!! && viewModel.expiryGiven.value!! && viewModel.mfgGiven.value!! && viewModel.reminderGiven.value!!
                } else {
                    false
                }
            }
            addSource(viewModel.productGiven) {
                this.value = if (it) {
                    viewModel.categoryGiven.value!! && viewModel.expiryGiven.value!! && viewModel.mfgGiven.value!! && viewModel.reminderGiven.value!!
                } else {
                    false
                }
            }
            addSource(viewModel.expiryGiven) {
                this.value = if (it) {
                    viewModel.productGiven.value!! && viewModel.categoryGiven.value!! && viewModel.mfgGiven.value!! && viewModel.reminderGiven.value!!
                } else {
                    false
                }
            }
            addSource(viewModel.mfgGiven) {
                this.value = if (it) {
                    viewModel.productGiven.value!! && viewModel.expiryGiven.value!! && viewModel.categoryGiven.value!! && viewModel.reminderGiven.value!!
                } else {
                    false
                }
            }
            addSource(viewModel.reminderGiven) {
                this.value = if (it) {
                    viewModel.productGiven.value!! && viewModel.expiryGiven.value!! && viewModel.mfgGiven.value!! && viewModel.categoryGiven.value!!
                } else {
                    false
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentAddTrackerV2Binding.inflate(inflater, container, false)
        bind.apply {
            categoryRecycler.layoutManager = GridLayoutManager(
                requireContext(),
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4,
                RecyclerView.HORIZONTAL,
                false
            )
            productRecycler.layoutManager = GridLayoutManager(
                requireContext(),
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4,
                RecyclerView.HORIZONTAL,
                false
            )
            categoryResultAdapter = SearchResultsAdapter(requireContext(), this@AddTrackerV2)
            productResultAdapter = ProductResultsAdapter(requireContext(), this@AddTrackerV2)
            categoryRecycler.adapter = categoryResultAdapter
            productRecycler.adapter = productResultAdapter
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            formCompleted.observe(viewLifecycleOwner) {
                addTrackerButton.isGone = it.not()
            }
            categoryNameEdittext.doOnTextChanged { text, _, _, _ ->
                if (text.toString().count() > 0) {
                    catViewModel.searchCategoryByWord(text.toString())
                } else {
                    catViewModel.showAllAsResult()
                }
            }
            catViewModel.searchResults.observe(viewLifecycleOwner) {
                categoryResultAdapter.submitList(it)
            }
            catViewModel.showAllAsResult()

            productNameEdittext.doOnTextChanged { text, _, _, _ ->
                if (text.toString().count() > 0) {
                    productViewModel.searchByText(text.toString())
                } else {
                    productViewModel.getAllProducts()
                }
            }
            productViewModel.searchResults.observe(viewLifecycleOwner) {
                productResultAdapter.submitList(it)
            }
            productViewModel.getAllProducts()

            viewModel.categoryGiven.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.selectedCategory?.let { category ->
                        selectedCategoryIcon.setImage(category.image, requireContext())
                    }
                }
                bind.categoryRecycler.isGone = it
                bind.selectedCategoryIcon.isGone = it.not()
                bind.categoryGivenCheck.isGone = it.not()
            }

            viewModel.productGiven.observe(viewLifecycleOwner){
                if(it){
                    viewModel.selectedProduct?.let { product->
                        selectedProductIcon.setImage(product.image, requireContext())
                    }
                }
                bind.productRecycler.isGone = it
                bind.selectedProductIcon.isGone = it.not()
                bind.productGivenCheck.isGone = it.not()
            }


            bind.expiryClickView.setOnClickListener {
                pickingDateTimeFor = LocalDateTimeFor.EXPIRY
                val cal = Calendar.getInstance()
                viewModel.expiryDate?.let { expiry->
                    DatePickerDialog(requireContext(),
                        R.style.datePickerTheme,this@AddTrackerV2,expiry.year,expiry.month.value-1,expiry.dayOfMonth).show()
                }?: kotlin.run {
                    DatePickerDialog(requireContext(),
                        R.style.datePickerTheme,this@AddTrackerV2,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(
                            Calendar.DAY_OF_MONTH)).show()
                }
            }
            bind.mfgClickView.setOnClickListener {
                pickingDateTimeFor = LocalDateTimeFor.MFG
                val cal = Calendar.getInstance()
                viewModel.mfgDate?.let { mfg->
                    DatePickerDialog(requireContext(),
                        R.style.datePickerTheme,this@AddTrackerV2,mfg.year,mfg.month.value-1,mfg.dayOfMonth).show()
                }?: kotlin.run {
                    DatePickerDialog(requireContext(),
                        R.style.datePickerTheme,this@AddTrackerV2,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(
                            Calendar.DAY_OF_MONTH)).show()
                }
            }
            bind.reminderDateClickView.setOnClickListener {
                pickingDateTimeFor = LocalDateTimeFor.REMINDER
                val cal = Calendar.getInstance()
                viewModel.reminderDate?.let { reminder->
                    DatePickerDialog(requireContext(),
                        R.style.datePickerTheme,this@AddTrackerV2,reminder.year,reminder.month.value-1,reminder.dayOfMonth).show()
                }?: kotlin.run {
                    DatePickerDialog(requireContext(),
                        R.style.datePickerTheme,this@AddTrackerV2,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(
                            Calendar.DAY_OF_MONTH)).show()
                }
            }

            viewModel.expiryGiven.observe(viewLifecycleOwner){
                expiryGivenCheck.isGone = it.not() && !viewModel.mfgGiven.value!!
                expiryDateLayout.isGone = it.not() && !viewModel.mfgGiven.value!!
                if(it) {
                    viewModel.expiryDate?.let { date ->
                        expiryDateEdittext.setText(
                            getString(
                                R.string.date_string_with_month_name,
                                date.month.name.substring(0, 3),
                                date.dayOfMonth,
                                date.year
                            )
                        )
                    }
                }else{
                    expiryDateEdittext.setText("")
                }
            }
            viewModel.mfgGiven.observe(viewLifecycleOwner){
                expiryGivenCheck.isGone = it.not() && !viewModel.expiryGiven.value!!
                expiryDateLayout.isGone = it.not() && !viewModel.expiryGiven.value!!
                if(it) {
                    viewModel.mfgDate?.let { date ->
                        mfgDateEdittext.setText(
                            getString(
                                R.string.date_string_with_month_name,
                                date.month.name.substring(0, 3),
                                date.dayOfMonth,
                                date.year
                            )
                        )
                    }
                }else{
                    expiryDateEdittext.setText("")
                }
            }
            viewModel.reminderGiven.observe(viewLifecycleOwner){
                reminderSetCheck.isGone = it.not()
                reminderDateLayout.isGone = it.not()
                if(it) {
                    viewModel.reminderDate?.let { date ->
                        reminderDateEdittext.setText(
                            getString(
                                R.string.date_string_with_month_name_and_time,
                                date.month.name.substring(0, 3),
                                date.dayOfMonth,
                                date.year,
                                TimeConvertor.getTime(date.hour,date.minute,true)
                            )
                        )
                    }
                }else{
                    expiryDateEdittext.setText("")
                }
            }

            var doRemind = false
            bind.reminderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    bind.remindDayBeforeCheckbox.id -> {
                        bind.reminderDateClickView.isEnabled = false
                        val beforeOneDay = viewModel.expiryDate?.minusDays(1)
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
                            viewModel.expiryDate?.let {
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
                bind.addTrackerButton.visibility = View.VISIBLE
            }

            bind.addTrackerButton.setOnClickListener {
                if(!doRemind){
                    viewModel.reminderDate = null
                }

                val tracker = Tracker(
                    productId = viewModel.selectedProduct?.product?.productId!!,
                    mfgDate = viewModel.mfgDate,
                    expiryDate = viewModel.expiryDate,
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



            createNewCategoryChip.setOnClickListener {

            }
            createNewProductChip.setOnClickListener {

            }
        }
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }

    override fun openInfoOfCategory(categoryAndImage: CategoryAndImage) {
        viewModel.selectedCategory = categoryAndImage
        viewModel.categoryGiven.postValue(true)
    }

    override fun openInfoOfProduct(productAndImage: ProductAndImage) {
        viewModel.selectedProduct = productAndImage
        viewModel.productGiven.postValue(true)
    }

    private fun moveToCreateNew(){

    }
}