package com.baljeet.expirytracker.fragment.shared

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
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


            bind.shareButton.isEnabled = true

            backButton.setOnClickListener { activity?.onBackPressed() }

            preparePdf(args.requestData)
        }
        return bind.root
    }

    private fun preparePdf(request : RequestPDF){
        request.createPdfReport(requireContext()).getUriOfPdf(requireContext())?.let { uri->
            bind.pdfView.visibility = View.VISIBLE
            bind.pdfView.fromUri(uri).load()
            bind.shareButton.setOnClickListener {
                share(uri)
            }
        }
    }

    private fun share(uri : Uri){
        val shareIntent = Intent().apply {
            this.action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_STREAM, uri)
            this.type = "application/pdf"
            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }

}