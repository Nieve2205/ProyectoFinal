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

// Actividad principal donde se muestra la pantalla de inicio.
class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)  // Establece el layout de la actividad.

        // Configuración del texto estilizado
        val registerTextView = findViewById<TextView>(R.id.registerTextView)  // Referencia al TextView del mensaje.

        val fullText = "¿Aun no tienes cuenta? Registrate"  // Texto completo que será estilizado.
        val spannableString = SpannableString(fullText)  // Crea un SpannableString para aplicar estilos.

        // Aplica color blanco a la primera parte del texto.
        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            22,  // Longitud de la primera parte
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Aplica color rojo a la segunda parte del texto ("Registrate").
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            22,
            fullText.length,  // Hasta el final
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Asigna el texto estilizado al TextView.
        registerTextView.text = spannableString

        // Navega a la actividad de registro cuando se hace clic en el TextView.
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configura el botón de login para navegar a la actividad de login.
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}