package com.example.weatherclient.data.api


import com.example.weatherclient.data.response.CurrentWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apikey : String
    ) : CurrentWeatherResponse

    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apikey : String
    ) : CurrentWeatherResponse
}