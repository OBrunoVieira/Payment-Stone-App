package com.example.paymentstone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.listeners.OnMenuSelected
import com.example.paymentstone.model.TransactionData
import com.example.paymentstone.ui.view_holders.TransactionListViewHolder

class TransactionListAdapter(var transactionList: MutableList<TransactionData>? = null,
                             var listener: OnMenuSelected) : RecyclerView.Adapter<TransactionListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TransactionListViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_transaction_item, parent, false), listener)

    override fun getItemCount() = transactionList?.size ?: 0

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        transactionList?.let {
            holder.bind(it[position])
        }
    }
}