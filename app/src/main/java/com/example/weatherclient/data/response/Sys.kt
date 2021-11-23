package com.example.weatherclient.data.response


import com.google.gson.annotations.SerializedName

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int
)