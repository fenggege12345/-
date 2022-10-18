package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 封装所有网络请求接口
 */
object SunnyWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>()

    private val weatherService = ServiceCreator.create<WeatherService>()

    //这里使用Retrofit自带的await试试(经过测试可行)
//    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    suspend fun getRealtimeWeather(lng:String,lat:String)= weatherService.getRealTimeWeather(lng,lat).await()

    suspend fun getDailyWeather(lng:String,lat:String)= weatherService.getDailyWeather(lng,lat).await()


    //自定义的优化回调
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { configuration ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        configuration.resume(body)
                    } else {
                        configuration.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    configuration.resumeWithException(t)
                }
            })
        }
    }
}