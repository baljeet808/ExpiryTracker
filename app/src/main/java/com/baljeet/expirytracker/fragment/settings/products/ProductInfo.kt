package com.baljeet.expirytracker.fragment.settings.products

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.DeletePopUpLayoutBinding
import com.baljeet.expirytracker.databinding.FragmentProductInfoBinding
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.SharedPref


class ProductInfo : Fragment() {

    private lateinit var bind: FragmentProductInfoBinding
    private val viewModel: ProductViewModel by viewModels()
    private val navArgs: ProductInfoArgs by navArgs()


    private lateinit var deleteDialog: AlertDialog
    private lateinit var dialogBuilder: AlertDialog.Builder

    private var nameChanged = MutableLiveData(false)

    private var changesAreValid = MediatorLiveData<Boolean>().apply {
        addSource(nameChanged) {
            this.value = validateName(bind.nameEdittext.text.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentProductInfoBinding.inflate(inflater, container, false)
        bind.apply {
            closeBtn.setOnClickListener { activity?.onBackPressed() }
            val product = navArgs.productAndImage

            when (product.image.mimeType) {
                "asset" -> {
                    imageView.setPadding(60)
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            resources.getIdentifier(
                                product.image.imageUrl,
                                "drawable",
                                requireContext().packageName
                            )
                        )
                    )
                }
                else -> {
                    imageView.setPadding(0)
                    imageView.setImageBitmap(ImageConvertor.stringToBitmap(product.image.bitmap))
                }
            }
            nameEdittext.setText(product.product.name)

            nameEdittext.doOnTextChanged { _, _, _, _ ->
                nameChanged.value = true
            }

            changesAreValid.observe(viewLifecycleOwner) {
                saveButton.isEnabled = it
            }

            saveButton.setOnClickListener {
                viewModel.updateProduct(
                    Product(
                        categoryId = product.product.categoryId,
                        name = nameEdittext.text.toString(),
                        imageId = product.image.imageId,
                        productId = product.product.productId,
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
             deleteNote.text = getString(R.string.product_delete_note)
            if(SharedPref.doNotAskBeforeDeletingCategory){
                progressBar.visibility = View.VISIBLE
                infoIcon.visibility = View.GONE
                deleteNote.text = getString(R.string.deleting_product)
                doNotShowAgainCheckbox.visibility = View.GONE
                deleteButton.visibility = View.GONE
            }


            deleteButton.setOnClickListener {

                if(doNotShowAgainCheckbox.isChecked){
                    SharedPref.doNotAskBeforeDeletingCategory = true
                }

                progressBar.visibility = View.VISIBLE
                infoIcon.visibility = View.GONE
                deleteNote.text = getString(R.string.deleting_product)
                doNotShowAgainCheckbox.visibility = View.GONE
                deleteButton.visibility = View.GONE

                viewModel.deleteProduct(navArgs.productAndImage.product)

                Handler(Looper.getMainLooper()).postDelayed({
                    if(deleteDialog.isShowing){
                        deleteDialog.dismiss()
                        activity?.onBackPressed()
                    }
                },1000)
            }
        }
        dialogBuilder.setView(popUpBinding.root)
        deleteDialog =dialogBuilder.setCancelable(true).create()
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

        if(SharedPref.doNotAskBeforeDeletingCategory){
            Handler(Looper.getMainLooper()).postDelayed({
                if(deleteDialog.isShowing){
                    viewModel.deleteProduct(navArgs.productAndImage.product)
                    deleteDialog.dismiss()
                    activity?.onBackPressed()
                }
            },1000)
        }
    }


    private fun validateName(text: String): Boolean {
        bind.apply {
            return if (text.isBlank()) {
                bind.nameBox.error = "Required"
                bind.nameBox.isErrorEnabled = true
                false
            } else {
                if (text != navArgs.productAndImage.product.name) {
                    val possibleResults = viewModel.readProductByName(text)
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