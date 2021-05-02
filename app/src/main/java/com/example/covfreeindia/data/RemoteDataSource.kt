package com.example.covfreeindia.data

import com.example.covfreeindia.data.network.CenterDetailsApi
import com.example.covfreeindia.models.CenterByPincode
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val centerDetailsApi: CenterDetailsApi
){

    suspend fun getCentersByPinCode(queries: Map<String, String>): Response<CenterByPincode> {
        return centerDetailsApi.getCentersByPin(queries)
    }
}