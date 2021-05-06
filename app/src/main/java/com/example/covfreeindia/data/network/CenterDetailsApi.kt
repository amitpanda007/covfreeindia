package com.example.covfreeindia.data.network

import com.example.covfreeindia.models.centers.CenterByPincode
import com.example.covfreeindia.models.districts.District
import com.example.covfreeindia.models.districts.Districts
import com.example.covfreeindia.models.states.States
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface CenterDetailsApi {

    // COWIN API DIRECT CALL
    @GET("appointment/sessions/public/calendarByPin")
    suspend fun getCentersByPin(
        @QueryMap queries: Map<String, String>
    ): Response<CenterByPincode>

    @GET("appointment/sessions/public/calendarByDistrict")
    suspend fun getDistrictCenters(
        @QueryMap queries: Map<String, String>
    ): Response<CenterByPincode>

    @GET("admin/location/states")
    suspend fun getStates(): Response<States>

    @GET("admin/location/districts/{stateId}")
    suspend fun getStateDistrict(@Path("stateId") stateId: String): Response<Districts>

    // PERSONAL API CALLS
    @GET("center/avlByPin")
    suspend fun getCentersByPinNodeApi(
        @QueryMap queries: Map<String, String>
    ): Response<CenterByPincode>

    @GET("center/avlByDist")
    suspend fun getDistrictCentersNodeApi(
        @QueryMap queries: Map<String, String>
    ): Response<CenterByPincode>

    @GET("location/states")
    suspend fun getStatesNodeApi(): Response<States>

    @GET("location/districts/{stateId}")
    suspend fun getStateDistrictNodeApi(@Path("stateId") stateId: String): Response<Districts>

}