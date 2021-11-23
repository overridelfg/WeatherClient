package com.example.weatherclient.data.api


import com.example.weatherclient.data.response.CurrentWeaherResponse
import com.example.weatherclient.data.response.Weather
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("weather")
    fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apikey : String
    ) : Call<CurrentWeaherResponse>

    @GET("weather")
    fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apikey : String
    ) : Call<CurrentWeaherResponse>
}