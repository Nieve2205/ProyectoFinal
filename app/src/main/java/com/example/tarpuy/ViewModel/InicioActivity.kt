package com.example.tarpuy.ViewModel

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        // Configuración del texto estilizado
        val registerTextView = findViewById<TextView>(R.id.registerTextView)

        val fullText = "¿Aun no tienes cuenta? Registrate"
        val spannableString = SpannableString(fullText)

        // Color blanco para "¿Aun no tienes cuenta?"
        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            22, // Longitud de la primera parte
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Color rojo para "Registrate"
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            22,
            fullText.length, // Hasta el final
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Aplicar el texto estilizado al TextView
        registerTextView.text = spannableString

        // Navegar a la actividad de registro
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
