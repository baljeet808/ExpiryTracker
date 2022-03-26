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
import com.baljeet.expirytracker.model.*
import java.io.FileOutputStream
import java.time.LocalDateTime


fun RequestPDF.createPdfReport(context: Context) : PdfDocument{

    val doc = PdfDocument()

    val montserratFont = context.resources.getFont(R.font.montserrat)
    val proximaLight = context.resources.getFont(R.font.proxima_nova_alt_light)
    val montserratSemiBold = context.resources.getFont(R.font.montserrat_semi_bold)

    val cardBGColor = TypedValue()
    context.theme.resolveAttribute(R.attr.cardBackgroundColor, cardBGColor, true)

    val textDialogColor = TypedValue()
    context.theme.resolveAttribute(R.attr.text_dialog_color, textDialogColor, true)

    val colorGreen = context.getColor(R.color.soft_green)
    val colorPeach = context.getColor(R.color.always_peach)
    val colorYellow = context.getColor(R.color.soft_yellow)


    val themeColor = when(this.textColor){
        SelectedTextColor.BLACK->{
            context.getColor(R.color.black)
        }
        SelectedTextColor.BLUE->{
            context.getColor(R.color.theme_blue)
        }
        SelectedTextColor.GREY->{
            context.getColor(R.color.grey)
        }
    }

    val pageHeight = 1700
    val pageWidth = 1250
    var pageNumber = 1
    val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    val page1 = doc.startPage(pageInfo1)
    var canvas = page1.canvas

    val pages = ArrayList<PdfDocument.Page>()
    pages.add(page1)

    val myPaint = Paint()
    val headingLeft = 50F
    if(this.useOfImages == UseImages.ON) {
        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.et_logo_250)
        val scaledBmp = Bitmap.createScaledBitmap(bmp, 90, 90, false)
        canvas.drawBitmap(scaledBmp, headingLeft, 20F, myPaint)
    }

    val titlePaint = Paint()
    titlePaint.textAlign = Paint.Align.LEFT
    titlePaint.typeface = montserratFont
    titlePaint.textSize = 40F
    titlePaint.letterSpacing = 0.03F
    titlePaint.color = themeColor
    canvas.drawText("EXPIRY TRACKER REPORT", if(this.useOfImages == UseImages.ON) 180F else 50F , 60F, titlePaint)


    val sourcePaint = Paint()
    sourcePaint.textAlign = Paint.Align.LEFT
    sourcePaint.typeface = proximaLight
    sourcePaint.textSize = 24F
    sourcePaint.letterSpacing = 0F
    sourcePaint.color = themeColor
    canvas.drawText(
        when(periodType){
            PeriodType.DAILY -> {
                "Daily Report:  ".plus(context.getString(
                    R.string.date_short_var,
                    periodStartDate!!.dayOfMonth,
                    periodStartDate!!.month.name.lowercase()
                ))
            }
            PeriodType.WEEKLY -> {
               "Weekly Report:  ".plus(context.getString(
                    R.string.week_var,
                    periodStartDate!!.dayOfMonth,
                    periodStartDate!!.month.name.lowercase(),
                    periodEndDate!!.dayOfMonth,
                    periodEndDate!!.month.name.lowercase()
                ))
            }
            PeriodType.MONTHLY -> {
                "Monthly Report:  ".plus(context.getString(
                    R.string.month_var,
                    periodStartDate!!.month.name.lowercase(),
                    periodStartDate!!.year
                ))
            }
            PeriodType.YEARLY -> {
                 "Yearly Report:  ".plus(periodStartDate!!.year.toString())
            } },
        if(this.useOfImages == UseImages.ON) 180F else 50F,
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
        1040F,
        100F,
        sourcePaint
    )

    //header line
    val linePaint = Paint()
    linePaint.style = Paint.Style.STROKE
    linePaint.strokeWidth = 2F
    linePaint.color = themeColor
    canvas.drawLine(60F, 130F, 1190F, 130F, linePaint)

    //detail summary
    titlePaint.textSize = 20F
    val resultMsg = when(resultCase){
        ResultCase.GOOD_JOB->{
            context.getString(R.string.result_case_good)
        }
        ResultCase.NEED_TO_IMPROVE->{
            context.getString(R.string.result_case_improve)
        }
        ResultCase.KEEP_IT_UP->{
            context.getString(R.string.result_case_keep_it_up)
        }
        ResultCase.MORE_EXPIRED->{
            context.getString(R.string.result_case_expired)
        }
    }
    var i = 1
    for (text in letsBreakString(resultMsg)) {
        canvas.drawText(text, 65F, 150F + (i * 30F), titlePaint)
        i++
    }
    i--
    val yBondDescription = 150F + (i * 30F)

    sourcePaint.textSize = 18F
    canvas.drawText(context.getString(R.string.total_product_tracked_var,totalTracked),65F,yBondDescription+50F,sourcePaint)
    canvas.drawText(context.getString(R.string.total_product_used__fresh,totalFresh),65F,yBondDescription+80F,sourcePaint)
    canvas.drawText(context.getString(R.string.total_product_expired_var,totalExpired),65F,yBondDescription+110F,sourcePaint)
    canvas.drawText(context.getString(R.string.total_product_used_near_expiry_var,totalNearExpiry),65F,yBondDescription+140F,sourcePaint)

    var yBondTotals = yBondDescription + 240F




    if(this.groupBy == GroupBy.RESULTS) {

        val headingPaint = Paint()
        headingPaint.textAlign = Paint.Align.LEFT
        headingPaint.typeface = montserratSemiBold
        headingPaint.textSize = 24F
        headingPaint.letterSpacing = 0.1F
        headingPaint.color = colorGreen

        i = 0
        sourcePaint.textSize = 22F
        sourcePaint.letterSpacing = 0.05F


        if (totalFresh > 0) {
            canvas.drawText("USED FRESH", 65F, yBondTotals, headingPaint)

            for (tracker in trackers.filter { t -> t.tracker.usedWhileFresh }) {

                yBondTotals += (60F)
                if (yBondTotals + (i * 90F) > pageHeight - 200) {

                    linePaint.style = Paint.Style.STROKE
                    linePaint.strokeWidth = 2F
                    linePaint.color = themeColor
                    canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

                    headingPaint.color = themeColor
                    headingPaint.textSize = 40F
                    headingPaint.typeface = montserratFont
                    canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

                    doc.finishPage(pages[pageNumber - 1])
                    pageNumber++
                    val pageInfo =
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    val page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    pages.add(page)
                    yBondTotals = 90F
                    i = 0
                }
                if (this.useOfImages == UseImages.ON) {
                    val bitmap = when (tracker.productAndCategoryAndImage.image.mimeType) {
                        "asset" -> {
                            BitmapFactory.decodeResource(
                                context.resources,
                                context.resources.getIdentifier(
                                    tracker.productAndCategoryAndImage.image.imageUrl,
                                    "drawable",
                                    context.packageName
                                )
                            )
                        }
                        else -> {
                            ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                        }
                    }

                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, false)
                    canvas.drawBitmap(scaledBitmap, 65F, yBondTotals + (i * 90F) - 20F, myPaint)
                }
                canvas.drawText(
                    context.getString(
                        R.string.product_name_var,
                        tracker.productAndCategoryAndImage.product.name
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F),
                    sourcePaint
                )
                canvas.drawText(
                    context.getString(
                        R.string.category_name_var,
                        tracker.productAndCategoryAndImage.categoryAndImage.category.categoryName
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F) + 30F,
                    sourcePaint
                )
                val expiryDate = tracker.tracker.expiryDate!!
                canvas.drawText(
                    context.getString(
                        R.string.expiry_date_var_1,
                        expiryDate.month.name.substring(0, 3).uppercase(),
                        expiryDate.dayOfMonth,
                        expiryDate.year
                    ), 600F, yBondTotals, sourcePaint
                )
                val mfgDate = tracker.tracker.mfgDate!!
                canvas.drawText(
                    context.getString(
                        R.string.mfg_date_var_1,
                        mfgDate.month.name.substring(0, 3).uppercase(),
                        mfgDate.dayOfMonth,
                        mfgDate.year
                    ), 600F, yBondTotals + (i * 90F) + 30F, sourcePaint
                )
                val usedDate = tracker.tracker.usedDate!!
                canvas.drawText(
                    context.getString(
                        R.string.used_date_var,
                        usedDate.month.name.substring(0, 3).uppercase(),
                        usedDate.dayOfMonth,
                        usedDate.year
                    ), 600F, yBondTotals + (i * 90F) + 60F, sourcePaint
                )

                val statusLine = Paint()
                statusLine.style = Paint.Style.STROKE
                statusLine.strokeWidth = 10F
                statusLine.color = colorGreen
                canvas.drawLine(
                    1000F,
                    yBondTotals + (i * 90F) - 30F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    statusLine
                )

                val itemUnderLine = Paint()
                itemUnderLine.style = Paint.Style.STROKE
                itemUnderLine.strokeWidth = 2F
                itemUnderLine.color = colorGreen
                canvas.drawLine(
                    60F,
                    yBondTotals + (i * 90F) + 70F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    itemUnderLine
                )
                i++
            }
        }
        yBondTotals += (i * 90F)
        i = 0
        yBondTotals += 80F
        if (yBondTotals > pageHeight - 200) {

            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 2F
            linePaint.color = themeColor
            canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

            headingPaint.color = themeColor
            headingPaint.textSize = 40F
            headingPaint.typeface = montserratFont
            canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

            doc.finishPage(pages[pageNumber - 1])
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            canvas = page.canvas
            pages.add(page)
            yBondTotals = 90F
            i = 0
        }


        if (totalNearExpiry + totalOk > 0) {
            headingPaint.color = colorYellow
            headingPaint.textAlign = Paint.Align.LEFT
            headingPaint.typeface = montserratSemiBold
            headingPaint.textSize = 24F
            headingPaint.letterSpacing = 0.1F
            canvas.drawText("USED NEAR EXPIRY", 65F, yBondTotals, headingPaint)

            for (tracker in trackers.filter { t -> t.tracker.usedNearExpiry || t.tracker.usedWhileOk }) {
                yBondTotals += (60F)
                if (yBondTotals + (i * 90F) > pageHeight - 200) {

                    linePaint.style = Paint.Style.STROKE
                    linePaint.strokeWidth = 2F
                    linePaint.color = themeColor
                    canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

                    headingPaint.color = themeColor
                    headingPaint.textSize = 40F
                    headingPaint.typeface = montserratFont
                    canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

                    doc.finishPage(pages[pageNumber - 1])
                    pageNumber++
                    val pageInfo =
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    val page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    pages.add(page)
                    yBondTotals = 90F
                    i = 0
                }
                if (this.useOfImages == UseImages.ON) {

                    val bitmap = when (tracker.productAndCategoryAndImage.image.mimeType) {
                        "asset" -> {
                            BitmapFactory.decodeResource(
                                context.resources,
                                context.resources.getIdentifier(
                                    tracker.productAndCategoryAndImage.image.imageUrl,
                                    "drawable",
                                    context.packageName
                                )
                            )
                        }
                        else -> {
                            ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                        }
                    }

                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, false)
                    canvas.drawBitmap(scaledBitmap, 65F, yBondTotals + (i * 90F) - 20F, myPaint)
                }
                canvas.drawText(
                    context.getString(
                        R.string.product_name_var,
                        tracker.productAndCategoryAndImage.product.name
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F),
                    sourcePaint
                )
                canvas.drawText(
                    context.getString(
                        R.string.category_name_var,
                        tracker.productAndCategoryAndImage.categoryAndImage.category.categoryName
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F) + 30F,
                    sourcePaint
                )
                val expiryDate = tracker.tracker.expiryDate!!
                canvas.drawText(
                    context.getString(
                        R.string.expiry_date_var_1,
                        expiryDate.month.name.substring(0, 3).uppercase(),
                        expiryDate.dayOfMonth,
                        expiryDate.year
                    ), 600F, yBondTotals + (i * 90F), sourcePaint
                )
                val mfgDate = tracker.tracker.mfgDate!!
                canvas.drawText(
                    context.getString(
                        R.string.mfg_date_var_1,
                        mfgDate.month.name.substring(0, 3).uppercase(),
                        mfgDate.dayOfMonth,
                        mfgDate.year
                    ), 600F, yBondTotals + (i * 90F) + 30F, sourcePaint
                )
                val usedDate = tracker.tracker.usedDate!!
                canvas.drawText(
                    context.getString(
                        R.string.used_date_var,
                        usedDate.month.name.substring(0, 3).uppercase(),
                        usedDate.dayOfMonth,
                        usedDate.year
                    ), 600F, yBondTotals + (i * 90F) + 60F, sourcePaint
                )


                val statusLine = Paint()
                statusLine.style = Paint.Style.STROKE
                statusLine.strokeWidth = 10F
                statusLine.color = colorYellow
                canvas.drawLine(
                    1000F,
                    yBondTotals + (i * 90F) - 30F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    statusLine
                )

                val itemUnderLine = Paint()
                itemUnderLine.style = Paint.Style.STROKE
                itemUnderLine.strokeWidth = 2F
                itemUnderLine.color = colorYellow
                canvas.drawLine(
                    60F,
                    yBondTotals + (i * 90F) + 70F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    itemUnderLine
                )
                i++
            }
        }
        yBondTotals += (i * 90F)
        i = 0
        yBondTotals += 80F
        if (yBondTotals > pageHeight - 200) {

            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 2F
            linePaint.color = themeColor
            canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

            headingPaint.color = themeColor
            headingPaint.textSize = 40F
            headingPaint.typeface = montserratFont
            canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

            doc.finishPage(pages[pageNumber - 1])
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            canvas = page.canvas
            pages.add(page)
            yBondTotals = 90F
            i = 0
        }



        if (totalExpired > 0) {
            headingPaint.color = colorPeach
            headingPaint.textAlign = Paint.Align.LEFT
            headingPaint.typeface = montserratSemiBold
            headingPaint.textSize = 24F
            headingPaint.letterSpacing = 0.1F
            canvas.drawText("EXPIRED", 65F, yBondTotals, headingPaint)
            for (tracker in trackers.filter { t -> t.tracker.gotExpired }) {

                yBondTotals += (60F)

                if (yBondTotals + (i * 90F) > pageHeight - 200) {

                    linePaint.style = Paint.Style.STROKE
                    linePaint.strokeWidth = 2F
                    linePaint.color = themeColor
                    canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

                    headingPaint.color = themeColor
                    headingPaint.textSize = 40F
                    headingPaint.typeface = montserratFont
                    canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

                    doc.finishPage(pages[pageNumber - 1])
                    pageNumber++
                    val pageInfo =
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    val page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    pages.add(page)
                    yBondTotals = 90F
                    i = 0
                }
                if (this.useOfImages == UseImages.ON) {
                    val bitmap = when (tracker.productAndCategoryAndImage.image.mimeType) {
                        "asset" -> {
                            BitmapFactory.decodeResource(
                                context.resources,
                                context.resources.getIdentifier(
                                    tracker.productAndCategoryAndImage.image.imageUrl,
                                    "drawable",
                                    context.packageName
                                )
                            )
                        }
                        else -> {
                            ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                        }
                    }

                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, false)
                    canvas.drawBitmap(scaledBitmap, 65F, yBondTotals + (i * 90F) - 20F, myPaint)
                }
                canvas.drawText(
                    context.getString(
                        R.string.product_name_var,
                        tracker.productAndCategoryAndImage.product.name
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F),
                    sourcePaint
                )
                canvas.drawText(
                    context.getString(
                        R.string.category_name_var,
                        tracker.productAndCategoryAndImage.categoryAndImage.category.categoryName
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F) + 30F,
                    sourcePaint
                )
                val expiryDate = tracker.tracker.expiryDate!!
                canvas.drawText(
                    context.getString(
                        R.string.expiry_date_var_1,
                        expiryDate.month.name.substring(0, 3).uppercase(),
                        expiryDate.dayOfMonth,
                        expiryDate.year
                    ), 600F, yBondTotals + (i * 90F), sourcePaint
                )
                val mfgDate = tracker.tracker.mfgDate!!
                canvas.drawText(
                    context.getString(
                        R.string.mfg_date_var_1,
                        mfgDate.month.name.substring(0, 3).uppercase(),
                        mfgDate.dayOfMonth,
                        mfgDate.year
                    ), 600F, yBondTotals + (i * 90F) + 30F, sourcePaint
                )
                val usedDate = tracker.tracker.usedDate!!
                canvas.drawText(
                    context.getString(
                        R.string.used_date_var,
                        usedDate.month.name.substring(0, 3).uppercase(),
                        usedDate.dayOfMonth,
                        usedDate.year
                    ), 600F, yBondTotals + (i * 90F) + 60F, sourcePaint
                )


                val statusLine = Paint()
                statusLine.style = Paint.Style.STROKE
                statusLine.strokeWidth = 10F
                statusLine.color = colorPeach
                canvas.drawLine(
                    1000F,
                    yBondTotals + (i * 90F) - 30F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    statusLine
                )

                val itemUnderLine = Paint()
                itemUnderLine.style = Paint.Style.STROKE
                itemUnderLine.strokeWidth = 2F
                itemUnderLine.color = colorPeach
                canvas.drawLine(
                    60F,
                    yBondTotals + (i * 90F) + 70F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    itemUnderLine
                )
                i++
            }
        }
        yBondTotals += (i * 90F)
        i = 0
        yBondTotals += 80F
        if (yBondTotals + (i * 90F) > pageHeight - 200) {

            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 2F
            linePaint.color = themeColor
            canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

            headingPaint.color = themeColor
            headingPaint.textSize = 40F
            headingPaint.typeface = montserratFont
            canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

            doc.finishPage(pages[pageNumber - 1])
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            page.canvas
            pages.add(page)

        } else {
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 2F
            linePaint.color = themeColor
            canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

            headingPaint.color = themeColor
            headingPaint.textSize = 40F
            headingPaint.typeface = montserratFont
            canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)
        }

    }
    else{
        val headingPaint = Paint()
        headingPaint.textAlign = Paint.Align.LEFT
        headingPaint.typeface = montserratSemiBold
        headingPaint.textSize = 24F
        headingPaint.letterSpacing = 0.1F
        headingPaint.color = themeColor

        i = 0
        sourcePaint.textSize = 22F
        sourcePaint.letterSpacing = 0.05F

        
        for(categorisedTrackers in this.trackers.getGroupedListByCategories()){
            canvas.drawText(categorisedTrackers.categoryName, 65F, yBondTotals+  (i * 90F) , headingPaint)

            for (tracker in trackers.filter { t -> t.tracker.usedWhileFresh }) {

                yBondTotals += (60F)
                if (yBondTotals + (i * 90F) > pageHeight - 200) {

                    linePaint.style = Paint.Style.STROKE
                    linePaint.strokeWidth = 2F
                    linePaint.color = themeColor
                    canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

                    headingPaint.color = themeColor
                    headingPaint.textSize = 40F
                    headingPaint.typeface = montserratFont
                    canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

                    doc.finishPage(pages[pageNumber - 1])
                    pageNumber++
                    val pageInfo =
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    val page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    pages.add(page)
                    yBondTotals = 90F
                    i = 0
                }
                if (this.useOfImages == UseImages.ON) {
                    val bitmap = when (tracker.productAndCategoryAndImage.image.mimeType) {
                        "asset" -> {
                            BitmapFactory.decodeResource(
                                context.resources,
                                context.resources.getIdentifier(
                                    tracker.productAndCategoryAndImage.image.imageUrl,
                                    "drawable",
                                    context.packageName
                                )
                            )
                        }
                        else -> {
                            ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                        }
                    }

                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, false)
                    canvas.drawBitmap(scaledBitmap, 65F, yBondTotals + (i * 90F) - 20F, myPaint)
                }
                canvas.drawText(
                    context.getString(
                        R.string.product_name_var,
                        tracker.productAndCategoryAndImage.product.name
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F),
                    sourcePaint
                )
                canvas.drawText(
                    context.getString(
                        R.string.category_name_var,
                        tracker.productAndCategoryAndImage.categoryAndImage.category.categoryName
                    ),
                    if (this.useOfImages == UseImages.ON) 180F else 65F,
                    yBondTotals + (i * 90F) + 30F,
                    sourcePaint
                )
                val expiryDate = tracker.tracker.expiryDate!!
                canvas.drawText(
                    context.getString(
                        R.string.expiry_date_var_1,
                        expiryDate.month.name.substring(0, 3).uppercase(),
                        expiryDate.dayOfMonth,
                        expiryDate.year
                    ), 600F, yBondTotals, sourcePaint
                )
                val mfgDate = tracker.tracker.mfgDate!!
                canvas.drawText(
                    context.getString(
                        R.string.mfg_date_var_1,
                        mfgDate.month.name.substring(0, 3).uppercase(),
                        mfgDate.dayOfMonth,
                        mfgDate.year
                    ), 600F, yBondTotals + (i * 90F) + 30F, sourcePaint
                )
                val usedDate = tracker.tracker.usedDate!!
                canvas.drawText(
                    context.getString(
                        R.string.used_date_var,
                        usedDate.month.name.substring(0, 3).uppercase(),
                        usedDate.dayOfMonth,
                        usedDate.year
                    ), 600F, yBondTotals + (i * 90F) + 60F, sourcePaint
                )

                val statusLine = Paint()
                statusLine.style = Paint.Style.STROKE
                statusLine.strokeWidth = 10F
                statusLine.color = if(tracker.tracker.gotExpired || tracker.tracker.usedNearExpiry){
                    colorPeach
                }else if(tracker.tracker.usedWhileOk){
                    colorYellow
                }else{
                    colorGreen
                }
                canvas.drawLine(
                    1000F,
                    yBondTotals + (i * 90F) - 30F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    statusLine
                )

                val itemUnderLine = Paint()
                itemUnderLine.style = Paint.Style.STROKE
                itemUnderLine.strokeWidth = 2F
                itemUnderLine.color = if(tracker.tracker.gotExpired || tracker.tracker.usedNearExpiry){
                    colorPeach
                }else if(tracker.tracker.usedWhileOk){
                    colorYellow
                }else{
                    colorGreen
                }
                canvas.drawLine(
                    60F,
                    yBondTotals + (i * 90F) + 70F,
                    1000F,
                    yBondTotals + (i * 90F) + 70F,
                    itemUnderLine
                )
                i++
            }
        }

        yBondTotals += (i * 90F)
        i = 0
        yBondTotals += 80F
        if (yBondTotals + (i * 90F) > pageHeight - 200) {

            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 2F
            linePaint.color = themeColor
            canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

            headingPaint.color = themeColor
            headingPaint.textSize = 40F
            headingPaint.typeface = montserratFont
            canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)

            doc.finishPage(pages[pageNumber - 1])
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            page.canvas
            pages.add(page)

        } else {
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 2F
            linePaint.color = themeColor
            canvas.drawLine(60F, pageHeight - 100F, 1190F, pageHeight - 100F, linePaint)

            headingPaint.color = themeColor
            headingPaint.textSize = 40F
            headingPaint.typeface = montserratFont
            canvas.drawText(pageNumber.toString(), 1150F, pageHeight - 50F, headingPaint)
        }

    }

    doc.finishPage(pages[pageNumber-1])
    return doc
}



private fun letsBreakString(text: String): ArrayList<String> {
    val brokenResult = ArrayList<String>()
    val textLength = text.length

    if (textLength > 90) {
        val possibleBreak = textLength.toFloat() / 90.toFloat()
        var actualBreak: Int
        if (!possibleBreak.rem(1).equals(0F)) {
            actualBreak = textLength / 90
            actualBreak++
        } else {
            actualBreak = possibleBreak.toInt()
        }

        var lastIndex = 90 - 1
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
            lastIndex += 90
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