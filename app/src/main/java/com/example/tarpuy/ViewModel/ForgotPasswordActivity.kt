package com.example.tarpuy.ViewModel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            // Validar que el campo de correo no esté vacío
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviar el correo para restablecer la contraseña
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Correo enviado para restablecer la contraseña", Toast.LENGTH_SHORT).show()
                        finish()  // Cierra la actividad después de enviar el correo
                    } else {
                        Toast.makeText(this, "Error al enviar el correo: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}