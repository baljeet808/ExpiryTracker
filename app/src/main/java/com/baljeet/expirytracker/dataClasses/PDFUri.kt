package com.baljeet.expirytracker.dataClasses

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PDFUri(
    var uri : Uri
): Parcelable
