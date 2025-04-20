package com.mcallzbl.android_starter.base

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import timber.log.Timber

open class BaseActivity: ComponentActivity() {
    /**
     * 在Activity创建时调用，记录Activity的启动日志。
     *
     * @param savedInstanceState 如果Activity是第一次创建，则为null；否则包含之前保存的状态。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableHighRefreshRate()
        super.onCreate(savedInstanceState)
        Timber.d("${this::class.java.simpleName} is starting")
    }

    /**
     * 让页面支持高刷
     */
    private fun enableHighRefreshRate(){
        val modes = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            this.display?.supportedModes
        else{
            @Suppress("DEPRECATION")
            window.windowManager.defaultDisplay.supportedModes
        }
        modes?.sortBy {
            it.refreshRate
        }

        if(modes != null){
            window.let {
                val lp = it.attributes
                lp.preferredDisplayModeId = modes.last().modeId
                it.attributes = lp
            }
        }
    }

    /**
     * 在Activity销毁时调用，记录Activity的关闭日志。
     */
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("${this::class.java.simpleName} is closing")
    }
}