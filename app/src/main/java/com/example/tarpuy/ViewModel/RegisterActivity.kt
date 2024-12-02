package com.example.tarpuy.ViewModel

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a los EditText y el botón de registro
        val fullNameEditText = findViewById<EditText>(R.id.fullNameEditText)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // TextView para mostrar requisitos de contraseña
        val passwordRequirementsTextView = findViewById<TextView>(R.id.passwordRequirementsTextView)
        passwordRequirementsTextView.text = "*La contraseña debe tener un mínimo de 8 caracteres, incluir al menos una letra mayúscula, un número y un símbolo especial.*"

        // Configura el listener para el botón de registro
        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validaciones
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!fullName.contains(" ")) {
                Toast.makeText(this, "Ingresa tu nombre completo (al menos dos palabras)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Ingresa un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de la contraseña (mínimo 8 caracteres, mayúsculas, minúsculas, números, caracteres especiales)
            if (password.length < 8 || !password.matches(Regex(".*[A-Z].*")) ||
                !password.matches(Regex(".*[a-z].*")) || !password.matches(Regex(".*\\d.*")) ||
                !password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*"))
            ) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar si el nombre de usuario ya está registrado
            firestore.collection("users").whereEqualTo("username", username).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                    } else {
                        // Crear usuario en Firebase Authentication
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Registro exitoso
                                    val userId = auth.currentUser?.uid
                                    val user = hashMapOf("username" to username, "fullName" to fullName, "email" to email)

                                    if (userId != null) {
                                        firestore.collection("users").document(userId)
                                            .set(user)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, MapActivity::class.java)
                                                intent.putExtra("username", username)
                                                startActivity(intent)
                                                finish()  // Cierra la actividad actual para evitar que el usuario regrese
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this, "Error al guardar el usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al validar el nombre de usuario", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
