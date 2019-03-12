package com.example.paymentstone.ui.activities

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.paymentstone.R
import com.example.paymentstone.commons.extensions.bindView
import com.example.paymentstone.ui.views.DevicesDialog
import com.example.paymentstone.ui.views.MessageDialog
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider
import stone.providers.ReversalProvider
import stone.utils.Stone

class DashboardActivity : AppCompatActivity() {

    private val cardViewTransaction by bindView<CardView>(R.id.dashboard_card_view_transaction)
    private val cardViewMessage by bindView<CardView>(R.id.dashboard_card_view_message)
    private val cardViewExit by bindView<CardView>(R.id.dashboard_card_view_logout)
    private val cardViewErrorTransaction by bindView<CardView>(R.id.dashboard_card_view_error_transaction)
    private val cardViewTransactionList by bindView<CardView>(R.id.dashboard_card_view_transaction_list)
    private val textViewPinPadStatus by bindView<TextView>(R.id.dashboard_text_view_pin_pad_status)
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

        cardViewTransactionList.setOnClickListener { startActivity(Intent(this, TransactionListActivity::class.java)) }

        cardViewErrorTransaction.setOnClickListener { setupErrorTransaction() }

        cardViewExit.setOnClickListener { setupExit() }

        buttonConnect.setOnClickListener { setupConnectButton() }
    }

    override fun onResume() {
        super.onResume()
        checkPinPadStatus()
    }

    private fun checkPinPadStatus() {
        val isPinPadConnected = isPinPadConnected()
        textViewPinPadStatus.apply {
            setText(if (isPinPadConnected) R.string.dashboard_pin_pad_connected else R.string.dashboard_pin_pad_not_connected)
            setCompoundDrawablesWithIntrinsicBounds(
                    if (isPinPadConnected) R.drawable.shape_circle_green else R.drawable.shape_circle_gray,
                    0, 0, 0
            )
        }

        buttonConnect.apply {
            setText(if (isPinPadConnected) R.string.dashboard_disconnect else R.string.dashboard_connect)
            setTextColor(ContextCompat.getColor(context, if (isPinPadConnected) R.color.pomegranate else R.color.atlantis))
            setBackgroundResource(if (isPinPadConnected) R.drawable.shape_outline_red else R.drawable.shape_outline_green)
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

    private fun setupConnectButton() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            showActionDialog(R.string.bluetooth_disabled, R.string.settings, fun() {
                startActivity(Intent().setAction(Settings.ACTION_BLUETOOTH_SETTINGS))
            })
        } else {
            val bluetoothDialog = DevicesDialog(this)
            bluetoothDialog.setOnDismissListener { checkPinPadStatus() }
            bluetoothDialog.show()
        }
    }

    private fun setupExit() {
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
                        Toast.makeText(context, R.string.logout_error, LENGTH_SHORT).show()
                    }

                }
            }.deactivate()
        })
    }

    private fun setupErrorTransaction() {
        showActionDialog(R.string.transaction_error_confirmation, action = fun() {
            ReversalProvider(this).apply {
                dialogMessage = getString(R.string.transaction_error_loading)
                connectionCallback = object : StoneCallbackInterface {
                    override fun onSuccess() {
                        Toast.makeText(this@DashboardActivity, R.string.transaction_error_got_success, LENGTH_SHORT).show()
                    }

                    override fun onError() {
                        Toast.makeText(this@DashboardActivity, R.string.transaction_error_got_fail, LENGTH_SHORT).show()
                    }

                }
            }.execute()
        })
    }

    private fun showActionDialog(@StringRes message: Int,
                                 @StringRes positiveText: Int = R.string.yes,
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