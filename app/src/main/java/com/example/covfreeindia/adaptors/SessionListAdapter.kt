package com.example.covfreeindia.adaptors

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.covfreeindia.R
import com.example.covfreeindia.models.Session
import com.example.covfreeindia.util.Constants.Companion.LOG_TAG
import com.google.android.material.chip.Chip

class SessionListAdapter(private val sessions : List<Session>): RecyclerView.Adapter<SessionListAdapter.MyViewHolder>() {

//    private var sessions = emptyList<Session>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateOne: TextView = itemView.findViewById(R.id.dateTextViewOne)
        val chipOne: Chip = itemView.findViewById(R.id.slotStatusChipOne)
        val ageGroupOne: TextView = itemView.findViewById(R.id.minAgeTextOne)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.session_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentSession = sessions[position]
        Log.i(LOG_TAG, "${currentSession.date},${currentSession.availableCapacity},${currentSession.minAgeLimit}")

        //Set Date, Slots & Age group Info
        holder.dateOne.text = currentSession.date

        // Set Chip Color & text
        if (currentSession.availableCapacity > 0) {
            holder.chipOne.text = currentSession.availableCapacity.toString()
            holder.chipOne.chipBackgroundColor =  ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.teal_200))
        } else {
            holder.chipOne.text = "Booked"
            holder.chipOne.chipBackgroundColor =  ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.red_400))
        }
        holder.ageGroupOne.text = "${currentSession.minAgeLimit}+"
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    /*fun setData(session: List<Session>) {
        sessions = session
        notifyDataSetChanged()
    }*/
}