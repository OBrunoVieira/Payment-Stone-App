package com.example.paymentstone.ui.view_holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.OnItemClickListener
import com.example.paymentstone.commons.bindView
import com.example.paymentstone.model.BluetoothData

class BluetoothListViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
    private val textViewTitle by bindView<TextView>(R.id.bluetooth_item_text_view_title)
    private val textViewAddress by bindView<TextView>(R.id.bluetooth_item_text_view_address)

    init {
        itemView.setOnClickListener {
            listener?.onItemClicked(adapterPosition)
        }
    }

    fun bind(data: BluetoothData?) {
        textViewTitle.text = data?.name
        textViewAddress.text = data?.address
    }
}