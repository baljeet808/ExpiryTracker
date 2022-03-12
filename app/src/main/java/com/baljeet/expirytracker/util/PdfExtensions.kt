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
import com.baljeet.expirytracker.model.RequestPDF
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


fun RequestPDF.createPdfReport(context: Context) : PdfDocument{

    val doc = PdfDocument()

    val montserratFont = context.resources.getFont(R.font.montserrat)
    val proximaLight = context.resources.getFont(R.font.proxima_nova_alt_light)

    val cardBGColor = TypedValue()
    context.theme.resolveAttribute(R.attr.cardBackgroundColor, cardBGColor, true)

    val textDialogColor = TypedValue()
    context.theme.resolveAttribute(R.attr.text_dialog_color, textDialogColor, true)

    val textColor = context.getColor(R.color.text_color)
    val mainBackground = context.getColor(R.color.main_background)

    val themeColor = context.getColor(R.color.theme_blue)

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
    val scaledBmp = Bitmap.createScaledBitmap(bmp, 90, 90, false)
    canvas.drawBitmap(scaledBmp, 50F, 20F, myPaint)

    val titlePaint = Paint()
    titlePaint.textAlign = Paint.Align.LEFT
    titlePaint.typeface = montserratFont
    titlePaint.textSize = 40F
    titlePaint.letterSpacing = 0.03F
    titlePaint.color = themeColor
    canvas.drawText("EXPIRY TRACKER REPORT", 180F, 60F, titlePaint)


    val sourcePaint = Paint()
    sourcePaint.textAlign = Paint.Align.LEFT
    sourcePaint.typeface = proximaLight
    sourcePaint.textSize = 24F
    sourcePaint.letterSpacing = 0F
    sourcePaint.color = mainBackground
    canvas.drawText(
        "Monthly Report",
        180F,
        100F,
        sourcePaint
    )

    //current date
    val dateToday = LocalDateTime.now()
    canvas.drawText(
        context.getString(
            R.string.date_string_with_month_name,
            dateToday.month.name.substring(0,3).uppercase(),
            dateToday.dayOfMonth,
            dateToday.year
        ),
        1190F,
        100F,
        sourcePaint
    )

    //header line
    val linePaint = Paint()
    linePaint.style = Paint.Style.STROKE
    linePaint.strokeWidth = 2F
    linePaint.color = themeColor
    canvas.drawLine(60F, 130F, 1340F, 130F, linePaint)

    //detail summary

    



    doc.finishPage(pages[0])
    return doc
}



private fun letsBreakString(text: String, maxLength: Int): ArrayList<String> {
    val brokenResult = ArrayList<String>()
    val textLength = text.length

    if (textLength > maxLength) {
        val possibleBreak = textLength.toFloat() / maxLength.toFloat()
        var actualBreak: Int
        if (!possibleBreak.rem(1).equals(0F)) {
            actualBreak = textLength / maxLength
            actualBreak++
        } else {
            actualBreak = possibleBreak.toInt()
        }

        var lastIndex = maxLength - 1
        var firstIndex = 0

        for (i in 1..actualBreak) {
            if (lastIndex >= textLength) {
                brokenResult.add(text.substring(firstIndex, textLength - 1))
            } else {
                if (text[lastIndex].isWhitespace()) {
                    brokenResult.add(text.substring(firstIndex, lastIndex))
                } else {
                    if (lastIndex + 1 == textLength) {
                        brokenResult.add(text.substring(firstIndex, lastIndex))
                    } else {
                        if (text[lastIndex + 1].isWhitespace()) {
                            brokenResult.add(text.substring(firstIndex, lastIndex))
                        } else {
                            while (!text[lastIndex].isWhitespace()) {
                                lastIndex--
                            }
                            brokenResult.add(text.substring(firstIndex, lastIndex))
                        }
                    }
                }
            }
            firstIndex = lastIndex + 1
            lastIndex += maxLength
        }
    } else {
        brokenResult.add(text)
    }
    return brokenResult
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