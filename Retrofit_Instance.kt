package com.example.weather_app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit_Instance {
    fun getInstance():Retrofit{
        return Retrofit.Builder().baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

}