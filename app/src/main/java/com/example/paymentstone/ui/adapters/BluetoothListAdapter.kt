package com.example.paymentstone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.OnItemClickListener
import com.example.paymentstone.model.BluetoothData
import com.example.paymentstone.ui.view_holders.BluetoothListViewHolder

class BluetoothListAdapter(var list: List<BluetoothData>? = null, var listener: OnItemClickListener? = null)
    : RecyclerView.Adapter<BluetoothListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BluetoothListViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_bluetooth_item, parent, false), listener)

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: BluetoothListViewHolder, position: Int) {
        holder.bind(list?.get(position))
    }

}