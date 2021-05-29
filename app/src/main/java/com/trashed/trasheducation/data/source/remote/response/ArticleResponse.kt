package com.trashed.trasheducation.data.source.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleResponse(
    var explanation: String,
    var impact: String,
    var overcome: String,
    var link1: String,
    var link2: String,
    var video: String
): Parcelable