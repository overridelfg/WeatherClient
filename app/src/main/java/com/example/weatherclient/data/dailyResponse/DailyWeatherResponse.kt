package com.example.weatherclient.data.dailyResponse


import com.google.gson.annotations.SerializedName

data class DailyWeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val current: Current,
    val daily: List<Daily>,
    val alerts: List<Alert>
)