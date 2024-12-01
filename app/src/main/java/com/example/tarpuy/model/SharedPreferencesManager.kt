package com.example.tarpuy.model

import android.content.Context
import android.content.SharedPreferences

// Gestor para almacenar y recuperar preferencias de usuario usando SharedPreferences.
object SharedPreferencesManager {
    private const val PREFERENCES_NAME = "user_preferences"  // Nombre del archivo de preferencias.
    private const val KEY_SAVED_CITY = "saved_city"  // Clave para almacenar la ciudad guardada.

    // Obtiene las SharedPreferences del contexto dado.
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    // Guarda la ciudad en las SharedPreferences.
    fun saveCity(context: Context, city: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_SAVED_CITY, city)  // Guarda la ciudad con la clave definida.
            apply()  // Aplica los cambios de manera asíncrona.
        }
    }

    // Recupera la ciudad guardada desde las SharedPreferences.
    fun getSavedCity(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_SAVED_CITY, null)  // Devuelve la ciudad guardada o null si no existe.
    }
}
