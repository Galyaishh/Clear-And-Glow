package com.example.clear_and_glow

import android.app.Application
import com.example.clear_and_glow.utilities.ImageLoader

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        ImageLoader.init(this)
        }
}