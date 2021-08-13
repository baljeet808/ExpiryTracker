package com.baljeet.expirytracker.fragment.shared

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.baljeet.expirytracker.data.*
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.listAdapters.OptionsAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.datetime.Month
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_TITLE = "Category"

class SelectFrom : Fragment(), OptionsAdapter.OnOptionSelectedListener {

    private val datePicker1 = MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme)
        .setTitleText("Manufactured Date").build()
    private val datePicker2 = MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme)
        .setTitleText("Expiry Date").build()


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
    private lateinit var completedCheck3: ImageView
    private lateinit var nameLayout: ConstraintLayout
    private lateinit var dateLayout: ConstraintLayout
    private lateinit var expiryEdittext : TextInputEditText
    private lateinit var mfgEdittext : TextInputEditText
    private lateinit var mfgClickView: View
    private lateinit var expiryClickView: View
    private lateinit var addProductBtn : Button

    private lateinit var categoryClickView : View
    private lateinit var nameClickView : View
    private val categoriesWithImages = ArrayList<CategoryAndImage>()
    private val productsWithImages = ArrayList<ProductAndImage>()

    private val viewModel: SelectFromViewModel by activityViewModels()
    private val categoryVM : CategoryViewModel by activityViewModels()
    private val productVM : ProductViewModel by activityViewModels()
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
        completedCheck3 = view.findViewById(R.id.completed3_check)

        nameLayout.visibility = View.GONE
        completedCheck1.visibility = View.GONE
        optionsRecycler.visibility = View.VISIBLE

        adapter = OptionsAdapter(categoriesWithImages, requireContext(), null, this, null)
        optionsRecycler.adapter = adapter
        categoryVM.readAllCategoriesWithImages.observe(viewLifecycleOwner, {
            categoriesWithImages.clear()
            categoriesWithImages.addAll(it)
            adapter.setCategoriesWithImages(it)
        })

        nameAdapter = OptionsAdapter(null,requireContext(),null,this,productsWithImages)
        nameRecycler.adapter = nameAdapter

        expiryClickView = view.findViewById(R.id.expiry_click_view)
        mfgClickView = view.findViewById(R.id.mfg_click_view)

        expiryEdittext = view.findViewById(R.id.expiry_date_edittext)
        mfgEdittext = view.findViewById(R.id.mfg_date_edittext)

        expiryClickView.setOnClickListener {  datePicker2.show(childFragmentManager,"tag1") }
        mfgClickView.setOnClickListener {  datePicker1.show(childFragmentManager,"tag2") }

        datePicker2.addOnPositiveButtonClickListener { its->
            viewModel.setExpiryDate(its)
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.time = Date(its)
            expiryEdittext.setText(resources.getString(R.string.date_string_with_month_name,
                                                        Month.of(cal.get(Calendar.MONTH)+1).name.substring(0,3),
                                                        cal.get(Calendar.DAY_OF_MONTH),
                                                        cal.get(Calendar.YEAR)))
            mfgEdittext.text?.let {
                if(it.isNotEmpty()){
                    completedCheck3.visibility = View.VISIBLE
                    addProductBtn.visibility = View.VISIBLE
                }}
        }

        datePicker1.addOnPositiveButtonClickListener { its ->
            viewModel.setMfgDate(its)
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.time = Date(its)
            mfgEdittext.setText(resources.getString(R.string.date_string_with_month_name,
                Month.of(cal.get(Calendar.MONTH)+1).name.substring(0,3),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.YEAR)))
            expiryEdittext.text?.let {
                if(it.isNotEmpty()){
                    completedCheck3.visibility = View.VISIBLE
                    addProductBtn.visibility = View.VISIBLE
                }}
        }

        customEditBox.doOnTextChanged { _, _, _, _ ->

        }
        customNameEditBox.doOnTextChanged { _, _, _, _ ->

        }

        categoryClickView = view.findViewById(R.id.category_click_view)
        categoryClickView.setOnClickListener {
            completedCheck1.visibility = View.GONE
            optionsRecycler.visibility = View.VISIBLE
        }
        nameClickView = view.findViewById(R.id.name_click_view)
        nameClickView.setOnClickListener {
            completedCheck2.visibility = View.GONE
            nameRecycler.visibility = View.VISIBLE
        }

        addProductBtn = view.findViewById(R.id.add_product_button)
        addProductBtn.setOnClickListener{
            activity?.onBackPressed()
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
                customEditBox.setText(categoriesWithImages[position].category.categoryName)
                viewModel.setSelectedCategory(categoriesWithImages[position])
                selectedItemIcon.visibility = View.VISIBLE
                customBox.isEndIconVisible = false
                customNameBox.isEndIconVisible = false
                selectedItemIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        resources.getIdentifier(
                            categoriesWithImages[position].image.imageUrl,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    optionsRecycler.visibility = View.GONE
                    nameLayout.visibility = View.VISIBLE
                    nameRecycler.visibility = View.VISIBLE
                    productVM.readProductWithImageById(viewModel.getSelectedCategory()?.category?.categoryId!!)
                    productVM.productsByCategoryWithImage.observe(viewLifecycleOwner, {
                        productsWithImages.clear()
                        productsWithImages.addAll(it)
                        nameAdapter.setProducts(it)
                    })
                    nameAdapter.refreshAll(null)
                    completedCheck1.visibility = View.VISIBLE
                    completedCheck2.visibility = View.GONE
                    completedCheck3.visibility = View.GONE
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
                customNameEditBox.setText(productsWithImages[position].product.name)
                viewModel.setSelectedProduct(productsWithImages[position])
                selectedNameIcon.visibility = View.VISIBLE
                customNameBox.isEndIconVisible = false
                selectedNameIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        resources.getIdentifier(
                            productsWithImages[position].image.imageUrl,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    nameRecycler.visibility = View.GONE
                    completedCheck2.visibility = View.VISIBLE
                    completedCheck3.visibility = View.GONE
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