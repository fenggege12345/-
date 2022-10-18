package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(
    val name: String, val location: Location,
    //由于返回json中命名规范问题，使用SerializedName映射
    @SerializedName("formatted_address") val address: String
)

data class Location(val lng: String, val lat: String)

