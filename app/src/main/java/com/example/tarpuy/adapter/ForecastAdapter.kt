package com.example.tarpuy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.R
import com.example.tarpuy.model.ForecastItem

// Adaptador para mostrar pronósticos en un RecyclerView.
class ForecastAdapter(private val forecastList: List<ForecastItem>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    // ViewHolder que mantiene las vistas de cada ítem (texto de pronóstico e ícono).
    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val forecastText: TextView = view.findViewById(R.id.forecastText)
        val forecastIcon: ImageView = view.findViewById(R.id.forecastIcon)
    }

    // Infla el layout de cada ítem.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    // Enlaza los datos (descripción e ícono) al ViewHolder.
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = forecastList[position]
        holder.forecastText.text = item.description
        holder.forecastIcon.setImageResource(item.iconResId)
    }

    // Devuelve el número de ítems en la lista.
    override fun getItemCount() = forecastList.size
}

