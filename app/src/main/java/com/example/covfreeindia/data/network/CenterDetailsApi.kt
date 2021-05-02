package com.example.covfreeindia.data.network

import com.example.covfreeindia.models.CenterByPincode
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface CenterDetailsApi {

    @GET("appointment/sessions/public/calendarByPin")
    suspend fun getCentersByPin(
        @QueryMap queries: Map<String, String>
    ): Response<CenterByPincode>
}