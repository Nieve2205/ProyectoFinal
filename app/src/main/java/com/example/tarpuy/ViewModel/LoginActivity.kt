package com.example.tarpuy.ViewModel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Actividad para manejar el inicio de sesión de un usuario con Firebase.
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth  // Instancia de autenticación de Firebase.
    private lateinit var firestore: FirebaseFirestore  // Instancia de Firestore para acceder a la base de datos.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)  // Establece el layout de la actividad.

        auth = FirebaseAuth.getInstance()  // Obtiene la instancia de FirebaseAuth.
        firestore = FirebaseFirestore.getInstance()  // Obtiene la instancia de FirebaseFirestore.

        // Referencias a las vistas del layout.
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)

        // Configuración del botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()  // Obtiene el email ingresado.
            val password = passwordEditText.text.toString()  // Obtiene la contraseña ingresada.

            // Verifica que ambos campos no estén vacíos.
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)  // Intenta iniciar sesión con Firebase.
                    .addOnCompleteListener(this) { task ->  // Maneja el resultado de la tarea de inicio de sesión.
                        if (task.isSuccessful) {
                            // Inicio de sesión exitoso
                            val userId = auth.currentUser?.uid  // Obtiene el ID del usuario autenticado.
                            if (userId != null) {
                                // Recuperar el nombre de usuario desde Firestore.
                                firestore.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        val username = document.getString("username") ?: "usuario"  // Obtiene el nombre de usuario.
                                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainScreen::class.java)
                                        intent.putExtra("username", username)  // Pasa el nombre de usuario a la siguiente actividad.
                                        startActivity(intent)
                                        finish()  // Opcional: cierra la actividad de inicio de sesión.
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al recuperar el nombre de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()  // Error en el inicio de sesión.
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()  // Si faltan campos.
            }
        }

        // Configura el enlace de "Olvidaste la contraseña?"
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)  // Navega a la actividad de recuperación de contraseña.
            startActivity(intent)
        }
    }
}

