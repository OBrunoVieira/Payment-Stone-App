package com.example.paymentstone

import android.app.Application
import stone.utils.Stone

class StoneApplication : Application() {

    companion object {
        lateinit var instance: StoneApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stone.setAppName(getString(R.string.app_name))
    }
}