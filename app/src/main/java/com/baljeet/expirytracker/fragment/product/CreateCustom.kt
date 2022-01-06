package com.baljeet.expirytracker.fragment.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.FragmentCreateCustomBinding

class CreateCustom : Fragment() {

    private lateinit var bind : FragmentCreateCustomBinding
    private val navArgs : CreateCustomArgs by navArgs()

    private val categoryViewModel : CategoryViewModel by viewModels()
    private val productViewModel :  ProductViewModel by viewModels()

    private val nameIsGiven = MutableLiveData<Boolean>(false)
    private val imageIsSelected = MutableLiveData<Boolean>(false)

    private val formIsComplete = MediatorLiveData<Boolean>().apply {
        this.value = false
        addSource(nameIsGiven){
            this.value = it && imageIsSelected.value!!
        }
        addSource(imageIsSelected){
            this.value = it && nameIsGiven.value!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCreateCustomBinding.inflate(inflater, container, false)
        bind.apply {
            closeBtn.setOnClickListener {
                activity?.onBackPressed()
            }
            fragmentTitleText.text = fragmentTitleText.text.toString().plus(" ").plus(navArgs.itemType)
            nameBox.hint = navArgs.itemType.plus(" Name")
            imageHeading.text = imageHeading.text.toString().plus(" ").plus(navArgs.itemType)

            formIsComplete.observe(viewLifecycleOwner, {
                addProductButton.isGone = it
            })

            nameEdittext.doOnTextChanged { text, _, _, count ->
                if(count >2){
                    when(navArgs.itemType){
                        "Category"->{
                            val possibleResults = categoryViewModel.readCategoryByName(text.toString())
                            if(possibleResults.isNotEmpty()){
                                nameBox.isErrorEnabled = true
                                nameBox.error = "This name is already used. Try Again"
                                nameIsGiven.postValue(true)
                            }
                            else{
                                nameBox.isErrorEnabled = false
                                nameBox.error = null
                                nameIsGiven.postValue(false)
                            }
                        }
                        else->{
                            val possibleResults = productViewModel.readProductByName(text.toString())
                            if(possibleResults.isNotEmpty()){
                                nameBox.isErrorEnabled = true
                                nameBox.error = "This name is already used. Try Again"
                                nameIsGiven.postValue(true)
                            }  else{
                                nameBox.isErrorEnabled = false
                                nameBox.error = null
                                nameIsGiven.postValue(false)
                            }
                        }
                    }
                }else{
                    nameBox.isErrorEnabled = true
                    nameBox.error = "At least 3 character required."
                    nameIsGiven.postValue(false)
                }
            }

            

        }
        return bind.root
    }

}