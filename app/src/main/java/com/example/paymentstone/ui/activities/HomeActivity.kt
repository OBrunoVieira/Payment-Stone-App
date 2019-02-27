package com.example.paymentstone.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paymentstone.R
import com.example.paymentstone.commons.bindView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stone.application.interfaces.StoneCallbackInterface
import stone.environment.Environment
import stone.providers.ActiveApplicationProvider

class HomeActivity : AppCompatActivity() {

    private val fabDone by bindView<FloatingActionButton>(R.id.home_fab_done)
    private val editText by bindView<EditText>(R.id.home_edit_text_stone_code)
    private val spinner by bindView<Spinner>(R.id.home_spinner)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        spinner.adapter = recoverSpinnerAdapter()
        fabDone.setOnClickListener { activateApplication() }
    }

    private fun recoverSpinnerAdapter(): ArrayAdapter<String> {
        val environmentNameList = Environment.values().map { it.name }
        return ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, environmentNameList)
    }

    private fun activateApplication() {
        ActiveApplicationProvider(this).apply {
            useDefaultUI(true)
            dialogTitle = context.getString(R.string.loading)
            connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    startActivity(Intent(this@HomeActivity, DashboardActivity::class.java))
                }

                override fun onError() {
                    Toast.makeText(context, R.string.home_activation_error, Toast.LENGTH_SHORT).show()
                }
            }
        }.activate(editText.text.toString())
    }
}