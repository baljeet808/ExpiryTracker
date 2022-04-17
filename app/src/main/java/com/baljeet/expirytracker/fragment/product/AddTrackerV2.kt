package com.baljeet.expirytracker.fragment.product

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
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
    private val imageViewModel: ImageViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val viewModel: AddTrackerViewModel by activityViewModels()
    private val trackerViewModel: TrackerViewModel by viewModels()

    private var pickingDateTimeFor : LocalDateTimeFor = LocalDateTimeFor.MFG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        clearViewModel()
                        isEnabled = false
                        activity?.onBackPressed()
                    }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentAddTrackerV2Binding.inflate(inflater, container, false)
        bind.apply {
            categoryRecycler.layoutManager = GridLayoutManager(
                requireContext(),
                2 ,
                RecyclerView.HORIZONTAL,
                false
            )
            productRecycler.layoutManager = GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.HORIZONTAL,
                false
            )
            categoryResultAdapter = SearchResultsAdapter(requireContext(), imageViewModel,this@AddTrackerV2)
            productResultAdapter = ProductResultsAdapter(requireContext(), imageViewModel,this@AddTrackerV2)
            categoryRecycler.adapter = categoryResultAdapter
            productRecycler.adapter = productResultAdapter
            mfgDateEdittext.setText(
                viewModel.mfgDate?.let {  date->
                    resources.getString(
                        R.string.date_string_with_month_name,
                        date.month.name.substring(0, 3),
                        date.dayOfMonth,
                        date.year
                    )
                }
            )
            closeBtn.setOnClickListener {
                clearViewModel()
                activity?.onBackPressed()
            }
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEverything()
    }

    private fun observeEverything(){
        bind.apply {
            val formCompleted = MediatorLiveData<Boolean>().apply {
                this.value = false
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
            formCompleted.observe(viewLifecycleOwner) {
                addTrackerButton.isGone = it.not()
            }
            categoryNameEdittext.doOnTextChanged { text, _, _, _ ->
                if (text.toString().count() > 0) {
                    catViewModel.searchCategoryByWord(text.toString())
                } else {
                    catViewModel.showAllAsResult()
                }
                if(categoryRecycler.isGone){
                    categoryRecycler.isGone = false
                    viewModel.selectedProduct = null
                    viewModel.productGiven.postValue(false)
                }
            }
            categoryNameLayout.setEndIconOnClickListener {
                if(categoryRecycler.isGone){
                    categoryRecycler.isGone = false
                    viewModel.selectedProduct = null
                    viewModel.productGiven.postValue(false)
                    categoryNameEdittext.setText("")
                }
            }

            catViewModel.searchResults.observe(viewLifecycleOwner) {
                categoryResultAdapter.submitList(it)
            }
            catViewModel.showAllAsResult()

            productNameEdittext.doOnTextChanged { text, _, _, _ ->
                if (text.toString().count() > 0) {
                    productViewModel.searchByTextInCategory(text.toString(), viewModel.selectedCategory?.categoryId?:0)
                } else {
                    productViewModel.getAllProductsInCategory(viewModel.selectedCategory?.categoryId?:0)
                }
                if(productRecycler.isGone){
                    productRecycler.isGone = false
                }
            }

            productNameLayout.setEndIconOnClickListener {
                if(productRecycler.isGone){
                    productRecycler.isGone = false
                    productNameEdittext.setText("")
                }
            }
            productViewModel.searchResults.observe(viewLifecycleOwner) {
                productResultAdapter.submitList(it)
            }

            viewModel.categoryGiven.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.selectedCategory?.let { category ->
                        val image = imageViewModel.getImageById(category.imageId)
                        categoryNameEdittext.setText(category.categoryName)
                        selectedCategoryIcon.setImage(image, requireContext())
                        nameLayout.isGone = false
                        productViewModel.getAllProductsInCategory(category.categoryId)
                        viewModel.selectedProduct?: kotlin.run{
                            viewModel.productGiven.postValue(false)
                        }
                    }
                }
                categoryRecycler.isGone = it
                selectedCategoryIcon.isGone = it.not()
                categoryGivenCheck.isGone = it.not()
            }

            viewModel.productGiven.observe(viewLifecycleOwner){
                if(it){
                    viewModel.selectedProduct?.let { product->
                        val image = imageViewModel.getImageById(product.imageId)
                        productNameEdittext.setText(product.name)
                        selectedProductIcon.setImage(image, requireContext())
                    }
                    dateLayout.isGone = false
                    productNameEdittext.clearFocus()
                }
                productRecycler.isGone = it
                selectedProductIcon.isGone = it.not()
                productGivenCheck.isGone = it.not()
            }


            viewModel.expiryGiven.observe(viewLifecycleOwner){
                if(it) {
                    viewModel.expiryDate?.let { date ->
                        expiryDateEdittext.setText(
                            resources.getString(
                                R.string.date_string_with_month_name,
                                date.month.name.substring(0, 3),
                                date.dayOfMonth,
                                date.year
                            )
                        )
                    }
                    expiryGivenCheck.isGone = viewModel.mfgGiven.value != true
                    editReminderLayout.isGone = viewModel.mfgGiven.value != true
                }else{
                    expiryDateEdittext.setText("")
                    expiryGivenCheck.isGone = true
                }
            }
            viewModel.mfgGiven.observe(viewLifecycleOwner){
                expiryGivenCheck.isGone = it.not() && !viewModel.expiryGiven.value!!
                if(it) {
                    viewModel.mfgDate?.let { date ->
                        mfgDateEdittext.setText(
                            resources.getString(
                                R.string.date_string_with_month_name,
                                date.month.name.substring(0, 3),
                                date.dayOfMonth,
                                date.year
                            )
                        )
                    }
                    expiryGivenCheck.isGone = viewModel.expiryGiven.value != true
                    editReminderLayout.isGone = viewModel.expiryGiven.value != true
                }else{
                    mfgDateEdittext.setText("")
                    expiryGivenCheck.isGone = true
                }
            }
            viewModel.reminderGiven.observe(viewLifecycleOwner){
                reminderSetCheck.isGone = it.not()
                reminderDateLayout.isGone = it.not()
                if(it) {
                    viewModel.reminderDate?.let { date ->
                        reminderDateEdittext.setText(
                            resources.getString(
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


            var doRemind = false
            bind.reminderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    bind.remindDayBeforeCheckbox.id -> {
                        bind.reminderDateClickView.isEnabled = true
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
                        viewModel.reminderGiven.postValue(true)
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
                        viewModel.reminderGiven.postValue(true)
                    }
                    bind.doNotRemindCheckbox.id -> {
                        bind.reminderDateClickView.isEnabled = false
                        bind.reminderDateEdittext.setText(R.string.do_not_remind)
                        doRemind = false
                        viewModel.reminderGiven.postValue(true)
                    }
                }
            }

            bind.addTrackerButton.setOnClickListener {
                if(!doRemind){
                    viewModel.reminderDate = null
                }

                val tracker = Tracker(
                    productId = viewModel.selectedProduct?.productId!!,
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
                moveToCreateNew(true)
            }

            createNewProductChip.setOnClickListener {
                moveToCreateNew(false)
            }
        }
    }


    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.reminderDate = viewModel.reminderDate?.let {
            LocalDateTime.of(it.year,it.month,it.dayOfMonth,hourOfDay,minute)
        }
        viewModel.reminderGiven.postValue(true)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        when(pickingDateTimeFor){
            LocalDateTimeFor.MFG->{

                viewModel.mfgDate = LocalDateTime.of(
                        year,
                        month+1,
                        dayOfMonth,
                        0,
                        1
                    )
                viewModel.mfgGiven.postValue(true)
            }
            LocalDateTimeFor.EXPIRY->{
                viewModel.expiryDate = LocalDateTime.of(
                        year,
                        month+1,
                        dayOfMonth,
                        23,
                        58
                    )
                viewModel.expiryGiven.postValue(true)
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
                val currentTime = LocalDateTime.now()
                previousDate?.let {
                    TimePickerDialog(requireContext(),this,previousDate.hour,previousDate.minute,false).show()
                }?: kotlin.run {
                    TimePickerDialog(requireContext(),this,currentTime.hour,currentTime.minute,false).show()
                }
            }
        }
    }

    override fun openInfoOfCategory(category: Category) {
        viewModel.selectedCategory = category
        viewModel.categoryGiven.postValue(true)
    }

    override fun openInfoOfProduct(product: Product) {
        viewModel.selectedProduct = product
        viewModel.productGiven.postValue(true)
    }

    private fun moveToCreateNew(optionIsCategory : Boolean){
        val name = if(optionIsCategory){
            if(bind.categoryNameEdittext.text.isNullOrBlank()){
                null
            }else{
                bind.categoryNameEdittext.text.toString()
            }
        }else{
            if(bind.productNameEdittext.text.isNullOrBlank()){
                null
            }else{
                bind.productNameEdittext.text.toString()
            }
        }
        Navigation.findNavController(requireView()).navigate(
            AddTrackerV2Directions.actionAddTrackerV2ToCreateCustom(
                itemType = if (optionIsCategory) "Category" else "Product",
                selectedCategory = if (optionIsCategory) null else viewModel.selectedCategory ,
                tempName = name
            )
        )
    }

    private fun  clearViewModel(){
        viewModel.apply {
            productGiven.postValue(false)
            categoryGiven.postValue(false)
            mfgGiven.postValue(true)
            expiryGiven.postValue(false)
            reminderGiven.postValue(false)
            selectedProduct = null
            selectedCategory = null
            mfgDate = LocalDateTime.now()
            expiryDate = null
            reminderDate = null
        }
    }
}