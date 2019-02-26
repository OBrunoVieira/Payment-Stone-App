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
import stone.utils.Stone

class DashboardActivity : AppCompatActivity() {

    private val cardViewTransaction by bindView<CardView>(R.id.dashboard_card_view_transaction)
    private val buttonConnect by bindView<Button>(R.id.dashboard_button_pin_pad_action)
    private lateinit var bluetoothDialog: BluetoothDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        bluetoothDialog = BluetoothDialog(this)

        val isPinPadConnected = Stone.getPinpadListSize() > 0
        cardViewTransaction.apply {
            //            isEnabled = isPinPadConnected
//            alpha = if (isPinPadConnected) 1f else 0.4f
        }

        cardViewTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        buttonConnect.setOnClickListener {

            if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                AlertDialog.Builder(this)
                        .setMessage(R.string.dashboard_bluetooth_disabled)
                        .setPositiveButton(R.string.settings) { dialog, _ ->
                            dialog.dismiss()
                            startActivity(Intent().setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            } else {
                bluetoothDialog.show()
            }
        }
    }
}