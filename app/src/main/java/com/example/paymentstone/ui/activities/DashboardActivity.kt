package com.example.paymentstone.ui.activities

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.paymentstone.R
import com.example.paymentstone.commons.bindView
import com.example.paymentstone.ui.views.BluetoothDialog
import com.example.paymentstone.ui.views.MessageDialog
import stone.utils.Stone

class DashboardActivity : AppCompatActivity() {

    private val cardViewTransaction by bindView<CardView>(R.id.dashboard_card_view_transaction)
    private val cardViewMessage by bindView<CardView>(R.id.dashboard_card_view_message)
    private val buttonConnect by bindView<Button>(R.id.dashboard_button_pin_pad_action)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        cardViewTransaction.setOnClickListener {
            executeAfterPinPadConnection(fun() {
                startActivity(Intent(this, TransactionActivity::class.java))
            })
        }

        cardViewMessage.setOnClickListener {
            executeAfterPinPadConnection(fun() {
                MessageDialog(this).show()
            })
        }

        buttonConnect.setOnClickListener {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                AlertDialog.Builder(this)
                        .setMessage(R.string.dashboard_bluetooth_disabled)
                        .setPositiveButton(R.string.settings) { dialog, _ ->
                            dialog.dismiss()
                            startActivity(Intent().setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
            } else {
                BluetoothDialog(this).show()
            }
        }
    }

    private fun executeAfterPinPadConnection(target: () -> Unit) {
        if (isPinPadConnected()) {
            target()
        } else {
            AlertDialog.Builder(this)
                    .setTitle(R.string.dashboard_pin_pad_not_connected_title)
                    .setMessage(R.string.dashboard_pin_pad_not_connected_description)
                    .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .show()
        }
    }

    private fun isPinPadConnected() = Stone.getPinpadListSize() > 0
}