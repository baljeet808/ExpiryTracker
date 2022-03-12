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
import com.baljeet.expirytracker.model.RequestPDF
import com.baljeet.expirytracker.util.createPdfReport
import com.baljeet.expirytracker.util.getUriOfPdf


class PdfPreview : Fragment() {

    private lateinit var bind : FragmentPdfPreviewBinding
    private val args : PdfPreviewArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind =  FragmentPdfPreviewBinding.inflate(inflater, container, false)
        bind.apply {


            bind.downloadButton.isEnabled = true

            backButton.setOnClickListener { activity?.onBackPressed() }
            downloadButton.setOnClickListener {
                Toast.makeText(requireContext(),"start downloading", Toast.LENGTH_SHORT).show()
            }
            preparePdf(args.requestData)
        }
        return bind.root
    }

    private fun preparePdf(request : RequestPDF){
        request.createPdfReport(requireContext()).getUriOfPdf(requireContext())?.let { uri->
            bind.pdfView.visibility = View.VISIBLE
            bind.pdfView.fromUri(uri).load()
        }
    }

}