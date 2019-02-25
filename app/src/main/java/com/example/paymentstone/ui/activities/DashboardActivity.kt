package com.example.paymentstone.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.paymentstone.R
import com.example.paymentstone.commons.bindView
import stone.utils.Stone

class DashboardActivity : AppCompatActivity() {

    private val cardViewTransaction by bindView<CardView>(R.id.dashboard_card_view_transaction)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val isPinPadConnected = Stone.getPinpadListSize() > 0
        cardViewTransaction.apply {
//            isEnabled = isPinPadConnected
//            alpha = if (isPinPadConnected) 1f else 0.4f
        }

        cardViewTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }
    }
}