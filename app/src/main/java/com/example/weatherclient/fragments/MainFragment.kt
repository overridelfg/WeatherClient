package com.example.weatherclient.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherclient.model.DailyWeather
import com.example.weatherclient.R
import com.example.weatherclient.adapters.RecyclerViewAdapter
import com.example.weatherclient.adapters.RecyclerViewDailyAdapter
import com.example.weatherclient.data.api.*
import com.example.weatherclient.data.dailyResponse.DailyWeatherResponse
import com.example.weatherclient.data.hourlyResponse.HourlyWeatherResponse
import com.example.weatherclient.data.response.CurrentWeatherResponse
import com.example.weatherclient.databinding.FragmentMainBinding
import com.example.weatherclient.viewmodel.UIStateCurrentWeather
import com.example.weatherclient.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private var appid: String = "4bfeb4f08be3f2aa289378c8a1dd4b3f"

    private lateinit var thisContext: Context
    private lateinit var findCityET: EditText
    private lateinit var findCityButton: ImageButton
    private lateinit var weatherInfo: TextView
    private lateinit var weatherStatus: ImageView
    private lateinit var weatherViewModel : WeatherViewModel
    private lateinit var cityNameTextView: TextView
    private lateinit var currentDate: TextView
    private lateinit var test: TextView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewDaily: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var layoutManagerDaily: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var adapterDaily: RecyclerViewDailyAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocationButton : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            thisContext = container.context
        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findCityET = view.findViewById(R.id.editTextSearch)
        findCityButton = view.findViewById(R.id.buttonSend)
        weatherInfo = view.findViewById(R.id.weatherView)
        weatherStatus = view.findViewById(R.id.weatherStatus)
        cityNameTextView = view.findViewById(R.id.cityNameTextView)
        currentDate = view.findViewById(R.id.currentDate)
        currentLocationButton = view.findViewById(R.id.getCurrentLocationButton)
        weatherDescriptionTextView = view.findViewById(R.id.weatherDescriptionTextView)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel:: class.java)
        weatherViewModel.currentWeatherStateFlowPublic.onEach {
            when(it){
                is UIStateCurrentWeather.Loading -> currentDate.text = "loading"
                is UIStateCurrentWeather.Success -> {
                    currentDate.text = it.currentWeather.currentDate
                    cityNameTextView.text = it.currentWeather.cityName
                    weatherInfo.text = it.currentWeather.weatherInfo
                    weatherDescriptionTextView.text = it.currentWeather.weatherDescription

                }
                else -> Toast.makeText(thisContext, "exception", Toast.LENGTH_LONG).show()
            }
        }.launchIn(lifecycleScope)
        layoutManager = LinearLayoutManager(thisContext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerViewAdapter(thisContext)
        recyclerView.adapter = adapter

        layoutManagerDaily = LinearLayoutManager(thisContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewDaily = view.findViewById(R.id.recyclerViewDaily)
        recyclerViewDaily.layoutManager = layoutManagerDaily
        adapterDaily = RecyclerViewDailyAdapter(thisContext)
        recyclerViewDaily.adapter = adapterDaily

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(thisContext)


        currentLocationButton.setOnClickListener{
            fetchLocation()
        }

        findCityButton.setOnClickListener{
            val city = findCityET.text.toString()
            weatherViewModel.getWeatherDetails(city, appid)
        }

    }

    private fun fetchLocation() {
        val taskLastLocation = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(thisContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 101)
            }
            return
        }
        taskLastLocation.addOnSuccessListener {
            if(it != null){
                Toast.makeText(thisContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_LONG).show()
                //getWeatherDetailsByCoordinates(it.latitude, it.longitude)
            }
        }
    }

