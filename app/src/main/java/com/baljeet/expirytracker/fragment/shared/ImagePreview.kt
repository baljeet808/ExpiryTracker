package com.baljeet.expirytracker.fragment.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.databinding.FragmentImagePreviewBinding
import com.baljeet.expirytracker.util.ImageConvertor


class ImagePreview : Fragment() {

    private lateinit var bind: FragmentImagePreviewBinding
    private val navArgs : ImagePreviewArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.apply {
            imageCropView.setImageBitmap(ImageConvertor.stringToBitmap(navArgs.imageToPreview.bitmap))
            closeBtn.setOnClickListener { activity?.onBackPressed() }
        }
    }
}