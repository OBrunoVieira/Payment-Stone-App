package com.example.paymentstone.ui.activities

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.extensions.bindView
import com.example.paymentstone.commons.extensions.toReadableString
import com.example.paymentstone.commons.extensions.transformToCurrency
import com.example.paymentstone.commons.listeners.OnMenuSelected
import com.example.paymentstone.model.TransactionData
import com.example.paymentstone.model.TransactionData.Companion.CANCEL
import com.example.paymentstone.model.TransactionData.Companion.CAPTURE
import com.example.paymentstone.model.TransactionData.Companion.CLIENT_RECEIPT
import com.example.paymentstone.model.TransactionData.Companion.ESTABLISHMENT_RECEIPT
import com.example.paymentstone.ui.adapters.TransactionListAdapter
import stone.application.enums.ReceiptType
import stone.application.interfaces.StoneCallbackInterface
import stone.database.transaction.TransactionDAO
import stone.database.transaction.TransactionObject
import stone.providers.CancellationProvider
import stone.providers.CaptureTransactionProvider
import stone.providers.SendEmailTransactionProvider
import stone.repository.remote.email.pombo.email.Contact

class TransactionListActivity : BaseActivity() {
    private val toolbar by bindView<Toolbar>(R.id.transaction_list_toolbar)
    private val recyclerView by bindView<RecyclerView>(R.id.transaction_list_recycler_view)
    private val textViewEmpty by bindView<TextView>(R.id.transaction_list_text_view_empty)
    private val adapter = TransactionListAdapter(listener = recoverMenuListener())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = adapter
        recoverTransactionList()
    }

    private fun recoverTransactionList() {
        val transactionDataList = recoverTransactionDataList()
        if (transactionDataList.isNotEmpty()) {
            recyclerView.visibility = VISIBLE
            textViewEmpty.visibility = GONE

            adapter.transactionList = transactionDataList
            adapter.notifyDataSetChanged()
        } else {
            recyclerView.visibility = GONE
            textViewEmpty.visibility = VISIBLE
        }
    }

    private fun recoverMenuListener() = object : OnMenuSelected {
        override fun onMenuClicked(position: Int, type: Int) {
            val transactionObjectList = getTransactionObjectList()
            when (type) {
                CLIENT_RECEIPT -> {
                    sendReceipt(transactionObjectList[position], ReceiptType.CLIENT)
                }

                ESTABLISHMENT_RECEIPT -> {
                    sendReceipt(transactionObjectList[position], ReceiptType.MERCHANT)
                }

                CAPTURE -> {
                    captureTransaction(transactionObjectList[position])
                }

                CANCEL -> {
                    cancelTransaction(position)
                }
            }
        }
    }

    private fun captureTransaction(transactionObject: TransactionObject) {
        CaptureTransactionProvider(this, transactionObject).apply {
            dialogMessage = getString(R.string.transaction_capture_feedback)
            connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    Toast.makeText(this@TransactionListActivity, R.string.transaction_capture_success, LENGTH_SHORT).show()
                }

                override fun onError() {
                    Toast.makeText(this@TransactionListActivity, R.string.transaction_capture_error, LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun cancelTransaction(position: Int) {
        val transactionObjectList = getTransactionObjectList()
        val transactionObject = transactionObjectList[position]
        CancellationProvider(this, transactionObject).apply {
            dialogMessage = getString(R.string.transaction_cancel_feedback)
            connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    Toast.makeText(applicationContext, messageFromAuthorize, LENGTH_SHORT).show()
                    adapter.transactionList = recoverTransactionDataList()
                    adapter.notifyDataSetChanged()
                }

                override fun onError() {
                    Toast.makeText(applicationContext, getString(R.string.transaction_cancel_error, transactionObject.idFromBase), Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    private fun sendReceipt(transactionObject: TransactionObject, receiptType: ReceiptType) {
        SendEmailTransactionProvider(this, transactionObject, receiptType).apply {
            addTo(Contact("cliente@email.com", "Nome do cliente"))
            from = Contact("loja@email.com", "Nome do estabelecimento")
            dialogMessage = getString(R.string.transaction_receipt_feedback)
            connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    Toast.makeText(this@TransactionListActivity, R.string.transaction_receipt_success, Toast.LENGTH_SHORT).show()
                }

                override fun onError() {
                    Toast.makeText(this@TransactionListActivity, R.string.transaction_receipt_error, Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    private fun recoverTransactionDataList() =
            TransactionDAO(this).allTransactionsOrderByIdDesc
                    .map {
                        TransactionData(
                                it.amount.toDouble().transformToCurrency(),
                                it.instalmentTransaction.toReadableString(),
                                "${it.date} ${it.time}",
                                it.cardHolderNumber,
                                it.transactionStatus.name
                        )
                    }.toMutableList()

    private fun getTransactionObjectList() = TransactionDAO(this@TransactionListActivity).allTransactionsOrderByIdDesc
}