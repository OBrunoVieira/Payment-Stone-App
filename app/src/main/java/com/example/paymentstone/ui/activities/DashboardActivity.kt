package com.example.paymentstone.ui.activities

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.paymentstone.R
import com.example.paymentstone.commons.bindView
import com.example.paymentstone.ui.views.BluetoothDialog
import com.example.paymentstone.ui.views.MessageDialog
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider
import stone.utils.Stone

class DashboardActivity : AppCompatActivity() {

    private val cardViewTransaction by bindView<CardView>(R.id.dashboard_card_view_transaction)
    private val cardViewMessage by bindView<CardView>(R.id.dashboard_card_view_message)
    private val cardViewExit by bindView<CardView>(R.id.dashboard_card_view_logout)
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

        cardViewExit.setOnClickListener {
            showActionDialog(R.string.logout_confirmation, action = fun() {
                ActiveApplicationProvider(this).apply {
                    useDefaultUI(true)
                    dialogTitle = getString(R.string.loading)
                    dialogMessage = getString(R.string.logout_feedback)
                    connectionCallback = object : StoneCallbackInterface {
                        override fun onSuccess() {
                            startActivity(Intent(context, HomeActivity::class.java))
                            finish()
                        }

                        override fun onError() {
                            Toast.makeText(context, R.string.logout_error, Toast.LENGTH_SHORT).show()
                        }

                    }
                }.deactivate()
            })
        }

        buttonConnect.setOnClickListener {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                showActionDialog(R.string.bluetooth_disabled, R.string.settings, fun() {
                    startActivity(Intent().setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
                })
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

    private fun showActionDialog(@StringRes message: Int,
                                 @StringRes positiveText: Int = R.string.ok,
                                 action: () -> Unit) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(positiveText) { dialog, _ ->
                    dialog.dismiss()
                    action()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun isPinPadConnected() = Stone.getPinpadListSize() > 0
}