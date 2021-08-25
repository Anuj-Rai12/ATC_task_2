package com.example.cargo.data


import com.google.gson.annotations.SerializedName

data class GalleryData(
    @SerializedName("photos") val photos: Photos,
    @SerializedName("stat") val stat: String
)