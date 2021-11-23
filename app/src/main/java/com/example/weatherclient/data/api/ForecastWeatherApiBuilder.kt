package com.example.weatherclient.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ForecastWeatherApiBuilder {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private fun getRetrofit() : Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    val forecastWeatherApiService:ForecastWeatherApiService = getRetrofit().create(ForecastWeatherApiService::class.java)
}