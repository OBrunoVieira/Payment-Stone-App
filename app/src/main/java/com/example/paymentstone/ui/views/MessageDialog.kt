package com.example.paymentstone.ui.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.example.paymentstone.R
import com.example.paymentstone.commons.extensions.bindView
import stone.providers.DisplayMessageProvider
import stone.utils.Stone

class MessageDialog(context: Context) : Dialog(context) {
    private val editText by bindView<EditText>(R.id.message_edit_text)
    private val textViewSend by bindView<TextView>(R.id.message_text_view_send)
    private val textViewCancel by bindView<TextView>(R.id.message_text_view_cancel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_message)

        textViewSend.setOnClickListener { sendMessage() }
        textViewCancel.setOnClickListener { dismiss() }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    private fun sendMessage() {
        DisplayMessageProvider(context, editText.text.toString(), Stone.getPinpadFromListAt(0))
                .execute()
        dismiss()
    }
}