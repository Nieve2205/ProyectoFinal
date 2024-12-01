package com.example.tarpuy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.R
import com.example.tarpuy.model.ForecastItem

class ForecastAdapter(private val forecastList: List<ForecastItem>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val forecastText: TextView = view.findViewById(R.id.forecastText)
        val forecastIcon: ImageView = view.findViewById(R.id.forecastIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = forecastList[position]
        holder.forecastText.text = item.description
        holder.forecastIcon.setImageResource(item.iconResId)
    }

    override fun getItemCount() = forecastList.size
}
