package com.sunnyweather.android

import android.app.Application
import android.content.Context

/**
 * 自定义全局Application
 */
class SunnyWeatherApplication : Application() {

    /**
     * 用于获取全局context
     */
    companion object {
        @SuppressWarnings("StaticFieldLeak")
        lateinit var context: Context

        //天气接口令牌
        const val TOKEN = "8gY1XnBuofxgmgGU"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}