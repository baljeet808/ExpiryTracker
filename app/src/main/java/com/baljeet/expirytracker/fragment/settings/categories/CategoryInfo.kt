package com.baljeet.expirytracker.fragment.settings.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.databinding.FragmentCategoryInfoBinding
import com.baljeet.expirytracker.util.ImageConvertor

class CategoryInfo : Fragment() {

    private lateinit var bind : FragmentCategoryInfoBinding
    private val viewModel : CategoryViewModel by viewModels()
    private val navArgs : CategoryInfoArgs by navArgs()

    private var nameChanged = MutableLiveData(false)

    private var changesAreValid = MediatorLiveData<Boolean>().apply {
        addSource(nameChanged){
             this.value = validateName(bind.nameEdittext.text.toString())
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCategoryInfoBinding.inflate(inflater, container, false)
        bind.apply {
           bind.closeBtn.setOnClickListener { activity?.onBackPressed() }
            val category = navArgs.categoryAndImage
            fragmentTitleText.text = category.category.categoryName

            when(category.image.mimeType){
                "asset"->{
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
                else->{
                    imageView.setImageBitmap(ImageConvertor.stringToBitmap(category.image.bitmap))
                }
            }
            nameEdittext.setText(category.category.categoryName)

            nameEdittext.doOnTextChanged { _, _, _, _ ->
                nameChanged.value = true
            }

            changesAreValid.observe(viewLifecycleOwner){
                saveButton.isGone = !it
                saveLabel.isGone = !it
            }


            saveButton.setOnClickListener {
                 viewModel.updateCategory(Category(
                     categoryId = category.category.categoryId,
                     categoryName = nameEdittext.text.toString(),
                     imageId = category.image.imageId
                 ))
                activity?.onBackPressed()
            }
            deleteButton.setOnClickListener {
                //TODO: think how to delete category safely
                activity?.onBackPressed()
                Toast.makeText(requireContext(),"no deleted yet", Toast.LENGTH_SHORT).show()
            }

        }
        return bind.root
    }

    private fun validateName(text : String): Boolean{
        bind.apply {
            return  if(text.isBlank()){
                bind.nameBox.error = "Required"
                bind.nameBox.isErrorEnabled = true
                false
            }else{
                if(text != navArgs.categoryAndImage.category.categoryName) {
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
                }else{
                    true
                }

            }
        }
    }

}