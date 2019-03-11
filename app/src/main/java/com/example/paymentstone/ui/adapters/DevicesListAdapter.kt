package com.example.paymentstone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.listeners.OnItemClickListener
import com.example.paymentstone.model.DevicesData
import com.example.paymentstone.ui.view_holders.DevicesListViewHolder

class DevicesListAdapter(var list: List<DevicesData>? = null, var listener: OnItemClickListener? = null)
    : RecyclerView.Adapter<DevicesListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DevicesListViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_bluetooth_item, parent, false), listener)

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: DevicesListViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

}