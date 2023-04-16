package com.mihir.podcast

import android.app.Application
import com.facebook.stetho.Stetho
import com.mihir.podcast.ui.BuildConfig

class PodcastApplication:  Application() {
    override fun onCreate() {
        super.onCreate()
        //TODO: Initialize app database
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}