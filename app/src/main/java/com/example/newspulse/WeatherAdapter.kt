package com.example.newspulse.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.R
import com.example.newspulse.models.WeatherResponse

class WeatherAdapter(private val weatherList: List<WeatherResponse>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.tvCityName)
        val temp: TextView = itemView.findViewById(R.id.tvTemp)
        val desc: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.cityName.text = weather.name
        holder.temp.text = "${weather.main.temp}°C"
        holder.desc.text = weather.weather[0].description
    }

    override fun getItemCount(): Int = weatherList.size
}
