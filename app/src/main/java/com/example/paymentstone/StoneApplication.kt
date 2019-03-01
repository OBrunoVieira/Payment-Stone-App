package com.example.paymentstone

import android.app.Application
import stone.application.StoneStart
import stone.utils.Stone

class StoneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stone.setAppName(getString(R.string.app_name))
    }
}