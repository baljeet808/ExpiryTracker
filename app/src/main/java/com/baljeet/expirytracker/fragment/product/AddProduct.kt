package com.baljeet.expirytracker.fragment.product

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewAnimator
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentAddProductBinding
import com.baljeet.expirytracker.fragment.shared.SelectFromViewModel
import com.baljeet.expirytracker.listAdapters.OptionsAdapter
import com.baljeet.expirytracker.util.Constants
import com.dwellify.contractorportal.util.TimeConvertor
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.toLocalDateTime

class AddProduct : Fragment() , OptionsAdapter.OnOptionSelectedListener{

    private val datePicker1 = MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme)
        .setTitleText("Manufactured Date").build()
    private val datePicker2 = MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme)
        .setTitleText("Expiry Date").build()

    private val categoriesWithImages = ArrayList<CategoryAndImage>()
    private val productsWithImages = ArrayList<ProductAndImage>()

    private val viewModel: SelectFromViewModel by activityViewModels()
    private val categoryVM : CategoryViewModel by activityViewModels()
    private val productVM : ProductViewModel by activityViewModels()
    private val trackerViewModel : TrackerViewModel by activityViewModels()



    private lateinit var adapter: OptionsAdapter
    private lateinit var nameAdapter: OptionsAdapter

    private lateinit var bind : FragmentAddProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentAddProductBinding.inflate(inflater,container,false)

        bind.closeBtn.setOnClickListener { activity?.onBackPressed() }


        bind.optionsRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        bind.nameOptionsRecycler.layoutManager = GridLayoutManager(requireContext(), 3)


        bind.nameLayout.visibility = View.GONE
        bind.completedCheck.visibility = View.GONE
        bind.optionsRecycler.visibility = View.VISIBLE

        adapter = OptionsAdapter(categoriesWithImages, requireContext(), null, this, null)
        bind.optionsRecycler.adapter = adapter
        categoryVM.readAllCategoriesWithImages.observe(viewLifecycleOwner, {
            categoriesWithImages.clear()
            categoriesWithImages.addAll(it)
            adapter.setCategoriesWithImages(it)
        })

        nameAdapter = OptionsAdapter(null,requireContext(),null,this,productsWithImages)
        bind.nameOptionsRecycler.adapter = nameAdapter


        bind.expiryClickView.setOnClickListener {  datePicker2.show(childFragmentManager,"tag1") }
        bind.mfgClickView.setOnClickListener {  datePicker1.show(childFragmentManager,"tag2") }

        datePicker2.addOnPositiveButtonClickListener { its->
            val expiryInstant = TimeConvertor.fromEpochMillisecondsToInstant(its)

            val expiryDate = expiryInstant.toLocalDateTime(Constants.TIMEZONE)

            viewModel.setExpiryDate(LocalDateTime(expiryDate.year,expiryDate.monthNumber,expiryDate.dayOfMonth,expiryDate.hour,expiryDate.minute,0,0))
            expiryDate.let {
                bind.expiryDateEdittext.setText(resources.getString(R.string.date_string_with_month_name,
                    Month.of(it.monthNumber).name.substring(0,3),
                    it.dayOfMonth,
                    it.year))
            }

            bind.mfgDateEdittext.text?.let {
                if(it.isNotEmpty()){
                    bind.completed2Check.visibility = View.VISIBLE
                    bind.addProductButton.visibility = View.VISIBLE
                }}
        }

        datePicker1.addOnPositiveButtonClickListener { its ->
            val mfgInstant = TimeConvertor.fromEpochMillisecondsToInstant(its)
            val mfgDate = mfgInstant.toLocalDateTime(Constants.TIMEZONE)

            viewModel.setMfgDate(LocalDateTime(mfgDate.year,mfgDate.monthNumber,mfgDate.dayOfMonth,mfgDate.hour,mfgDate.minute,0,0))
            mfgDate.let {
                bind.mfgDateEdittext.setText(resources.getString(R.string.date_string_with_month_name,
                    Month.of(it.monthNumber).name.substring(0,3),
                    it.dayOfMonth,
                    it.year))
            }
            bind.expiryDateEdittext.text?.let {
                if(it.isNotEmpty()){
                    bind.completed3Check.visibility = View.VISIBLE
                    bind.addProductButton.visibility = View.VISIBLE
                }}
        }

        bind.categoryClickView.setOnClickListener {
            bind.completedCheck.visibility = View.GONE
            bind.optionsRecycler.visibility = View.VISIBLE
        }
        bind.nameClickView.setOnClickListener {
            bind.completed2Check.visibility = View.GONE
            bind.nameOptionsRecycler.visibility = View.VISIBLE
        }

        bind.addProductButton.setOnClickListener{
            val tracker = Tracker(0,
                viewModel.getSelectedProduct()?.product?.productId!!,
                viewModel.getMfgDate(),
                viewModel.getExpiryDate(),
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
            activity?.onBackPressed()
        }
        return bind.root
    }

    override fun onOptionSelected(position: Int, checkVisibility: Int, optionIsCategory : Boolean) {
        
        if(position == -1){
            Navigation.findNavController(requireView()).navigate(AddProductDirections.actionAddProductToCreateCustom(if(optionIsCategory) "Category" else "Product"))
        }else {

            if (optionIsCategory) {
                if (checkVisibility == View.GONE) {
                    bind.customEdittext.setText(categoriesWithImages[position].category.categoryName)
                    viewModel.setSelectedCategory(categoriesWithImages[position])
                    bind.selectedCategoryIcon.visibility = View.VISIBLE
                    bind.customBoxLayout.isEndIconVisible = false
                    bind.customNameBoxLayout.isEndIconVisible = false
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
                    Handler(Looper.getMainLooper()).postDelayed({
                        bind.optionsRecycler.visibility = View.GONE
                        bind.nameLayout.visibility = View.VISIBLE
                        bind.nameOptionsRecycler.visibility = View.VISIBLE
                        productVM.readProductWithImageById(viewModel.getSelectedCategory()?.category?.categoryId!!)
                        productVM.productsByCategoryWithImage.observe(viewLifecycleOwner, {
                            productsWithImages.clear()
                            productsWithImages.addAll(it)
                            nameAdapter.setProducts(it)
                        })
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
}