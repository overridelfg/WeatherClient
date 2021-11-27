package com.example.weatherclient.data.api
import com.example.weatherclient.data.dailyResponse.DailyWeatherResponse
import com.example.weatherclient.data.hourlyResponse.HourlyWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=hourly&appid=4bfeb4f08be3f2aa289378c8a1dd4b3f
interface ForecastWeatherApiService {
    @GET("onecall")
    suspend fun getDailyWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") timeType : String,
        @Query("appid") apikey : String
    ) : DailyWeatherResponse

    @GET("onecall")
    suspend fun getHourlyWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") timeType : String,
        @Query("appid") apikey : String
    ) : HourlyWeatherResponse
}