package com.example.cargo.data


import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("farm") val farm: Int,
    @SerializedName("height_s") val heightS: Int,
    @SerializedName("id") val id: String,
    @SerializedName("isfamily") val isfamily: Int,
    @SerializedName("isfriend") val isfriend: Int,
    @SerializedName("ispublic") val ispublic: Int,
    @SerializedName("owner") val owner: String,
    @SerializedName("secret") val secret: String,
    @SerializedName("server") val server: String,
    @SerializedName("title") val title: String,
    @SerializedName("url_s") val urlS: String,
    @SerializedName("width_s") val widthS: Int
)