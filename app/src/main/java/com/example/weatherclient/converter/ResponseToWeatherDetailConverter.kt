package com.example.weatherclient.converter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.example.weatherclient.R
import com.example.weatherclient.data.dailyResponse.DailyWeatherResponse
import com.example.weatherclient.data.hourlyResponse.HourlyWeatherResponse
import com.example.weatherclient.data.response.CurrentWeatherResponse
import com.example.weatherclient.model.DailyWeather
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
                    ", min " + df.format(response.main.tempMin - 273.15) + "℃",
            currentWeatherBackground = typeOfDailyWeatherUI(response.weather[0].main)

        )
    }

    fun convertHourly(response: HourlyWeatherResponse): MutableList<DailyWeather>{
        val listOfHourlyWeather = mutableListOf<DailyWeather>()
        for(i in 0 until 25){
            listOfHourlyWeather.add(DailyWeather(response.hourly[i].temp - 273.15, typeOfDailyWeatherUI(response.hourly[i].weather[0].main), fromUnixToDateHourly(response.hourly[i].dt.toLong())))
        }
        return listOfHourlyWeather
    }

    fun convertDaily(response: DailyWeatherResponse): MutableList<DailyWeather>{
        val listOfDailyWeather = mutableListOf<DailyWeather>()
        for(i in 0 until 8){
            listOfDailyWeather.add(DailyWeather(response.daily[i].temp.day - 273.15, typeOfDailyWeatherUI(response.daily[i].weather[0].main), fromUnixToDate(response.daily[i].dt.toLong())))
        }
        return listOfDailyWeather
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

    @SuppressLint("SimpleDateFormat")
    private fun fromUnixToDateHourly(unix: Long): String{
        val sdf = java.text.SimpleDateFormat("HH:mm")
        val date = Date(unix * 1000)
        return sdf.format(date).toString()
    }

    private fun typeOfDailyWeatherUI(type: String) : Int{
        when (type) {
            "Clouds" -> {
                return R.drawable.clouds
            }
            "Haze" -> {
                return R.drawable.haze
            }
            "Rain" -> {
                return R.drawable.rain
            }
            "Clear" -> {
                return R.drawable.sun
            }
            "Mist" -> {
                return R.drawable.mist
            }
            "Snow" -> {
                return R.drawable.snow
            }
            else -> return R.drawable.ic_launcher_background
        }
    }



}