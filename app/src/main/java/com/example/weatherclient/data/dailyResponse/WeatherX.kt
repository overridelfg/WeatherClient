package com.example.weatherclient.data.dailyResponse


import com.google.gson.annotations.SerializedName

data class WeatherX(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)