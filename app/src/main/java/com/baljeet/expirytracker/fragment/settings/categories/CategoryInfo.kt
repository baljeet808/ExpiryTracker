package com.baljeet.expirytracker.fragment.settings.categories


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.DeletePopUpLayoutBinding
import com.baljeet.expirytracker.databinding.FragmentCategoryInfoBinding
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.SharedPref

class CategoryInfo : Fragment() {

    private lateinit var bind: FragmentCategoryInfoBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val navArgs: CategoryInfoArgs by navArgs()

    private lateinit var deleteDialog: AlertDialog
    private lateinit var dialogBuilder: AlertDialog.Builder

    private var nameChanged = MutableLiveData(false)

    private var changesAreValid = MediatorLiveData<Boolean>().apply {
        addSource(nameChanged) {
            this.value = validateName(bind.nameEdittext.text.toString())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPref.init(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCategoryInfoBinding.inflate(inflater, container, false)
        bind.apply {
            bind.closeBtn.setOnClickListener { activity?.onBackPressed() }
            val category = navArgs.categoryAndImage

            when (category.image.mimeType) {
                "asset" -> {
                    imageView.setPadding(60)
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            resources.getIdentifier(
                                category.image.imageUrl,
                                "drawable",
                                requireContext().packageName
                            )
                        )
                    )
                }
                else -> {
                    imageView.setPadding(0)
                    imageView.setImageBitmap(ImageConvertor.stringToBitmap(category.image.bitmap))
                }
            }
            nameEdittext.setText(category.category.categoryName)

            nameEdittext.doOnTextChanged { _, _, _, _ ->
                nameChanged.value = true
            }

            changesAreValid.observe(viewLifecycleOwner) {
                saveButton.isEnabled = it
            }


            saveButton.setOnClickListener {
                viewModel.updateCategory(
                    Category(
                        categoryId = category.category.categoryId,
                        categoryName = nameEdittext.text.toString(),
                        imageId = category.image.imageId,
                        isDeleted = false
                    )
                )
                activity?.onBackPressed()
            }
            deleteButton.setOnClickListener {
                showPopup()
            }

        }
        return bind.root
    }

    private fun showPopup() {

        dialogBuilder = AlertDialog.Builder(requireContext())
        val popUpBinding =  DeletePopUpLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        popUpBinding.apply {

            if(SharedPref.doNotAskBeforeDeletingCategory){
                progressBar.visibility = View.VISIBLE
                infoIcon.visibility = View.GONE
                deleteNote.text = getString(R.string.deleting_category)
                doNotShowAgainCheckbox.visibility = View.GONE
                deleteButton.visibility = View.GONE
            }


            deleteButton.setOnClickListener {

                if(doNotShowAgainCheckbox.isChecked){
                    SharedPref.doNotAskBeforeDeletingCategory = true
                }

                progressBar.visibility = View.VISIBLE
                infoIcon.visibility = View.GONE
                deleteNote.text = getString(R.string.deleting_category)
                doNotShowAgainCheckbox.visibility = View.GONE
                deleteButton.visibility = View.GONE

                productViewModel.deleteAllByCategoryId(navArgs.categoryAndImage.category.categoryId)
                viewModel.deleteCategory(navArgs.categoryAndImage.category)

                Handler(Looper.getMainLooper()).postDelayed({
                    if(deleteDialog.isShowing){
                        deleteDialog.dismiss()
                        activity?.onBackPressed()
                    }
                },1500)

            }
        }
        dialogBuilder.setView(popUpBinding.root)
        deleteDialog =dialogBuilder.setCancelable(true).create()
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

        if(SharedPref.doNotAskBeforeDeletingCategory){
            Handler(Looper.getMainLooper()).postDelayed({
                if(deleteDialog.isShowing){
                    productViewModel.deleteAllByCategoryId(navArgs.categoryAndImage.category.categoryId)
                    viewModel.deleteCategory(navArgs.categoryAndImage.category)
                    deleteDialog.dismiss()
                    activity?.onBackPressed()
                }
            },1500)
        }
    }


    private fun validateName(text: String): Boolean {
        bind.apply {
            return if (text.isBlank()) {
                bind.nameBox.error = "Required"
                bind.nameBox.isErrorEnabled = true
                false
            } else {
                if (text != navArgs.categoryAndImage.category.categoryName) {
                    val possibleResults = viewModel.readCategoryByName(text)
                    if (possibleResults.isNotEmpty()) {
                        nameBox.isErrorEnabled = true
                        nameBox.error = "This name is already in use."
                        false
                    } else {
                        nameBox.isErrorEnabled = false
                        nameBox.error = null
                        true
                    }
                } else {
                    true
                }

            }
        }
    }

}