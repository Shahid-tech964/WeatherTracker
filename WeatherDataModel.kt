package com.example.weather_app

data class WeatherDataModel(
    val current: Current,
    val location: Location,
    val condition: Condition
)