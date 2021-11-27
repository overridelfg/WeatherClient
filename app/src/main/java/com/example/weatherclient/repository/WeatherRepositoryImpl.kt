package com.example.weatherclient.repository

import com.example.weatherclient.data.api.ApiBuilder
import com.example.weatherclient.data.api.ForecastWeatherApiBuilder
import com.example.weatherclient.data.dailyResponse.DailyWeatherResponse
import com.example.weatherclient.data.hourlyResponse.HourlyWeatherResponse
import com.example.weatherclient.data.response.CurrentWeatherResponse

object WeatherRepositoryImpl : WeatherRepository{
    private val weatherService = ApiBuilder.apiService
    private val forecastWeatherService = ForecastWeatherApiBuilder.forecastWeatherApiService
    override suspend fun getCurrentWeather(location: String, apikey: String): CurrentWeatherResponse {
        return weatherService.getCurrentWeather(location, apikey)
    }

    override suspend fun getCurrentWeatherByCoordinate(
        latitude: Double,
        longitude: Double,
        apikey: String
    ): CurrentWeatherResponse {
       return weatherService.getCurrentWeatherByCoordinates(latitude, longitude, apikey)
    }

    override suspend fun getDailyWeather(
        latitude: Double,
        longitude: Double,
        timeType: String,
        apikey: String
    ): DailyWeatherResponse {
        return forecastWeatherService.getDailyWeather(latitude, longitude, timeType, apikey)
    }

    override suspend fun getHourlyWeather(
        latitude: Double,
        longitude: Double,
        timeType: String,
        apikey: String
    ): HourlyWeatherResponse {
        return forecastWeatherService.getHourlyWeather(latitude, longitude, timeType, apikey)
    }
}