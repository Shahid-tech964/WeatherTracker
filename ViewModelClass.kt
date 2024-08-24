package com.example.weather_app

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import kotlin.math.log

class ViewModelClass:ViewModel() {

    val apiInstance =Retrofit_Instance.getInstance().create(Api_Interface::class.java)
    val key="e659d4494b5349a48e973315242706 "
    val _weatherResult= MutableLiveData<NetworkState<WeatherDataModel>>()
    val weatherResult:LiveData<NetworkState<WeatherDataModel>> =_weatherResult

    fun getdefault(){
//        _weatherResult.value=NetworkState.Loading
        viewModelScope.launch {
           val Default_Response = apiInstance.getWeatherData(key, "Delhi India ")
            if (Default_Response.isSuccessful){
                Default_Response.body()?.let {
                    _weatherResult.value=NetworkState.Default(it)
                }
            }
        }

    }


    fun getcity(city:String){
        if (city.isEmpty()){
        }
        else {
            _weatherResult.value = NetworkState.Loading
            viewModelScope.launch {
                val response = apiInstance.getWeatherData(key, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkState.Success(it)
                    }
                } else {

                    _weatherResult.value = NetworkState.Error("Sorry! Data not Found")
                }
            }
        }

    }
}