package com.example.weatherclient.utils

sealed class ResponseWithStatus<out T> {
    data class OnSuccessResponse<T> (val value: T) : ResponseWithStatus<T>()
    data class OnErrorResponse(
        val isNetworkFailure: Boolean,
        val code: Int?,
        val body: String?
    ) : ResponseWithStatus<Nothing>()
}