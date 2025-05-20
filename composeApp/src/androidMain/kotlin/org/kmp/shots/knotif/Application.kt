package org.kmp.shots.knotif

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.setUp(applicationContext)
    }
}