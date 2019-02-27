package com.example.paymentstone.ui.views

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.OnItemClickListener
import com.example.paymentstone.commons.bindView
import com.example.paymentstone.model.BluetoothData
import com.example.paymentstone.ui.adapters.BluetoothListAdapter
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.BluetoothConnectionProvider
import stone.utils.PinpadObject

class BluetoothDialog(context: Context) : Dialog(context), OnItemClickListener {

    private val recyclerView by bindView<RecyclerView>(R.id.bluetooth_recycler_view)
    private lateinit var adapter: BluetoothListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_bluetooth)

        val list = BluetoothAdapter.getDefaultAdapter().bondedDevices?.map { BluetoothData(it.name, it.address) }
        adapter = BluetoothListAdapter(list, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClicked(position: Int) {
        val bluetoothData = adapter.list?.get(position)
        val pinPadObject = PinpadObject(bluetoothData?.name, bluetoothData?.address, false)

        BluetoothConnectionProvider(context, pinPadObject).apply {
            dialogTitle = context.getString(R.string.loading)
            useDefaultUI(true)
            connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    Toast.makeText(context, R.string.dashboard_pin_pad_connected, Toast.LENGTH_SHORT).show()
                    dismiss()
                }

                override fun onError() {
                    Toast.makeText(context, R.string.dashboard_pin_pad_error, Toast.LENGTH_SHORT).show()
                }

            }
        }.execute()
    }
}