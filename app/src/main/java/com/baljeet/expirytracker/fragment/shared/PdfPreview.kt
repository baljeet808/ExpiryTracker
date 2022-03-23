package com.baljeet.expirytracker.fragment.shared

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.baljeet.expirytracker.databinding.FragmentPdfPreviewBinding
import com.baljeet.expirytracker.model.*
import com.baljeet.expirytracker.util.SharedPref
import com.baljeet.expirytracker.util.createPdfReport
import com.baljeet.expirytracker.util.getUriOfPdf


class PdfPreview : Fragment() {

    private lateinit var bind: FragmentPdfPreviewBinding
    private val args: PdfPreviewArgs by navArgs()

    private var requestData: RequestPDF? = null
    private var pdfRequestUpdated = MutableLiveData(false)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentPdfPreviewBinding.inflate(inflater, container, false)
        bind.apply {

            bind.shareButton.isEnabled = true

            backButton.setOnClickListener { activity?.onBackPressed() }
            SharedPref.init(requireContext())
            SharedPref.isUserAPro.apply {
                pdfToolsCard.isEnabled = this
                proIcon.isGone = this
            }
            requestData = args.requestData

            proIcon.setOnClickListener {
                Navigation.findNavController(requireView())
                    .navigate(PdfPreviewDirections.actionPdfPreviewToBePro())
            }
            imageToggleChip.setOnClickListener {
                requestData?.useOfImages =
                    if (imageToggleChip.isChecked) UseImages.OFF else UseImages.ON
                pdfRequestUpdated.postValue(true)
            }
            textColorGroup.setOnCheckedChangeListener { _, btnId ->
                when (btnId) {
                    radioButtonBlue.id -> {
                        requestData?.textColor = SelectedTextColor.BLUE
                    }
                    radioButtonGrey.id -> {
                        requestData?.textColor = SelectedTextColor.GREY
                    }
                    radioButtonBlack.id -> {
                        requestData?.textColor = SelectedTextColor.BLACK
                    }
                }
                pdfRequestUpdated.postValue(true)
            }

            bgColorGroup.setOnCheckedChangeListener { _, btnId ->
                when (btnId) {
                    radioButtonBlackBg.id -> {
                        requestData?.backgroundColor = BackgroundColor.BLACK
                    }
                    radioButtonGreyBg.id -> {
                        requestData?.backgroundColor = BackgroundColor.GREY
                    }
                    radioButtonWhiteBg.id -> {
                        requestData?.backgroundColor = BackgroundColor.WHITE
                    }
                }
                pdfRequestUpdated.postValue(true)
            }
            
            groupByGroup.setOnCheckedChangeListener { _, btnId ->
                when (btnId) {
                    radioButtonCategories.id -> {
                        requestData?.groupBy = GroupBy.CATEGORIES
                    }
                    radioButtonTracking.id -> {
                        requestData?.groupBy = GroupBy.RESULTS
                    }
                }
                pdfRequestUpdated.postValue(true)
            }

            pdfRequestUpdated.observe(viewLifecycleOwner) {
                requestData?.let {
                    preparePdf(it)
                }
            }
        }
        return bind.root
    }

    private fun preparePdf(request: RequestPDF) {
        request.createPdfReport(requireContext()).getUriOfPdf(requireContext())?.let { uri ->
            bind.pdfView.visibility = View.VISIBLE
            bind.pdfView.fromUri(uri).load()
            bind.shareButton.setOnClickListener {
                share(uri)
            }
        }
    }

    private fun share(uri: Uri) {
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