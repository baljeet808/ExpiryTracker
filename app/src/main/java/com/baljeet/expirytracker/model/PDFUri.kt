package com.baljeet.expirytracker.model

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class PDFUri(
    var uri : Uri
): Parcelable
