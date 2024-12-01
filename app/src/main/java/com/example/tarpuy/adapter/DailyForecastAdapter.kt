package com.example.tarpuy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.R
import com.example.tarpuy.model.DailyForecastItem

// Adaptador para mostrar pronósticos diarios en un RecyclerView.
class DailyForecastAdapter(private val dailyForecasts: List<DailyForecastItem>) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    // ViewHolder que mantiene las vistas para cada ítem (fecha, temperatura, ícono).
    class DailyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
    }

    // Infla el layout de cada ítem.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return DailyForecastViewHolder(view)
    }

    // Enlaza los datos (fecha, temperatura promedio, ícono) al ViewHolder.
    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dailyForecast = dailyForecasts[position]
        holder.dateTextView.text = dailyForecast.date
        // Calcula la temperatura promedio y la muestra.
        val averageTemperature = dailyForecast.forecasts.map { it.temperature }.average()
        holder.temperatureTextView.text = "Temp: ${String.format("%.1f", averageTemperature)} °C"
        // Muestra el ícono del clima.
        holder.weatherIcon.setImageResource(dailyForecast.forecasts.first().iconResId)
    }

    // Devuelve el número de ítems en la lista.
    override fun getItemCount(): Int = dailyForecasts.size
}


