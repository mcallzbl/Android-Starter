package com.mcallzbl.android_starter.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

open class BaseService : Service() {

    override fun onCreate() {
        super.onCreate()
        Timber.d("${this::class.java.simpleName} is starting")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("${this::class.java.simpleName} is closing")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}