//    private fun getWeatherDetails(city: String){
//        weatherService = ApiBuilder.apiService
//        val call = weatherService.getCurrentWeather(city, appid)
//
//        call.enqueue(object: Callback<CurrentWeatherResponse>{
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(
//                call: Call<CurrentWeatherResponse>,
//                response: Response<CurrentWeatherResponse>
//            ) {
//                if(response.isSuccessful) {
//                    val latitude = response.body()!!.coord.lat
//                    val longitude = response.body()!!.coord.lon
//                    currentDate.text = fromUnixToDateHour(response.body()!!.dt.toLong())
//                    cityNameTextView.text = city +", " + response.body()!!.sys.country
//                    weatherInfo.text = df.format(response.body()!!.main.temp - 273.15) + "℃"
//                    weatherDescriptionTextView.text =
//                        "Feels like " + df.format(response.body()!!.main.feelsLike - 273.15) + "℃\n" + response.body()!!.weather[0].description.replaceFirstChar {
//                            if (it.isLowerCase()) it.titlecase(
//                                Locale.getDefault()
//                            ) else it.toString()
//                        } + "\nMax " + df.format(response.body()!!.main.tempMax - 273.15) + "℃" +
//                                ", min " + df.format(response.body()!!.main.tempMin - 273.15) + "℃"
//                    typeOfWeatherUI(response.body()!!.weather[0].main)
//                    getHourlyWeatherForecast(latitude, longitude)
//                    getDailyWeatherForecast(latitude, longitude)
//                }
//                else{
//                    Toast.makeText(thisContext, "$city doesn't exist, enter another city!", Toast.LENGTH_LONG).show()
//                }
//            }
//            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
//                Toast.makeText(thisContext, t.message, Toast.LENGTH_LONG).show()
//            }
//        })
//
//    }
//
//    private fun getDailyWeatherForecast(latitude: Double, longitude: Double){
//        forecastWeatherService = ForecastWeatherApiBuilder.forecastWeatherApiService
//        val call = forecastWeatherService.getDailyWeather(latitude, longitude, "hourly", appid)
//        call.enqueue(object: Callback<DailyWeatherResponse>{
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(
//                call: Call<DailyWeatherResponse>,
//                response: Response<DailyWeatherResponse>
//            ) {
//                if(response.isSuccessful) {
//                    adapterDaily.data.clear()
//                    for(i in 0 until 8){
//                        adapterDaily.data.add(DailyWeather(response.body()!!.daily[i].temp.day - 273.15, typeOfDailyWeatherUI(response.body()!!.daily[i].weather[0].main), fromUnixToDate(response.body()!!.daily[i].dt.toLong())))
//                    }
//                    adapterDaily.notifyDataSetChanged()
//                }
//                else{
//                    weatherInfo.text = "Not Found"
//                }
//            }
//
//            override fun onFailure(call: Call<DailyWeatherResponse>, t: Throwable) {
//                Toast.makeText(thisContext, t.message, Toast.LENGTH_LONG).show()
//            }
//
//        })
//    }
//
//    private fun getHourlyWeatherForecast(latitude: Double, longitude: Double){
//        forecastWeatherService = ForecastWeatherApiBuilder.forecastWeatherApiService
//        val call = forecastWeatherService.getHourlyWeather(latitude, longitude, "daily", appid)
//        call.enqueue(object: Callback<HourlyWeatherResponse>{
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(
//                call: Call<HourlyWeatherResponse>,
//                response: Response<HourlyWeatherResponse>
//            ) {
//                if(response.isSuccessful) {
//                    adapter.data.clear()
//                    for(i in 0 until 25){
//                        adapter.data.add(DailyWeather(response.body()!!.hourly[i].temp - 273.15, typeOfDailyWeatherUI(response.body()!!.hourly[i].weather[0].main), fromUnixToDateHourly(response.body()!!.hourly[i].dt.toLong())))
//                    }
//                    adapter.notifyDataSetChanged()
//                }
//                else{
//                    weatherInfo.text = "Not Found"
//                }
//            }
//
//            override fun onFailure(call: Call<HourlyWeatherResponse>, t: Throwable) {
//                Log.w("MyTag", "requestFailed", t);
//            }
//
//        })
//    }
//
//    private fun getWeatherDetailsByCoordinates(lat: Double, lon: Double){
//        weatherService = ApiBuilder.apiService
//        val call = weatherService.getCurrentWeatherByCoordinates(lat, lon, appid)
//
//        call.enqueue(object: Callback<CurrentWeatherResponse>{
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(
//                call: Call<CurrentWeatherResponse>,
//                response: Response<CurrentWeatherResponse>
//            ) {
//                if(response.isSuccessful) {
//                    val latitude = response.body()!!.coord.lat
//                    val longitude = response.body()!!.coord.lon
//                    currentDate.text = fromUnixToDateHour(response.body()!!.dt.toLong())
//                    cityNameTextView.text = response.body()!!.name +", " + response.body()!!.sys.country
//                    weatherInfo.text = df.format(response.body()!!.main.temp - 273.15) + "℃"
//                    weatherDescriptionTextView.text =
//                        "Feels like " + df.format(response.body()!!.main.feelsLike - 273.15) + "℃\n" + response.body()!!.weather[0].description.replaceFirstChar {
//                            if (it.isLowerCase()) it.titlecase(
//                                Locale.getDefault()
//                            ) else it.toString()
//                        } + "\nMax " + df.format(response.body()!!.main.tempMax - 273.15) + "℃" +
//                                ", min " + df.format(response.body()!!.main.tempMin - 273.15) + "℃"
//                    typeOfWeatherUI(response.body()!!.weather[0].main)
//                    getHourlyWeatherForecast(latitude, longitude)
//                    getDailyWeatherForecast(latitude, longitude)
//                }
//                else{
//                    Toast.makeText(thisContext, "${response.body()!!.name} doesn't exist, enter another city!", Toast.LENGTH_LONG).show()
//                }
//            }
//            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
//                Toast.makeText(thisContext, t.message, Toast.LENGTH_LONG).show()
//            }
//        })
//
//    }

    private fun typeOfWeatherUI(type: String){
        when (type) {
            "Clouds" -> {
                weatherStatus.background =
                    ContextCompat.getDrawable(thisContext, R.drawable.clouds)
            }
            "Haze" -> {
                weatherStatus.background =
                    ContextCompat.getDrawable(thisContext, R.drawable.haze)
            }
            "Rain" -> {
                weatherStatus.background =
                    ContextCompat.getDrawable(thisContext, R.drawable.rain)
            }
            "Clear" -> {
                weatherStatus.background =
                    ContextCompat.getDrawable(thisContext, R.drawable.sun)
            }
            "Mist" -> {
                weatherStatus.background =
                    ContextCompat.getDrawable(thisContext, R.drawable.mist)
            }
            "Snow" -> {
                weatherStatus.background =
                    ContextCompat.getDrawable(thisContext, R.drawable.snow)
            }
            else -> weatherStatus.background =
                ContextCompat.getDrawable(thisContext, R.drawable.ic_launcher_background)
        }
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


    @SuppressLint("SimpleDateFormat")
    private fun fromUnixToDateHour(unix: Long): String{
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

}