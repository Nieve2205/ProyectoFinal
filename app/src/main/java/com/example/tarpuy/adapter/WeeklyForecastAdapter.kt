package com.example.tarpuy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import com.example.tarpuy.R
import com.example.tarpuy.model.WeeklyForecastItem

// Adaptador para mostrar pronósticos semanales en un RecyclerView, con celdas en una tabla.
class WeeklyForecastAdapter(private val weeklyForecasts: List<WeeklyForecastItem>) :
    RecyclerView.Adapter<WeeklyForecastAdapter.WeeklyForecastViewHolder>() {

    // ViewHolder que contiene un GridLayout para mostrar los datos del pronóstico semanal.
    class WeeklyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gridLayout: GridLayout = itemView.findViewById(R.id.gridLayout)
    }

    // Infla el layout del ítem y crea el ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekly_forecast, parent, false)
        return WeeklyForecastViewHolder(view)
    }

    // Enlaza los datos de pronóstico semanal al GridLayout.
    override fun onBindViewHolder(holder: WeeklyForecastViewHolder, position: Int) {
        val forecast = weeklyForecasts[position]

        // Limpiar celdas anteriores del GridLayout.
        holder.gridLayout.removeAllViews()

        // Crear y agregar celdas de texto con los datos del pronóstico diario.
        holder.gridLayout.addView(createTextView(forecast.day, holder.itemView.context))
        holder.gridLayout.addView(createTextView(forecast.morningTemp, holder.itemView.context))
        holder.gridLayout.addView(createTextView(forecast.afternoonTemp, holder.itemView.context))
        holder.gridLayout.addView(createTextView(forecast.eveningTemp, holder.itemView.context))
        holder.gridLayout.addView(createTextView(forecast.nightTemp, holder.itemView.context))
    }

    // Crea un TextView con los parámetros adecuados para cada celda del GridLayout.
    private fun createTextView(text: String, context: Context): TextView {
        return TextView(context).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            this.text = text
            this.gravity = Gravity.CENTER
            this.setPadding(8, 8, 8, 8)
        }
    }

    // Devuelve el número total de ítems (pronósticos semanales).
    override fun getItemCount(): Int = weeklyForecasts.size
}
