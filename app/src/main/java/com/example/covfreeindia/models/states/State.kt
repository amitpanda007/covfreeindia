package com.example.covfreeindia.models.states


import com.google.gson.annotations.SerializedName

data class State(
    @SerializedName("state_id")
    val stateId: Int,
    @SerializedName("state_name")
    val stateName: String
)