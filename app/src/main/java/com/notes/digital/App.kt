package com.notes.digital

import android.app.Application
import com.notes.digital.core.di.AppContainer
import com.notes.digital.core.di.DefaultAppContainer

class App : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
