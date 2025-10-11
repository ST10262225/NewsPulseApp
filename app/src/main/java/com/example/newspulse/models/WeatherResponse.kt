package com.example.newspulse.models

// weather response class
data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>
)
//main class
data class Main(
    val temp: Float,
    val humidity: Int
)
//weather class
data class Weather(
    val description: String,
    val icon: String
)
