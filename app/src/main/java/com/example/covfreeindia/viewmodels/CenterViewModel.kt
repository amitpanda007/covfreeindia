package com.example.covfreeindia.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.covfreeindia.util.Constants
import com.example.covfreeindia.util.Constants.Companion.QUERY_AVL_CAP
import com.example.covfreeindia.util.Constants.Companion.QUERY_DATE
import com.example.covfreeindia.util.Constants.Companion.QUERY_DAYS
import com.example.covfreeindia.util.Constants.Companion.QUERY_DISTRICT_ID
import com.example.covfreeindia.util.Constants.Companion.QUERY_MAX_AGE
import com.example.covfreeindia.util.Constants.Companion.QUERY_MIN_AGE
import com.example.covfreeindia.util.Constants.Companion.QUERY_PINCODE

class CenterViewModel(application: Application): AndroidViewModel(application) {

    fun applyQueries(pinCode: String, days: String, minAge: String, maxAge: String, avlQty: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        //pincode=751003&date=01-05-2021
        queries[QUERY_PINCODE] = pinCode
        queries[QUERY_DAYS] = days
        queries[QUERY_MIN_AGE] = minAge
        queries[QUERY_MAX_AGE] = maxAge
        queries[QUERY_AVL_CAP] = avlQty
        //queries[QUERY_DATE] = date

        return  queries
    }

    fun applyDistrictQueries(distID: String, days: String, minAge: String, maxAge: String, avlQty: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        //pincode=751003&date=01-05-2021
        queries[QUERY_DISTRICT_ID] = distID
        queries[QUERY_DAYS] = days
        queries[QUERY_MIN_AGE] = minAge
        queries[QUERY_MAX_AGE] = maxAge
        queries[QUERY_AVL_CAP] = avlQty
        //queries[QUERY_DATE] = date

        return  queries
    }
}