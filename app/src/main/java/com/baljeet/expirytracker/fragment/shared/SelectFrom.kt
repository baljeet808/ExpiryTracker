package com.baljeet.expirytracker.fragment.shared

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ViewAnimator
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.listAdapters.OptionsAdapter
import com.baljeet.expirytracker.model.Category
import com.baljeet.expirytracker.model.Product
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.datetime.Month
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_TITLE = "Category"

class SelectFrom : Fragment(), OptionsAdapter.OnOptionSelectedListener {

    private  val constraintsBuilder =
        CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

    private val datePicker1 = MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme).
    setCalendarConstraints(constraintsBuilder.build()).setTitleText("Select Date").build()
    private val datePicker2 = MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme).
    setCalendarConstraints(constraintsBuilder.build()).setTitleText("Select Date").build()


    private lateinit var customBox: TextInputLayout
    private lateinit var customNameBox: TextInputLayout
    private lateinit var customEditBox: TextInputEditText
    private lateinit var customNameEditBox: TextInputEditText
    private lateinit var optionsRecycler: RecyclerView
    private lateinit var nameRecycler: RecyclerView
    private lateinit var selectedItemIcon: ImageView
    private lateinit var selectedNameIcon: ImageView
    private lateinit var adapter: OptionsAdapter
    private lateinit var nameAdapter: OptionsAdapter
    private lateinit var completedCheck1: ImageView
    private lateinit var completedCheck2: ImageView
    private lateinit var nameLayout: ConstraintLayout
    private lateinit var dateLayout: ConstraintLayout
    private lateinit var expiryEdittext : TextInputEditText
    private lateinit var mfgEdittext : TextInputEditText
    private lateinit var mfgClickView: View
    private lateinit var expiryClickView: View

    private lateinit var categoryClickView : View
    private lateinit var nameClickView : View
    private val categories = ArrayList<Category>()
    private val products = ArrayList<Product>()

    private val viewModel: SelectFromViewModel by activityViewModels()
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_from, container, false)

        customEditBox = view.findViewById(R.id.custom_edittext)
        customNameEditBox = view.findViewById(R.id.custom_name_edittext)
        customBox = view.findViewById(R.id.custom_box_layout)
        customNameBox = view.findViewById(R.id.custom_name_box_layout)
        optionsRecycler = view.findViewById(R.id.options_recycler)
        nameRecycler = view.findViewById(R.id.name_options_recycler)
        optionsRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        nameRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        selectedItemIcon = view.findViewById(R.id.selected_category_icon)
        selectedNameIcon = view.findViewById(R.id.selected_name_icon)

        nameLayout = view.findViewById(R.id.name_layout)
        dateLayout = view.findViewById(R.id.date_layout)
        completedCheck1 = view.findViewById(R.id.completed_check)
        completedCheck2 = view.findViewById(R.id.completed2_check)



        nameLayout.visibility = View.GONE
        completedCheck1.visibility = View.GONE
        optionsRecycler.visibility = View.VISIBLE

        categories.addAll(viewModel.getAllCategories())
        adapter = OptionsAdapter(categories, requireContext(), null, this, null)
        optionsRecycler.adapter = adapter

        products.addAll(viewModel.getProducts())
        nameAdapter = OptionsAdapter(null,requireContext(),null,this,products)
        nameRecycler.adapter = nameAdapter


        expiryClickView = view.findViewById(R.id.expiry_click_view)
        mfgClickView = view.findViewById(R.id.mfg_click_view)

        expiryEdittext = view.findViewById(R.id.expiry_date_edittext)
        mfgEdittext = view.findViewById(R.id.mfg_date_edittext)

        expiryClickView.setOnClickListener {  datePicker2.show(childFragmentManager,"tag1") }
        mfgClickView.setOnClickListener {  datePicker1.show(childFragmentManager,"tag2") }

        datePicker2.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.time = Date(it)
            expiryEdittext.setText(resources.getString(R.string.date_string,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH),cal.get(Calendar.YEAR)))
        }

        datePicker1.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.time = Date(it)
            mfgEdittext.setText(resources.getString(R.string.date_string,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH),cal.get(Calendar.YEAR)))
        }

        /*customEditBox.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                selectedItemIcon.visibility = View.GONE
                customBox.isEndIconVisible = true
                customBox.setEndIconActivated(true)
                optionsRecycler.visibility = View.VISIBLE
            }
        }
        customNameEditBox.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                selectedNameIcon.visibility = View.GONE
                customNameBox.isEndIconVisible = true
                nameRecycler.visibility = View.VISIBLE
            }
        }*/

        customEditBox.doOnTextChanged { _, _, _, _ ->

        }
        customNameEditBox.doOnTextChanged { _, _, _, _ ->

        }

        categoryClickView = view.findViewById(R.id.category_click_view)
        categoryClickView.setOnClickListener {
            completedCheck1.visibility = View.GONE
            optionsRecycler.visibility = View.VISIBLE
            /*nameLayout.visibility = View.GONE
            dateLayout.visibility = View.GONE
            completedCheck2.visibility = View.GONE
            nameAdapter.refreshAll(null)
            adapter.refreshAll(null)
            selectedItemIcon.visibility = View.GONE
            selectedNameIcon.visibility = View.GONE*/
        }
        nameClickView = view.findViewById(R.id.name_click_view)
        nameClickView.setOnClickListener {
            completedCheck2.visibility = View.GONE
            nameRecycler.visibility = View.VISIBLE
            /*dateLayout.visibility = View.GONE
            nameAdapter.refreshAll(null)
            selectedNameIcon.visibility = View.GONE*/
        }


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) =
            SelectFrom().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                }
            }
    }

    override fun onOptionSelected(position: Int, checkVisibility: Int, optionIsCategory : Boolean) {
        if(optionIsCategory) {
            if (checkVisibility == View.GONE) {
                customEditBox.setText(categories[position].categoryName)
                viewModel.setSelectedCategory(categories[position])
                selectedItemIcon.visibility = View.VISIBLE
                customBox.isEndIconVisible = false
                customNameBox.isEndIconVisible = false
                selectedItemIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        resources.getIdentifier(
                            categories[position].categoryIcon.imageUrl,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    optionsRecycler.visibility = View.GONE
                    nameLayout.visibility = View.VISIBLE
                    nameRecycler.visibility = View.VISIBLE
                    nameAdapter.updateProducts(viewModel.getProductsByCategory(categories[position]))
                    nameAdapter.refreshAll(null)
                    completedCheck1.visibility = View.VISIBLE
                    completedCheck2.visibility = View.GONE
                    customNameEditBox.text?.clear()
                    selectedNameIcon.visibility = View.GONE
                }, 200)
            } else {
                customEditBox.text?.clear()
                viewModel.setSelectedCategory(null)
                selectedItemIcon.visibility = View.GONE
                completedCheck1.visibility = ViewAnimator.GONE
            }
        }else{
            if (checkVisibility == View.GONE) {
                customNameEditBox.setText(products[position].name)
                viewModel.setSelectedProduct(products[position])
                selectedNameIcon.visibility = View.VISIBLE
                customNameBox.isEndIconVisible = false
                selectedNameIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        resources.getIdentifier(
                            products[position].image?.imageUrl,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    nameRecycler.visibility = View.GONE
                    completedCheck2.visibility = View.VISIBLE
                    dateLayout.visibility = View.VISIBLE
                }, 200)
            } else {
                customNameEditBox.text?.clear()
                viewModel.setSelectedProduct(null)
                selectedNameIcon.visibility = View.GONE
                completedCheck2.visibility = ViewAnimator.GONE
            }
        }

    }
}