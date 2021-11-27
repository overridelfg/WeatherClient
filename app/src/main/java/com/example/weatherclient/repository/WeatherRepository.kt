package com.example.weatherclient.repository

import com.example.weatherclient.data.dailyResponse.DailyWeatherResponse
import com.example.weatherclient.data.hourlyResponse.HourlyWeatherResponse
import com.example.weatherclient.data.response.CurrentWeatherResponse

interface WeatherRepository {

    suspend fun getCurrentWeather(location: String, apikey : String) : CurrentWeatherResponse

    suspend fun getCurrentWeatherByCoordinate(latitude: Double, longitude: Double, apikey : String) : CurrentWeatherResponse

    suspend fun getDailyWeather(latitude: Double, longitude: Double, timeType : String, apikey : String) : DailyWeatherResponse

    suspend fun getHourlyWeather(latitude: Double, longitude: Double, timeType : String, apikey : String) : HourlyWeatherResponse

}