package com.example.covfreeindia.models.districts


import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("district_id")
    val districtId: Int,
    @SerializedName("district_name")
    val districtName: String
)