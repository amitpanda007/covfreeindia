package com.example.covfreeindia.models.centers


import com.example.covfreeindia.models.centers.Center
import com.google.gson.annotations.SerializedName

data class CenterByPincode(
    @SerializedName("centers")
    val centers: List<Center>
)