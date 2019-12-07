package com.ait.weatherapp.api

import retrofit2.Call
import com.ait.weatherapp.data.CityWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("data/2.5/weather/")
    fun getCityWeather(
        @Query("q") base: String,
        @Query("units") units: String,
        @Query("appid") apiid: String
    ): Call<CityWeather>



}


