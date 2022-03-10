package com.baljeet.expirytracker.fragment.shared

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.databinding.FragmentPdfPreviewBinding


class PdfPreview : Fragment() {

    private lateinit var bind : FragmentPdfPreviewBinding
    private val args : PdfPreviewArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind =  FragmentPdfPreviewBinding.inflate(inflater, container, false)
        bind.apply {
            bind.pdfView.fromUri(args.pdfUri.uri).load()
            bind.pdfView.visibility = View.VISIBLE
            bind.downloadButton.isEnabled = true

            backButton.setOnClickListener { activity?.onBackPressed() }
            downloadButton.setOnClickListener {
                Toast.makeText(requireContext(),"start downloading", Toast.LENGTH_SHORT).show()
            }
        }
        return bind.root
    }

}