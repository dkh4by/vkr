package com.example.electronicreception

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class ElectronicReceptionApp : Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("0c1abcd0-0d25-4f17-a0f0-ff87e8b8e6e6")
        MapKitFactory.initialize(this)
    }
}