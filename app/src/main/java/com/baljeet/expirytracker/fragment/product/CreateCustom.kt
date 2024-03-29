package com.baljeet.expirytracker.fragment.product

import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.view.setPadding
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

    private val trackerViewModel :AddTrackerViewModel by activityViewModels()

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
            viewModel.selectedImage = image
            openEditor()
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
                    viewModel.selectedImage = image
                    openEditor()
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
        viewModel.once = 1
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

            formIsComplete.observe(viewLifecycleOwner) {
                addProductButton.isEnabled = it
            }
            viewModel.croppedImage?.let {
                showImageInPreview(it)
            }
            navArgs.tempName?.let {
                if(viewModel.once == 1) {
                    nameEdittext.setText(it)
                    viewModel.once = 2
                }
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

            iconsCard.setOnClickListener{
                Navigation.findNavController(requireView()).navigate(CreateCustomDirections.actionCreateCustomToIconsGallery())
            }

            addProductButton.setOnClickListener {


                val imageId = viewModel.croppedImage?.let {
                        if(it.mimeType == "asset"){
                            it.imageId
                        }else{
                            imageViewModel.addImage(it)
                            imageViewModel.getImageByName(it.imageName).imageId
                        }
                    }

                imageId?.let {
                    when (navArgs.itemType) {
                        "Category" -> {
                            categoryViewModel.addCategory(
                                Category(
                                    categoryId = 0,
                                    categoryName = nameEdittext.text.toString(),
                                    imageId = imageId,
                                    false
                                )
                            )
                        }
                        else -> {
                            productViewModel.addProduct(
                                Product(
                                    productId = 0,
                                    name = nameEdittext.text.toString(),
                                    categoryId = navArgs.selectedCategory?.categoryId!!,
                                    imageId = imageId,
                                    false
                                )
                            )
                            Log.d("Log for - ", "ran completely")
                        }
                    }
                }
                Handler(Looper.myLooper()!!).postDelayed({
                if(navArgs.itemType == "Category"){
                        val categories = categoryViewModel.readCategoryByName(nameEdittext.text.toString())
                        if(categories.isNotEmpty()){
                            trackerViewModel.selectedCategory = categories.firstOrNull()
                            trackerViewModel.categoryGiven.postValue(true)
                        }
                }else{
                    trackerViewModel.categoryGiven.postValue(true)
                    trackerViewModel.selectedCategory = navArgs.selectedCategory
                    val products = productViewModel.readProductByName(nameEdittext.text.toString())
                    if(products.isNotEmpty()){
                        trackerViewModel.selectedProduct = products.firstOrNull()
                        trackerViewModel.productGiven.postValue(true)
                    }
                }
                activity?.onBackPressed()
                },150)
            }

        }
        return bind.root
    }

    private fun openEditor(){
        val fileDescriptor: AssetFileDescriptor? =
            requireContext().contentResolver.openAssetFileDescriptor(viewModel.selectedImage!!.uri, "r")
        val fileSize = fileDescriptor?.length
        fileDescriptor?.close()
        fileSize?.let {
            if(it < 4145728) {
                Navigation.findNavController(requireView()).navigate(CreateCustomDirections.actionCreateCustomToImageEditor())
            }else{
                Toast.makeText(requireContext(),"Image larger then 4 mb not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageInPreview(image : Image){
        bind.apply {
            if(image.mimeType == "asset")  {
                optionImage.setPadding(60)
                optionImage.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        resources.getIdentifier(
                            image.imageUrl,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                )
            } else{
                optionImage.setPadding(0)
                optionImage.setImageBitmap(ImageConvertor.stringToBitmap(image.bitmap))
            }
            optionImage.imageTintList = null
            imageIsSelected.postValue(true)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.croppedImage?.let {
                showImageInPreview(it)
        }
    }

}