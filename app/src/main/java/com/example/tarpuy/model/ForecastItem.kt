package com.example.tarpuy.model

data class ForecastItem(
    val description: String,
    val iconResId: Int,
    val temperature: Double,
    val time: String,
    val weatherMain: String,
)

