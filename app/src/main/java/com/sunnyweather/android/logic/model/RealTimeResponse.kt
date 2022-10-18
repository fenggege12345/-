package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

data class RealTimeResponse(val status: String, val result: Result) {

    //将所有数据模型类定义在内部，防止与其他接口出现同名冲突
    data class Result(val realtime: Realtime)
    data class Realtime(
        val skycon: String,
        val temperature: Float,
        @SerializedName("air_quality") val airQuality: AirQuality
    )

    data class AirQuality(val aqi: AQI)
    data class AQI(val chn: Float)
}