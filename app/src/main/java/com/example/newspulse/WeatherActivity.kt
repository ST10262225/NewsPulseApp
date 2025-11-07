package com.example.newspulse

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.R
import com.example.newspulse.adapters.WeatherAdapter
import com.example.newspulse.models.WeatherResponse
import com.example.newspulse.api.WeatherApiService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class WeatherActivity : BaseActivity() { // ✅ Extend BaseActivity

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvHeading: TextView
    private lateinit var btnBack: Button
    private val weatherList = mutableListOf<WeatherResponse>()
    private lateinit var adapter: WeatherAdapter
    private val apiKey = "286f8b6c5f68a5d787f12c89d4502949" // OpenWeatherMap API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // Initialize views
        tvHeading = findViewById(R.id.tvHeading)
        btnBack = findViewById(R.id.btnBack)

        // Set localized texts
        tvHeading.text = getString(R.string.weather_heading)
        btnBack.text = getString(R.string.back)

        // Back button: go back to previous screen
        btnBack.setOnClickListener { finish() }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerWeather)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = WeatherAdapter(weatherList)
        recyclerView.adapter = adapter

        val cities = listOf("Johannesburg", "Cape Town", "Durban", "Pretoria")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApiService::class.java)

        cities.forEach { city ->
            api.getWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            weatherList.add(it)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(this@WeatherActivity, "Failed for $city", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Toast.makeText(this@WeatherActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
