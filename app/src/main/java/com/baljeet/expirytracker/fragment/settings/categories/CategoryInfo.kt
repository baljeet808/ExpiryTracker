package com.baljeet.expirytracker.fragment.settings.categories


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.DeletePopUpLayoutBinding
import com.baljeet.expirytracker.databinding.FragmentCategoryInfoBinding
import com.baljeet.expirytracker.databinding.ImagePickingOptionsLayoutBinding
import com.baljeet.expirytracker.fragment.product.CustomViewModel
import com.baljeet.expirytracker.fragment.shared.IconsViewModel
import com.baljeet.expirytracker.util.ImageConvertor
import com.baljeet.expirytracker.util.SharedPref
import com.baljeet.expirytracker.util.getContentType
import com.baljeet.expirytracker.util.getFileName
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

class CategoryInfo : Fragment() {

    private lateinit var bind: FragmentCategoryInfoBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val navArgs: CategoryInfoArgs by navArgs()
    private val imageViewModel : ImageViewModel by viewModels()
    private val customViewModel : CustomViewModel by activityViewModels()

    private val iconViewModel : IconsViewModel by activityViewModels()

    private lateinit var deleteDialog: AlertDialog
    private lateinit var dialogBuilder: AlertDialog.Builder

    private var nameChanged = MutableLiveData(false)

    private var changesAreValid = MediatorLiveData<Boolean>().apply {
        addSource(nameChanged) {
            this.value = validateName(bind.nameEdittext.text.toString())
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
                        clearImageCache()
                        isEnabled = false
                        activity?.onBackPressed()

                    }
                })
        SharedPref.init(requireContext())
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentCategoryInfoBinding.inflate(inflater, container, false)
        bind.apply {
            bind.closeBtn.setOnClickListener {
                activity?.onBackPressed()
            }
            val category = navArgs.categoryAndImage

            iconViewModel.selectedIcon?.let {
                imageView.setPadding(60)
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        resources.getIdentifier(
                            it.imageUrl,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                )
            }?: kotlin.run {
                customViewModel.croppedImage?.let {
                    when (it.mimeType) {
                        "asset" -> {
                            imageView.setPadding(60)
                            imageView.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    requireContext(),
                                    resources.getIdentifier(
                                        it.imageUrl,
                                        "drawable",
                                        requireContext().packageName
                                    )
                                )
                            )
                        }
                        else -> {
                            imageView.setPadding(0)
                            imageView.setImageBitmap(ImageConvertor.stringToBitmap(it.bitmap))
                        }
                    }
                }?: kotlin.run {
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

                val imageId = iconViewModel.selectedIcon?.imageId ?: kotlin.run {
                    customViewModel.croppedImage?.let {
                        imageViewModel.addImage(it)
                        imageViewModel.getImageByName(it.imageName).imageId
                    }  ?: kotlin.run {
                        category.image.imageId
                    }
                }
                viewModel.updateCategory(
                    Category(
                        categoryId = category.category.categoryId,
                        categoryName = nameEdittext.text.toString(),
                        imageId = imageId,
                        isDeleted = false
                    )
                )
                activity?.onBackPressed()
            }
            deleteButton.setOnClickListener {
                showPopup()
            }

            editImageBtn.setOnClickListener {
                 showBottomSheet()
            }

        }
        return bind.root
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        val pickerBind = ImagePickingOptionsLayoutBinding.inflate(layoutInflater)
        pickerBind.uploadBtn.setOnClickListener {
            pickAction.launch("image/*")
            clearImageCache()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnDismissListener {
              bind.editImageBtn.isEnabled = true
        }

        pickerBind.iconsBtn.setOnClickListener {
            customViewModel.croppedImage
            clearImageCache()
            bottomSheetDialog.dismiss()
            Navigation.findNavController(requireView()).navigate(CategoryInfoDirections.actionCategoryInfoToIconsGallery())
        }
        pickerBind.cameraBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
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
            clearImageCache()
            takeImageResult.launch(latestTmpUri)
        }
        bottomSheetDialog.setContentView(pickerBind.root)
        bottomSheetDialog.show()
    }

    fun clearImageCache(){
        iconViewModel.selectedIcon = null
        customViewModel.croppedImage = null
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

    private fun openEditor(image : Image){
        Navigation.findNavController(requireView()).navigate(CategoryInfoDirections.actionCategoryInfoToImageEditor(image))
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