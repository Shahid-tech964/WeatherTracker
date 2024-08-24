package com.example.weather_app

sealed class NetworkState< out T> {
    data class Success<out T>(val data:T):NetworkState< T>()
    data class Error(val error:String):NetworkState<Nothing>()
    object Loading:NetworkState<Nothing>()
    data class Default<out T>(val message:T) : NetworkState<T>()
}
