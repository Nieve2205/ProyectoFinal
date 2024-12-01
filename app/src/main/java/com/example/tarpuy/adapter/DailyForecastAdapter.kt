package com.example.tarpuy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.R
import com.example.tarpuy.model.DailyForecastItem

class DailyForecastAdapter(private val dailyForecasts: List<DailyForecastItem>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    class DailyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return DailyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dailyForecast = dailyForecasts[position]
        holder.dateTextView.text = dailyForecast.date

        // Mostrar la temperatura promedio de los pronósticos del día
        val averageTemperature = dailyForecast.forecasts.map { it.temperature }.average()
        holder.temperatureTextView.text = "Temp: ${String.format("%.1f", averageTemperature)} °C"

        // Mostrar el ícono del clima (puedes personalizar esto según tus necesidades)
        holder.weatherIcon.setImageResource(dailyForecast.forecasts.first().iconResId)
    }

    override fun getItemCount(): Int = dailyForecasts.size
}

