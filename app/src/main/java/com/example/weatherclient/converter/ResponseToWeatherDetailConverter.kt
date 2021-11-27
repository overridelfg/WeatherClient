package com.example.weatherclient.converter

import android.annotation.SuppressLint
import com.example.weatherclient.data.response.CurrentWeatherResponse
import com.example.weatherclient.model.WeatherDetails
import java.text.DecimalFormat
import java.util.*

class ResponseToWeatherDetailConverter {
    private var df: DecimalFormat = DecimalFormat("#.#")
    fun convert(response: CurrentWeatherResponse): WeatherDetails{
        return WeatherDetails(
            lat = response.coord.lat,
            lon = response.coord.lon,
            currentDate = fromUnixToDateHour(response.dt.toLong()),
            cityName = response.name + ", " + response.sys.country,
            weatherInfo = df.format(response.main.temp - 273.15) + "℃",
            weatherDescription = "Feels like " + df.format(response.main.feelsLike - 273.15)
                    + "℃\n" + response.weather[0].description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            } + "\nMax " + df.format(response.main.tempMax - 273.15) + "℃" +
                    ", min " + df.format(response.main.tempMin - 273.15) + "℃"
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun fromUnixToDate(unix: Long): String {
        val sdf = java.text.SimpleDateFormat("EEE, dd MMM")
        val date = Date(unix * 1000)
        return sdf.format(date).toString()
    }

    @SuppressLint("SimpleDateFormat")
    private fun fromUnixToDateHour(unix: Long): String {
        val sdf = java.text.SimpleDateFormat("dd MMM, HH:mm")
        val date = Date(unix * 1000)
        return sdf.format(date).toString()
    }
}