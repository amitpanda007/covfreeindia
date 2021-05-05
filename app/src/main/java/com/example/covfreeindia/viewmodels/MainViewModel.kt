package com.example.covfreeindia.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.covfreeindia.data.Repository
import com.example.covfreeindia.models.centers.CenterByPincode
import com.example.covfreeindia.models.districts.District
import com.example.covfreeindia.models.districts.Districts
import com.example.covfreeindia.models.states.States
import com.example.covfreeindia.util.Constants.Companion.LOG_TAG
import com.example.covfreeindia.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    var centerResponse: MutableLiveData<NetworkResult<CenterByPincode>> = MutableLiveData()
    var futureCenterResponse: MutableLiveData<NetworkResult<CenterByPincode>> = MutableLiveData()
    var districtCenterResponse: MutableLiveData<NetworkResult<CenterByPincode>> = MutableLiveData()
    var statesResponse: MutableLiveData<NetworkResult<States>> = MutableLiveData()
    var districtsResponse: MutableLiveData<NetworkResult<Districts>> = MutableLiveData()

    private var hasDistrictData: Boolean = false
    private var hasCenterData: Boolean = false

    fun getStates() = viewModelScope.launch {
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.getStates()
                statesResponse.value = handleStateResponse(response)
            } catch (e: Exception) {

            }
        } else {
            statesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    fun getDistricts(distId: String) = viewModelScope.launch {
        if(hasDistrictData) {
            districtsResponse.value = null
        }
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.getDistricts(distId)
                districtsResponse.value = handleDistrictResponse(response)
                hasDistrictData = true
            } catch (e: Exception) {

            }
        } else {
            districtsResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    fun getCenters(queries: Map<String, String>) = viewModelScope.launch {
        getCentersSafeCall(queries)
    }

    fun getDistrictCenters(queries: Map<String, String>) = viewModelScope.launch {
        getDistrictCentersSafeCall(queries)
    }

    private suspend fun getCentersSafeCall(queries: Map<String, String>) {
        Log.i(LOG_TAG, "getCentersSafeCall Called");
        if(hasCenterData) {
            centerResponse.value = null
        }
        if(hasInternetConnection()) {
            try {
                Log.i(LOG_TAG, "Calling API ${queries.entries}");
                val response = repository.remote.getCentersByPinCode(queries)
                Log.i(LOG_TAG, "Response: ${response.body()}");
                centerResponse.value = handleCenterResponse(response)
                hasCenterData = true
            } catch (e: Exception) {

            }
        } else {
            centerResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getDistrictCentersSafeCall(queries: Map<String, String>) {
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.getDistrictCentersByDistrictId(queries)
                districtCenterResponse.value = handleCenterResponse(response)
            } catch (e: Exception) {

            }
        } else {
            districtCenterResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleCenterResponse(response: Response<CenterByPincode>): NetworkResult<CenterByPincode>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Error.")
            }
            //TODO: Handle empty body
            response.body()!!.centers.isNullOrEmpty() -> {
                return NetworkResult.Error("Centers Not Found")
            }
            response.isSuccessful -> {
                val centerDetails = response.body()
                return NetworkResult.Success(centerDetails!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleStateResponse(response: Response<States>): NetworkResult<States>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Error.")
            }
            //TODO: Handle empty body
            response.body()!!.states.isNullOrEmpty() -> {
                return NetworkResult.Error("States Not Found")
            }
            response.isSuccessful -> {
                val centerDetails = response.body()
                return NetworkResult.Success(centerDetails!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleDistrictResponse(response: Response<Districts>): NetworkResult<Districts>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Error.")
            }
            //TODO: Handle empty body
            response.body()!!.districts.isNullOrEmpty() -> {
                return NetworkResult.Error("Districts Not Found")
            }
            response.isSuccessful -> {
                val centerDetails = response.body()
                return NetworkResult.Success(centerDetails!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun clear() {
        districtsResponse.value = null
    }

}