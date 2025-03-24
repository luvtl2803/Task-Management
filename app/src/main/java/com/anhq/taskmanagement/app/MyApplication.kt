package com.anhq.taskmanagement.app

import android.app.Application
import com.anhq.taskmanagement.core.sync.SyncInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SyncInitializer.init(this)
    }
}