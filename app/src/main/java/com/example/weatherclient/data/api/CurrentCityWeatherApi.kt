package com.example.weatherclient.data.api

class CurrentCityWeatherApi(private val apiService: ApiService) {
    suspend fun getCurrentWeather(location: String, appid: String) = apiService.getCurrentWeather(location, appid)
}