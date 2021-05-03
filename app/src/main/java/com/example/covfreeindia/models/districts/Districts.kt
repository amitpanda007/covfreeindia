package com.example.covfreeindia.models.districts


import com.google.gson.annotations.SerializedName

data class Districts(
    @SerializedName("districts")
    val districts: List<District>,
    @SerializedName("ttl")
    val ttl: Int
)