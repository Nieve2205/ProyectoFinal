package com.example.tarpuy.model

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREFERENCES_NAME = "user_preferences"
    private const val KEY_SAVED_CITY = "saved_city"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveCity(context: Context, city: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_SAVED_CITY, city)
            apply()
        }
    }

    fun getSavedCity(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_SAVED_CITY, null)
    }
}