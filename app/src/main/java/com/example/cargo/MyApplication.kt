package com.example.cargo

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

const val TAG="ANUJ"
@HiltAndroidApp
class MyApplication: Application() {
    companion object{
        private lateinit var myContext: WeakReference<Context>
        fun appContext()= myContext
    }
    override fun onCreate() {
        myContext = WeakReference(applicationContext)
        super.onCreate()
    }
}