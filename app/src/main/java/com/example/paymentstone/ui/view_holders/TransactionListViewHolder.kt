package com.example.paymentstone.ui.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.extensions.bindView
import com.example.paymentstone.commons.listeners.OnMenuSelected
import com.example.paymentstone.model.TransactionData
import com.example.paymentstone.model.TransactionData.Companion.CANCEL
import com.example.paymentstone.model.TransactionData.Companion.CAPTURE
import com.example.paymentstone.model.TransactionData.Companion.CLIENT_RECEIPT
import com.example.paymentstone.model.TransactionData.Companion.ESTABLISHMENT_RECEIPT
import stone.application.enums.TransactionStatusEnum

class TransactionListViewHolder(itemView: View, private val listener: OnMenuSelected) : RecyclerView.ViewHolder(itemView) {

    private val viewIndicator by bindView<View>(R.id.transaction_item_view_indicator)
    private val textViewTitle by bindView<TextView>(R.id.transaction_item_text_view_title)
    private val textViewSubTitle by bindView<TextView>(R.id.transaction_item_text_view_sub_title)
    private val textViewDate by bindView<TextView>(R.id.transaction_item_text_view_date)
    private val textViewStatus by bindView<TextView>(R.id.transaction_item_text_view_status)
    private val buttonMenu by bindView<ImageView>(R.id.transaction_list_image_view_menu)
    private var popMenu: PopupMenu

    init {
        popMenu = PopupMenu(itemView.context, buttonMenu).apply {
            inflate(R.menu.menu_transaction)
            setOnMenuItemClickListener(onMenuClicked())
        }

        buttonMenu.setOnClickListener { popMenu.show() }
    }

    fun bind(data: TransactionData) {
        val isTransactionApproved = data.status == TransactionStatusEnum.APPROVED.name
        val formattedTitle = "${data.amount} - ${data.installmentDescription}"

        viewIndicator.setBackgroundResource(if (isTransactionApproved) R.color.atlantis else R.color.pomegranate)

        textViewTitle.text = formattedTitle
        textViewSubTitle.text = data.cardNumber
        textViewDate.text = data.date
        textViewStatus.text = data.status

        popMenu.menu.findItem(R.id.menu_transaction_cancel).isVisible = isTransactionApproved
        popMenu.menu.findItem(R.id.menu_transaction_capture).isVisible = isTransactionApproved
    }

    private fun onMenuClicked() = PopupMenu.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.menu_transaction_client -> {
                listener.onMenuClicked(adapterPosition, CLIENT_RECEIPT)
                return@OnMenuItemClickListener true
            }

            R.id.menu_transaction_establishment -> {
                listener.onMenuClicked(adapterPosition, ESTABLISHMENT_RECEIPT)
                return@OnMenuItemClickListener true
            }

            R.id.menu_transaction_capture -> {
                listener.onMenuClicked(adapterPosition, CAPTURE)
                return@OnMenuItemClickListener true
            }

            else -> {
                listener.onMenuClicked(adapterPosition, CANCEL)
                return@OnMenuItemClickListener true
            }

        }
    }
}