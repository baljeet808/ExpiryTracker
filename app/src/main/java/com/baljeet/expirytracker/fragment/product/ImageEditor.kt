package com.baljeet.expirytracker.fragment.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentImageEditorBinding
import com.baljeet.expirytracker.util.ImageConvertor

class ImageEditor : Fragment() {
    private lateinit var bind : FragmentImageEditorBinding

    private val viewModel : CustomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentImageEditorBinding.inflate(inflater, container, false)

        bind.apply {
            imageCropView.setImageBitmap(ImageConvertor.uriToBitmap(viewModel.selectedImage?.uri!!, requireContext()))
            closeBtn.setOnClickListener { activity?.onBackPressed() }

            saveButton.setOnClickListener {
                viewModel.croppedImage = viewModel.selectedImage
                viewModel.croppedImage?.bitmap = ImageConvertor.bitMapToString(imageCropView.croppedImage,viewModel.selectedImage!!.mimeType).toString()
                activity?.onBackPressed()
            }
        }

        return bind.root
    }
}