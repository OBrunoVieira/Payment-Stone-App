package com.example.paymentstone.ui.views

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentstone.R
import com.example.paymentstone.commons.OnItemClickListener
import com.example.paymentstone.commons.bindView
import com.example.paymentstone.model.DevicesData
import com.example.paymentstone.ui.adapters.DevicesListAdapter
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.BluetoothConnectionProvider
import stone.utils.PinpadObject
import stone.utils.Stone

class DevicesDialog(context: Context) : Dialog(context), OnItemClickListener {

    private val recyclerView by bindView<RecyclerView>(R.id.devices_recycler_view)
    private val textViewTitle by bindView<TextView>(R.id.devices_text_view_title)
    private val textViewDevices by bindView<TextView>(R.id.devices_text_view_devices)

    private lateinit var adapter: DevicesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_devices)

        val isPinPadConnected = isPinPadConnected()
        val list = recoverDevicesList(isPinPadConnected)
        adapter = DevicesListAdapter(list, this)
        recyclerView.adapter = adapter

        val colorResource = if (isPinPadConnected) R.color.pomegranate else R.color.atlantis
        textViewTitle.setBackgroundResource(colorResource)
        textViewDevices.setTextColor(ContextCompat.getColor(context, colorResource))
    }

    override fun onItemClicked(position: Int) {
        val bluetoothData = adapter.list?.get(position)
        val pinPadObject = PinpadObject(bluetoothData?.name, bluetoothData?.address, false)

        if (isPinPadConnected()) {
            disconnectDevice(Stone.getPinpadFromListAt(position))
        } else {
            connectDevice(pinPadObject)
        }
    }

    private fun connectDevice(pinPadObject: PinpadObject) {
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

    private fun disconnectDevice(pinPadObject: PinpadObject) {
        Stone.removePinpadAtIndex(pinPadObject)
        dismiss()
    }

    private fun recoverDevicesList(isPinPadConnected: Boolean): List<DevicesData>? {
        return if (isPinPadConnected)
            Stone.getPinpadObjectList().map { DevicesData(it.name, it.macAddress) }
        else
            BluetoothAdapter.getDefaultAdapter().bondedDevices?.map { DevicesData(it.name, it.address) }
    }

    private fun isPinPadConnected() = Stone.getPinpadListSize() > 0
}