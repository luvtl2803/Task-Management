package com.anhq.todolist.app

import android.app.Application
import com.anhq.todolist.core.sync.SyncInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SyncInitializer.init(this)
    }
}