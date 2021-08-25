package com.example.cargo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

const val TAG="ANUJ"
@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}