package com.texttovoice.virtuai.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeneratedImage(
    @SerializedName("created")
    val created: Int,
    @SerializedName("data")
    val `data`: List<Data>
) : Parcelable

@Parcelize
data class Data(
    @SerializedName("url")
    val url: String
) : Parcelable