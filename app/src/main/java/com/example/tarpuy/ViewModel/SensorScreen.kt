package com.example.tarpuy.ViewModel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SensorScreen : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_screen) // Asocia el archivo XML con esta clase

        // Inicializar el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Configurar el ítem seleccionado para esta actividad
        bottomNavigationView.selectedItemId = R.id.navigation_sensor

        // Configurar el listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_sensor -> {
                    // Ya estás en la pantalla de SensorScreen, no es necesario iniciar esta actividad nuevamente
                    true
                }
                R.id.navigation_chatbot -> {
                    val intent = Intent(this, ChatbotActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_clima -> {
                    val intent = Intent(this, MainScreen::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
