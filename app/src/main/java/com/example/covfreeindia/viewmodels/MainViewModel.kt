package com.example.covfreeindia.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.covfreeindia.data.Repository
import com.example.covfreeindia.models.CenterByPincode
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

    fun getCenters(queries: Map<String, String>) = viewModelScope.launch {
        getCentersSafeCall(queries)
    }

    private suspend fun getCentersSafeCall(queries: Map<String, String>) {
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.getCentersByPinCode(queries)
                centerResponse.value = handleCenterResponseResponse(response)
            } catch (e: Exception) {

            }
        } else {
            centerResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleCenterResponseResponse(response: Response<CenterByPincode>): NetworkResult<CenterByPincode>? {
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

}