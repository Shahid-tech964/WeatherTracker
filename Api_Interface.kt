package com.example.weather_app

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api_Interface {
    @GET("v1/current.json")
    suspend fun getWeatherData(@Query("key") key:String, @Query("q") cityname:String):Response<WeatherDataModel>
}