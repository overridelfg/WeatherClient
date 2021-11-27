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
import com.example.weatherclient.viewmodel.UIStateDailyWeather
import com.example.weatherclient.viewmodel.UIStateHourlyWeather
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
    private var appid: String = "4bfeb4f08be3f2aa289378c8a1dd4b3f"

    private lateinit var thisContext: Context
    private lateinit var findCityET: EditText
    private lateinit var findCityButton: ImageButton
    private lateinit var weatherInfo: TextView
    private lateinit var weatherStatus: ImageView
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var cityNameTextView: TextView
    private lateinit var currentDate: TextView
    private lateinit var weatherDescriptionTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewDaily: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var layoutManagerDaily: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var adapterDaily: RecyclerViewDailyAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocationButton: Button


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
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.currentWeatherStateFlowPublic.onEach {
            when (it) {
                is UIStateCurrentWeather.Loading -> currentDate.text = "loading"
                is UIStateCurrentWeather.Success -> {
                    currentDate.text = it.currentWeather.currentDate
                    cityNameTextView.text = it.currentWeather.cityName
                    weatherInfo.text = it.currentWeather.weatherInfo
                    weatherDescriptionTextView.text = it.currentWeather.weatherDescription
                    weatherStatus.setImageResource(it.currentWeather.currentWeatherBackground)
                }
                else -> Toast.makeText(thisContext, "exception", Toast.LENGTH_LONG).show()
            }
        }.launchIn(lifecycleScope)
        weatherViewModel.hourlyWeatherStateFlowPublic.onEach {
            when (it) {
                is UIStateHourlyWeather.Success -> {
                    adapter.data.clear()
                    for (i in 0 until 25) {
                        adapter.data.add(
                            it.hourlyWeather[i]
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
                else -> Toast.makeText(thisContext, "exception", Toast.LENGTH_LONG).show()
            }
        }.launchIn(lifecycleScope)

        weatherViewModel.dailyWeatherStateFlowPublic.onEach {
            when (it) {
                is UIStateDailyWeather.Success -> {
                    adapterDaily.data.clear()
                    for (i in 0 until 8) {
                        adapterDaily.data.add(
                            it.dailyWeather[i]
                        )
                    }
                    adapterDaily.notifyDataSetChanged()
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


        currentLocationButton.setOnClickListener {
            fetchLocation()
        }

        findCityButton.setOnClickListener {
            val city = findCityET.text.toString()
            weatherViewModel.getWeatherDetails(city, appid)
        }

    }

    private fun fetchLocation() {
        val taskLastLocation = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                thisContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                thisContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 101
                )
            }
            return
        }
        taskLastLocation.addOnSuccessListener {
            if (it != null) {
                Toast.makeText(thisContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_LONG)
                    .show()
                weatherViewModel.getCurrentWeatherByCoordinates(it.latitude, it.longitude, appid)
            }
        }
    }

}