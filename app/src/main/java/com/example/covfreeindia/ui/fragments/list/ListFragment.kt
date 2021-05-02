package com.example.covfreeindia.ui.fragments.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covfreeindia.viewmodels.MainViewModel
import com.example.covfreeindia.R
import com.example.covfreeindia.adaptors.CenterListAdapter
import com.example.covfreeindia.util.Constants.Companion.LOG_TAG
import com.example.covfreeindia.util.NetworkResult
import com.example.covfreeindia.viewmodels.CenterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var centerViewModel: CenterViewModel
    private val centerAdapter by lazy { CenterListAdapter() }
    private lateinit var centerRecyclerView: RecyclerView
    private lateinit var mView: View

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

        setupRecyclerView()
        requestApiData()

        return mView
    }

    private fun requestApiData() {
        mainViewModel.getCenters(centerViewModel.applyQueries())
        mainViewModel.centerResponse.observe(viewLifecycleOwner, { response ->
            Log.i(LOG_TAG, response.toString())
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
}