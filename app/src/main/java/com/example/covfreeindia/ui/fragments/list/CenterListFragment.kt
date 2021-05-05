package com.example.covfreeindia.ui.fragments.list

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covfreeindia.viewmodels.MainViewModel
import com.example.covfreeindia.R
import com.example.covfreeindia.adaptors.CenterListAdapter
import com.example.covfreeindia.util.Constants.Companion.LOG_TAG
import com.example.covfreeindia.util.NetworkResult
import com.example.covfreeindia.viewmodels.CenterViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CenterListFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var centerViewModel: CenterViewModel
    private val centerAdapter by lazy { CenterListAdapter() }
    private lateinit var centerRecyclerView: RecyclerView
    private lateinit var mView: View

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        centerViewModel = ViewModelProvider(requireActivity()).get(CenterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_list, container, false)
        centerRecyclerView = mView.findViewById(R.id.centerRecyclerView)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if(hasLocationPermission()) {
            getCurrentLocation()
        }else {
            requestLocationPermission()
        }

        setupRecyclerView()
//        requestApiData()

        return mView
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        Log.i(LOG_TAG, "Application has Location Permission")
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val geoCoder = Geocoder(requireContext())
            val currentLocation = geoCoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            Log.d(LOG_TAG, "${location.latitude}, ${location.longitude}")
            Log.d(LOG_TAG, currentLocation.first().locality)
            Log.d(LOG_TAG, currentLocation.first().postalCode)

            // QUERY DATA FOR API

            /*val sdf = SimpleDateFormat("dd-MM-yyyy")
            val sevenDaysTime = 1000 * 60 * 60 * 24 * 7
            val today = Date()
            val currentDate = sdf.format(today)*/
            val postalCode = currentLocation.first().postalCode
            val days = "28"
            val minAge = "18"
            val maxAge = "45"
            val avlQty = "1"

            requestApiData(postalCode, days, minAge, maxAge, avlQty)
        }
    }

    private fun requestApiData(pinCode: String, days: String, minAge: String, maxAge: String, avlQty: String) {
        mainViewModel.getCenters(centerViewModel.applyQueries(pinCode, days, minAge, maxAge, avlQty))
        mainViewModel.centerResponse.observe(viewLifecycleOwner, { response ->
            // Log.i(LOG_TAG, response.toString())
            when(response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        centerAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    Toast.makeText(
                        requireContext(),
                        "Gathering Data...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        centerRecyclerView.adapter = centerAdapter
        centerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This Application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        getCurrentLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms.first())) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
    }
}