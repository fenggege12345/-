package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    //这里两个方法的try-catch太繁琐，见下面优化
    //创建place的liveData
//    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
//        val result = try {
//            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
//            if (placeResponse.status == "ok") {
//                val places = placeResponse.places
//                Result.success(places)
//            } else {
//                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
//            }
//        } catch (e: Exception) {
//            Result.failure<List<Place>>(e)
//        }
//        emit(result)
//    }

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

//    fun refreshWeather(lng: String, lat: String) = liveData(Dispatchers.IO) {
//        val result = try {
//            coroutineScope {
//                val deferredRealtime = async {
//                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
//                }
//                val deferredDaily = async {
//                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
//                }
//                val realTimeResponse = deferredRealtime.await()
//                val dailyResponse = deferredDaily.await()
//                if (realTimeResponse.status == "ok" && dailyResponse.status == "ok") {
//                    val weather =
//                        Weather(realTimeResponse.result.realtime, dailyResponse.result.daily)
//                    Result.success(weather)
//                } else {
//                    Result.failure(RuntimeException("realtime response status is ${realTimeResponse.status}" + "daily response status is ${dailyResponse.status}"))
//                }
//            }
//
//        } catch (e: Exception) {
//            Result.failure<Weather>(e)
//        }
//        emit(result)
//    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
            coroutineScope {
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realTimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realTimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather =
                        Weather(realTimeResponse.result.realtime, dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(RuntimeException("realtime response status is ${realTimeResponse.status}" + "daily response status is ${dailyResponse.status}"))
                }
            }
    }

    //sp操作应该也在子线程进行，这里不使用标准写法了
    fun savePlace(place: Place)=PlaceDao.savePlace(place)

    fun getSavedPlace()=PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}