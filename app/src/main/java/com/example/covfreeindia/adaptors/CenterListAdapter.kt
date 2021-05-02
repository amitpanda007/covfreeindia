package com.example.covfreeindia.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covfreeindia.R
import com.example.covfreeindia.models.Center
import com.example.covfreeindia.models.CenterByPincode
import com.example.covfreeindia.models.Session


class CenterListAdapter: RecyclerView.Adapter<CenterListAdapter.MyViewHolder>() {

    private var centers = emptyList<Center>()
//    private val sessionAdapter by lazy { SessionListAdapter() }
//    private var viewPool = RecyclerView.RecycledViewPool()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val centerName: TextView = itemView.findViewById(R.id.centerNameTextView)
        val centerAddress: TextView = itemView.findViewById(R.id.centerAddressTextView)

        val sessionRecyclerView : RecyclerView = itemView.findViewById(R.id.sessionRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.center_row_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentCenter = centers[position]
//        setSessionRecycler(holder.sessionRecycler, currentCenter.sessions)

//        holder.sessionRecycler.adapter = sessionAdapter
//        holder.sessionRecycler.layoutManager = LinearLayoutManager(holder.sessionRecycler.context, RecyclerView.HORIZONTAL, false)
//        sessionAdapter.setData(currentCenter.sessions)

        holder.sessionRecyclerView.apply {
            layoutManager = LinearLayoutManager(holder.sessionRecyclerView.context, RecyclerView.HORIZONTAL, false)
            adapter = SessionListAdapter(currentCenter.sessions)
//            recycledViewPool = viewPool
        }

        holder.centerName.text = currentCenter.name
        val centerAddress = "${currentCenter.blockName}, ${currentCenter.districtName}, ${currentCenter.stateName}, ${currentCenter.pincode}"
        holder.centerAddress.text = centerAddress
    }

    override fun getItemCount(): Int {
        return centers.size
    }

    fun setData(newData: CenterByPincode) {
        centers = newData.centers
        notifyDataSetChanged()
    }

}