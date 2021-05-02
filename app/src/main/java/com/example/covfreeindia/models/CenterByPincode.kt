package com.example.covfreeindia.models


import com.google.gson.annotations.SerializedName

data class CenterByPincode(
    @SerializedName("centers")
    val centers: List<Center>
)