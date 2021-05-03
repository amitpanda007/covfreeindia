package com.example.covfreeindia.ui.fragments.list

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covfreeindia.R
import com.example.covfreeindia.adaptors.CenterListAdapter
import com.example.covfreeindia.adaptors.DistrictListAdapter
import com.example.covfreeindia.util.Constants
import com.example.covfreeindia.util.Constants.Companion.LOG_TAG
import com.example.covfreeindia.util.NetworkResult
import com.example.covfreeindia.viewmodels.CenterViewModel
import com.example.covfreeindia.viewmodels.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DistrictListFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var centerViewModel: CenterViewModel
    private val districtAdapter by lazy { DistrictListAdapter() }
    private lateinit var districtRecyclerView: RecyclerView
    private lateinit var mView: View
    private var stateOptionMap = mutableMapOf<String, String>()
    private var stateOptionList = mutableListOf<String>()
    private var districtOptionMap = mutableMapOf<String, String>()
    private var districtOptionList = mutableListOf<String>()

    private val httpClient = OkHttpClient()

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
        mView = inflater.inflate(R.layout.fragment_district_list, container, false)
        districtRecyclerView = mView.findViewById(R.id.districtRecyclerView)

        // Setup States Dropdown
        val autoCompleteStateView = mView.findViewById<AutoCompleteTextView>(R.id.stateOptions)
        val arrayStateAdaptor = ArrayAdapter(requireContext(), R.layout.dropdown_item, stateOptionList)
        autoCompleteStateView.setAdapter(arrayStateAdaptor)

        //Setup Districts Dropdown
        val autoCompleteDistrictView = mView.findViewById<AutoCompleteTextView>(R.id.districtOptions)
        val arrayDistrictAdaptor = ArrayAdapter(requireContext(), R.layout.dropdown_item, districtOptionList)
        autoCompleteDistrictView.setAdapter(arrayDistrictAdaptor)

        autoCompleteStateView.setOnItemClickListener {_, _, position, _ ->
            val value = arrayStateAdaptor.getItem(position) ?: ""
            val stateId = stateOptionMap[value]
            Log.i(LOG_TAG, "Key for $value is $stateId");

            districtOptionList.clear()
            arrayDistrictAdaptor.clear()

            mainViewModel.getDistricts(stateId.toString())
            mainViewModel.districtsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it?.let {
                    val distItr = it.data?.districts?.listIterator()
                    distItr!!.forEach {
                        districtOptionList.add(it.districtName)
                        districtOptionMap[it.districtName] = it.districtId.toString()
                    }
                }
            })

            /*mainViewModel.districtsResponse.observe(viewLifecycleOwner, { resp ->
                val distItr = resp.data?.districts?.listIterator()
                distItr!!.forEach {
                    districtOptionList.add(it.districtName)
                    districtOptionMap[it.districtName] = it.districtId.toString()
                }
            })*/
        }

        autoCompleteDistrictView.setOnItemClickListener {_, _, position, _ ->
            val value = arrayDistrictAdaptor.getItem(position) ?: ""
            val districtId = districtOptionMap[value].toString()

            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val currentDate = sdf.format(Date())

            requestApiData(districtId, currentDate)
        }

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
        Log.i(Constants.LOG_TAG, "Application has Location Permission")
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val geoCoder = Geocoder(requireContext())
            val currentLocation = geoCoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            Log.d(Constants.LOG_TAG, "${location.latitude}, ${location.longitude}")
            Log.d(Constants.LOG_TAG, currentLocation.first().toString())
            Log.d(Constants.LOG_TAG, currentLocation.first().postalCode)

            val state = currentLocation.first().adminArea
            val district = currentLocation.first().subAdminArea
            Log.i(LOG_TAG, "Current State: $state")
            Log.i(LOG_TAG, "Current District: $district")

//            val sdf = SimpleDateFormat("dd-MM-yyyy")
//            val currentDate = sdf.format(Date())

            // TODO:GET & Calculate district id
            mainViewModel.getStates()
            mainViewModel.statesResponse.observe(viewLifecycleOwner, { response ->
                val stateItr = response.data?.states?.listIterator()
                stateItr!!.forEach {
                    stateOptionList.add(it.stateName)
                    stateOptionMap[it.stateName] = it.stateId.toString()
                }
            })

//            requestApiData("446", currentDate)
        }
    }

    private fun requestApiData(distId: String, date: String) {
        mainViewModel.getDistrictCenters(centerViewModel.applyDistrictQueries(distId, date))
        mainViewModel.districtCenterResponse.observe(viewLifecycleOwner, { response ->
            Log.i(Constants.LOG_TAG, response.toString())
            when(response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        districtAdapter.setData(it)
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
        districtRecyclerView.adapter = districtAdapter
        districtRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
            CenterListFragment.PERMISSION_LOCATION_REQUEST_CODE,
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