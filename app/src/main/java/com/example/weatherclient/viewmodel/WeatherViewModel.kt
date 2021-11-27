package com.example.weatherclient.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherclient.converter.ResponseToWeatherDetailConverter
import com.example.weatherclient.data.response.CurrentWeatherResponse
import com.example.weatherclient.model.DailyWeather
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
    private val hourlyWeatherStateFlow: MutableStateFlow<UIStateHourlyWeather> =
        MutableStateFlow(UIStateHourlyWeather.Loading)
    private val dailyWeatherStateFlow: MutableStateFlow<UIStateDailyWeather> =
        MutableStateFlow(UIStateDailyWeather.Loading)
    val currentWeatherStateFlowPublic = currentWeatherStateFlow.asStateFlow()
    val hourlyWeatherStateFlowPublic = hourlyWeatherStateFlow.asStateFlow()
    val dailyWeatherStateFlowPublic = dailyWeatherStateFlow.asStateFlow()


    fun getWeatherDetails(location: String,apikey: String) {
        viewModelScope.launch {
            try{
                val response = withContext(Dispatchers.IO) {
                    weatherRepository.getCurrentWeather(location, apikey)
                }
                val weatherDetails = ResponseToWeatherDetailConverter().convert(response)
                currentWeatherStateFlow.value = UIStateCurrentWeather.Success(weatherDetails)

                try{
                    val responseHourly = withContext(Dispatchers.IO){
                        weatherRepository.getHourlyWeather(response.coord.lat, response.coord.lon, "daily", apikey)
                    }
                    val hourlyWeatherDetails = ResponseToWeatherDetailConverter().convertHourly(responseHourly)
                    hourlyWeatherStateFlow.value = UIStateHourlyWeather.Success(hourlyWeatherDetails)
                }
                catch (e: Exception){
                    Log.e("HELLO", e.message.toString())
                    hourlyWeatherStateFlow.value = UIStateHourlyWeather.Error(e)
                }

                try{
                    val responseDaily = withContext(Dispatchers.IO){
                        weatherRepository.getDailyWeather(response.coord.lat, response.coord.lon, "hourly", apikey)
                    }
                    val dailyWeatherDetails = ResponseToWeatherDetailConverter().convertDaily(responseDaily)
                    dailyWeatherStateFlow.value = UIStateDailyWeather.Success(dailyWeatherDetails)
                }
                catch (e: Exception){
                    Log.e("HELLO", e.message.toString())
                    hourlyWeatherStateFlow.value = UIStateHourlyWeather.Error(e)
                }
            }
            catch(e: Exception){
                Log.e("HELLO", e.message.toString())
                currentWeatherStateFlow.value = UIStateCurrentWeather.Error(e)
            }
        }
    }

    fun getCurrentWeatherByCoordinates(lat: Double, lon: Double, apikey: String) {
        viewModelScope.launch {
            try{
                val response = withContext(Dispatchers.IO) {
                    weatherRepository.getCurrentWeatherByCoordinate(lat, lon, apikey)
                }
                val weatherDetails = ResponseToWeatherDetailConverter().convert(response)
                currentWeatherStateFlow.value = UIStateCurrentWeather.Success(weatherDetails)
                try{
                    val responseHourly = withContext(Dispatchers.IO){
                        weatherRepository.getHourlyWeather(response.coord.lat, response.coord.lon, "daily", apikey)
                    }
                    val hourlyWeatherDetails = ResponseToWeatherDetailConverter().convertHourly(responseHourly)
                    hourlyWeatherStateFlow.value = UIStateHourlyWeather.Success(hourlyWeatherDetails)
                }
                catch (e: Exception){
                    Log.e("HELLO", e.message.toString())
                    hourlyWeatherStateFlow.value = UIStateHourlyWeather.Error(e)
                }

                try{
                    val responseDaily = withContext(Dispatchers.IO){
                        weatherRepository.getDailyWeather(response.coord.lat, response.coord.lon, "hourly", apikey)
                    }
                    val dailyWeatherDetails = ResponseToWeatherDetailConverter().convertDaily(responseDaily)
                    dailyWeatherStateFlow.value = UIStateDailyWeather.Success(dailyWeatherDetails)
                }
                catch (e: Exception){
                    Log.e("HELLO", e.message.toString())
                    hourlyWeatherStateFlow.value = UIStateHourlyWeather.Error(e)
                }
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
    class Error(val e: Exception) : UIStateCurrentWeather()
    class Success(val currentWeather: WeatherDetails) : UIStateCurrentWeather()
}

sealed class UIStateDailyWeather {
    object Loading : UIStateDailyWeather()
    class Error(val e: Exception) : UIStateDailyWeather()
    class Success(val dailyWeather: MutableList<DailyWeather>) : UIStateDailyWeather()
}

sealed class UIStateHourlyWeather {
    object Loading : UIStateHourlyWeather()
    class Error(val e: Exception) : UIStateHourlyWeather()
    class Success(val hourlyWeather: MutableList<DailyWeather>) : UIStateHourlyWeather()
}




