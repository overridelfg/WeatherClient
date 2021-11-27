package com.example.weatherclient.model

data class WeatherDetails(
    val lat: Double,
    val lon: Double,
    val currentDate: String,
    val cityName: String,
    val weatherInfo: String,
    val weatherDescription: String,
    val currentWeatherBackground: Int
)