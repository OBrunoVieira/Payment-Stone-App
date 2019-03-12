package com.example.paymentstone.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.paymentstone.R
import com.example.paymentstone.commons.extensions.bindView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stone.application.interfaces.StoneCallbackInterface
import stone.environment.Environment
import stone.providers.ActiveApplicationProvider
import stone.utils.Stone
import stone.application.StoneStart


class HomeActivity : BaseActivity() {

    private val fabDone by bindView<FloatingActionButton>(R.id.home_fab_done)
    private val editText by bindView<EditText>(R.id.home_edit_text_stone_code)
    private val spinner by bindView<Spinner>(R.id.home_spinner)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.paymentstone.R.layout.activity_home)

        if (StoneStart.init(this) != null) {
            Stone.setEnvironment(Environment.SANDBOX)
            redirectToDashboard()
            return
        }

        fabDone.setOnClickListener { activateApplication() }
        setupSpinner()
    }

    private fun setupSpinner() {
        val environmentNameList = Environment.values().map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, environmentNameList)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Stone.setEnvironment(Environment.PRODUCTION)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = Environment.valueOf(
                        adapter.getItem(position)
                                ?: Environment.PRODUCTION.name
                )
                Stone.setEnvironment(item)
            }
        }
    }

    private fun activateApplication() {
        ActiveApplicationProvider(this).apply {
            useDefaultUI(true)
            dialogTitle = context.getString(R.string.loading)
            connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    redirectToDashboard()
                }

                override fun onError() {
                    Toast.makeText(context, R.string.home_activation_error, Toast.LENGTH_SHORT).show()
                }
            }
        }.activate(editText.text.toString())
    }

    private fun redirectToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}