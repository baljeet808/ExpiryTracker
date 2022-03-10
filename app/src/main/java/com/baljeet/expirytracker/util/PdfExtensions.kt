package com.baljeet.expirytracker.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.MediaStore
import android.util.TypedValue
import android.widget.Toast
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


fun ArrayList<TrackerAndProduct>.createPdfReport(context: Context) : PdfDocument{

    val doc = PdfDocument()

    val montserratFont = context.resources.getFont(R.font.montserrat)

    val cardBGColor = TypedValue()
    context.theme.resolveAttribute(R.attr.cardBackgroundColor, cardBGColor, true)

    val textDialogColor = TypedValue()
    context.theme.resolveAttribute(R.attr.text_dialog_color, textDialogColor, true)

    val themeColor = TypedValue()
    context.theme.resolveAttribute(R.attr.colorPrimary, themeColor, true)

    val pageHeight = 1700
    val pageWidth = 1400
    var pageNumber = 1
    val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    val page1 = doc.startPage(pageInfo1)
    var canvas = page1.canvas

    val pages = ArrayList<PdfDocument.Page>()
    pages.add(page1)

    val myPaint = Paint()
    val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.et_logo_250)
    val scaledBmp = Bitmap.createScaledBitmap(bmp, 100, 100, false)
    canvas.drawBitmap(scaledBmp, 950F, 100F, myPaint)

    doc.finishPage(page1)
    return doc
}


fun PdfDocument.getUriOfPdf(context : Context): Uri? {
    val resolver = context.contentResolver

    val docCollection =
        MediaStore.Downloads.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )

    val pdfDetails = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, "Expiry_tracker_analysis_report_${LocalDateTime.now()}.pdf")
        put(MediaStore.Downloads.IS_PENDING, 1)
    }

    val pdfContentUri = resolver.insert(docCollection, pdfDetails)
      try {
          resolver.openFileDescriptor(pdfContentUri!!, "w", null).use { pfd ->
              try {
                  this.writeTo(FileOutputStream(pfd!!.fileDescriptor))
              } catch (e: Exception) {
                  e.printStackTrace()
              }
          }

          pdfDetails.clear()
          pdfDetails.put(MediaStore.Downloads.IS_PENDING, 0)
          resolver.update(pdfContentUri, pdfDetails, null, null)
      }catch (e : Exception){
          Toast.makeText(context,"something went wrong, while creating pdf", Toast.LENGTH_SHORT).show()
      }
    this.close()
    return pdfContentUri
}