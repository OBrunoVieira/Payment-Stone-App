package com.example.paymentstone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stone.environment.Environment

class HomeActivity : AppCompatActivity() {

    private val fabDone by bindView<FloatingActionButton>(R.id.home_fab_done)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fabDone.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

//        for (environment: Environment in Environment.values()) {
//        }
    }
}