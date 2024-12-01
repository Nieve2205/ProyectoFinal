package com.example.tarpuy.model

data class DailyForecastItem(
    val date: String,
    val forecasts: List<ForecastItem>
)

data class WeeklyForecastItem(
    val day: String,
    val morningTemp: String,
    val morningIcon: Int, // Cambiar a Int
    val afternoonTemp: String,
    val afternoonIcon: Int, // Cambiar a Int
    val eveningTemp: String,
    val eveningIcon: Int, // Cambiar a Int
    val nightTemp: String,
    val nightIcon: Int // Cambiar a Int
)

