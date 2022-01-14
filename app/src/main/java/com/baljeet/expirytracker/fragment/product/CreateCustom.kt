package com.baljeet.expirytracker.fragment.product

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.FragmentCreateCustomBinding
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.getContentType
import com.baljeet.expirytracker.util.getFileName
import java.io.File

class CreateCustom : Fragment() {

    private lateinit var bind : FragmentCreateCustomBinding
    private val navArgs : CreateCustomArgs by navArgs()


    private val categoryViewModel : CategoryViewModel by viewModels()
    private val productViewModel :  ProductViewModel by viewModels()
    private val imageViewModel : ImageViewModel by viewModels()

    private val viewModel : CustomViewModel by activityViewModels()

    private val nameIsGiven = MutableLiveData(false)
    private val imageIsSelected = MutableLiveData(false)

    private val formIsComplete = MediatorLiveData<Boolean>().apply {
        this.value = false
        addSource(nameIsGiven){
            this.value = it && imageIsSelected.value!!
        }
        addSource(imageIsSelected){
            this.value = it && nameIsGiven.value!!
        }
    }

    private val pickAction = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val image = Image(
                imageId  = 0,
                imageUrl = it.toString(),
                imageName = requireContext().contentResolver.getFileName(it),
                alt = "image",
                mimeType = requireContext().contentResolver.getContentType(it),
                uri = it,
                bitmap = ""
            )
            openEditor(image)
        }
    }

    private var latestTmpUri: Uri? = null


    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { u ->
                    val image = Image(
                        imageId = 0,
                        imageUrl = u.toString(),
                        imageName = requireContext().contentResolver.getFileName(u),
                        alt = "image",
                        mimeType = "image/jpeg",
                        uri = u ,
                        bitmap =""
                    )
                    openEditor(image)
                } 
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clearEverything()
                isEnabled = false
                activity?.onBackPressed()
            }
        })
    }

    private fun clearEverything(){
        viewModel.croppedImage = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCreateCustomBinding.inflate(inflater, container, false)
        bind.apply {
            closeBtn.setOnClickListener {
                clearEverything()
                activity?.onBackPressed()
            }
            fragmentTitleText.text = fragmentTitleText.text.toString().plus(" ").plus(navArgs.itemType)
            nameBox.hint = navArgs.itemType.plus(" Name")
            imageHeading.text = imageHeading.text.toString().plus(" ").plus(navArgs.itemType)

            formIsComplete.observe(viewLifecycleOwner, {
                addProductButton.isEnabled = it
            })

            navArgs.selectedCategory?.let {
                Toast.makeText(requireContext(), it.categoryName, Toast.LENGTH_SHORT).show()
            }?: kotlin.run {
                Toast.makeText(requireContext(),"category is null", Toast.LENGTH_SHORT).show()
            }

            viewModel.croppedImage?.let {
                showImageInPreview(it)
            }

            nameEdittext.doOnTextChanged { text, _, _, _ ->
                if(text.toString().count() >2){
                    previewTitle.text = text.toString()
                    when(navArgs.itemType){
                        "Category"->{
                            val possibleResults = categoryViewModel.readCategoryByName(text.toString())
                            if(possibleResults.isNotEmpty()){
                                nameBox.isErrorEnabled = true
                                nameBox.error = "This name is already used. Try Again"
                                nameIsGiven.postValue(false)
                            }
                            else{
                                nameBox.isErrorEnabled = false
                                nameBox.error = null
                                nameIsGiven.postValue(true)
                            }
                        }
                        else->{
                            val possibleResults = productViewModel.readProductByName(text.toString())
                            if(possibleResults.isNotEmpty()){
                                nameBox.isErrorEnabled = true
                                nameBox.error = "This name is already used. Try Again"
                                nameIsGiven.postValue(false)
                            }  else{
                                nameBox.isErrorEnabled = false
                                nameBox.error = null
                                nameIsGiven.postValue(true)
                            }
                        }
                    }
                }else{
                    nameBox.isErrorEnabled = true
                    nameBox.error = "At least 3 character required."
                    nameIsGiven.postValue(false)
                }
            }

            cameraCard.setOnClickListener {
                val photoFile = File.createTempFile(
                    "IMG_",
                    ".jpg",
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )

                latestTmpUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )
                takeImageResult.launch(latestTmpUri)
            }

            galleryCard.setOnClickListener {
                pickAction.launch("image/*")
            }

            addProductButton.setOnClickListener {
                  imageViewModel.addImage(viewModel.croppedImage!!)
                  val image = imageViewModel.getImageByName(viewModel.croppedImage?.imageName!!)[0]
                  when(navArgs.itemType){
                      "Category"->{
                          categoryViewModel.addCategory(Category(
                              categoryId = 0,
                              categoryName = nameEdittext.text.toString(),
                              imageId = image.imageId
                          ))
                      }
                      else->{
                          productViewModel.addProduct(Product(
                              productId = 0,
                              name = nameEdittext.text.toString(),
                              categoryId = navArgs.selectedCategory?.categoryId!!,
                              imageId = image.imageId
                          ))
                          Log.d("Log for - ","ran completely")
                      }
                  }
                activity?.onBackPressed()
            }

        }
        return bind.root
    }

    private fun openEditor(image : Image){
        Navigation.findNavController(requireView()).navigate(CreateCustomDirections.actionCreateCustomToImageEditor(image))
    }

    private fun showImageInPreview(image : Image){
        bind.apply {
            optionImage.setImageBitmap(ImageConvertor.stringToBitmap(image.bitmap))
            imageIsSelected.postValue(true)
        }
    }

}