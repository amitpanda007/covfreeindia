package com.example.covfreeindia.models.states


import com.google.gson.annotations.SerializedName

data class States(
    @SerializedName("states")
    val states: List<State>,
    @SerializedName("ttl")
    val ttl: Int
)