package com.example.weatherclient.data.hourlyResponse


import com.google.gson.annotations.SerializedName

data class HourlyWeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val current: Current,
    val hourly: List<Hourly>
)