package com.example.weatherclient.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherclient.converter.ResponseToWeatherDetailConverter
import com.example.weatherclient.data.response.CurrentWeatherResponse
import com.example.weatherclient.model.WeatherDetails
import com.example.weatherclient.repository.WeatherRepository
import com.example.weatherclient.repository.WeatherRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*
import kotlin.Exception

class WeatherViewModel : ViewModel() {
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl
    private val currentWeatherStateFlow: MutableStateFlow<UIStateCurrentWeather> =
        MutableStateFlow(UIStateCurrentWeather.Loading)
    val currentWeatherStateFlowPublic = currentWeatherStateFlow.asStateFlow()


    fun getWeatherDetails(location: String, apikey: String) {
        viewModelScope.launch {
            try{
                val response = withContext(Dispatchers.IO) {
                    weatherRepository.getCurrentWeather(location, apikey)
                }
                val weatherDetails = ResponseToWeatherDetailConverter().convert(response)
                currentWeatherStateFlow.value = UIStateCurrentWeather.Success(weatherDetails)

            }
            catch(e: Exception){
                Log.e("HELLO", e.message.toString())

                currentWeatherStateFlow.value = UIStateCurrentWeather.Error(e)
            }
        }
    }

}

sealed class UIStateCurrentWeather {
    object Loading : UIStateCurrentWeather()
    class Error(e: Exception) : UIStateCurrentWeather()
    class Success(val currentWeather: WeatherDetails) : UIStateCurrentWeather()
}




