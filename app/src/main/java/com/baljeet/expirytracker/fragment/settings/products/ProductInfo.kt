package com.baljeet.expirytracker.fragment.settings.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.FragmentProductInfoBinding
import com.baljeet.expirytracker.util.ImageConvertor


class ProductInfo : Fragment() {

    private lateinit var bind: FragmentProductInfoBinding
    private val viewModel: ProductViewModel by viewModels()
    private val navArgs: ProductInfoArgs by navArgs()


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
            fragmentTitleText.text = product.product.name

            when (product.image.mimeType) {
                "asset" -> {
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
                    imageView.setImageBitmap(ImageConvertor.stringToBitmap(product.image.bitmap))
                }
            }
            nameEdittext.setText(product.product.name)

            nameEdittext.doOnTextChanged { _, _, _, _ ->
                nameChanged.value = true
            }

            changesAreValid.observe(viewLifecycleOwner) {
                saveButton.isGone = !it
                saveLabel.isGone = !it
            }

            saveButton.setOnClickListener {
                viewModel.updateProduct(
                    Product(
                        categoryId = product.product.categoryId,
                        name = nameEdittext.text.toString(),
                        imageId = product.image.imageId,
                        productId = product.product.productId
                    )
                )
                activity?.onBackPressed()
            }
            deleteButton.setOnClickListener {
                //TODO: think how to delete category safely
                activity?.onBackPressed()
                Toast.makeText(requireContext(), "no deleted yet", Toast.LENGTH_SHORT).show()
            }

        }
        return bind.root
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