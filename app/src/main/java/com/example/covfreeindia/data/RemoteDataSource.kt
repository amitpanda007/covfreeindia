package com.example.covfreeindia.data

import com.example.covfreeindia.data.network.CenterDetailsApi
import com.example.covfreeindia.models.centers.CenterByPincode
import com.example.covfreeindia.models.districts.Districts
import com.example.covfreeindia.models.states.States
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val centerDetailsApi: CenterDetailsApi
){

    suspend fun getCentersByPinCode(queries: Map<String, String>): Response<CenterByPincode> {
        return centerDetailsApi.getCentersByPin(queries)
    }

    suspend fun getDistrictCentersByDistrictId(queries: Map<String, String>): Response<CenterByPincode> {
        return centerDetailsApi.getDistrictCenters(queries)
    }

    suspend fun getStates(): Response<States> {
        return centerDetailsApi.getStates()
    }

    suspend fun getDistricts(distId: String): Response<Districts> {
        return centerDetailsApi.getStateDistrict(distId)
    }
}