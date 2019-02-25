package com.example.paymentstone.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.paymentstone.R
import com.example.paymentstone.commons.bindView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stone.environment.Environment

class HomeActivity : AppCompatActivity() {

    private val fabDone by bindView<FloatingActionButton>(R.id.home_fab_done)
    private val editText by bindView<EditText>(R.id.home_edit_text_stone_code)
    private val spinner by bindView<Spinner>(R.id.home_spinner)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        spinner.adapter = recoverSpinnerAdapter()
        fabDone.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun recoverSpinnerAdapter(): ArrayAdapter<String> {
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        for (environment: Environment in Environment.values()) {
            arrayAdapter.add(environment.name)
        }
        return arrayAdapter
    }
}