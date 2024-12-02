package com.example.tarpuy.ViewModel


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a los EditTexts
        usernameEditText = findViewById(R.id.usernameEditText)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText)
        cityEditText = findViewById(R.id.cityEditText)
        genderEditText = findViewById(R.id.genderEditText)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        backIcon = findViewById(R.id.back_icon)

        // Obtener datos enviados desde ProfileActivity
        usernameEditText.setText(intent.getStringExtra("username"))
        fullNameEditText.setText(intent.getStringExtra("fullName"))
        emailEditText.setText(intent.getStringExtra("email"))
        phoneNumberEditText.setText(intent.getStringExtra("phoneNumber"))
        dateOfBirthEditText.setText(intent.getStringExtra("dateOfBirth"))
        cityEditText.setText(intent.getStringExtra("city"))
        genderEditText.setText(intent.getStringExtra("gender"))

        // Listener para el icono de flecha (back)
        backIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Guardar cambios
        saveChangesButton.setOnClickListener {
            val updatedUser = hashMapOf(
                "username" to usernameEditText.text.toString(),
                "fullName" to fullNameEditText.text.toString(),
                "email" to emailEditText.text.toString(),
                "phoneNumber" to phoneNumberEditText.text.toString(),
                "dateOfBirth" to dateOfBirthEditText.text.toString(),
                "city" to cityEditText.text.toString(),
                "gender" to genderEditText.text.toString()
            )

            val userId = auth.currentUser?.uid
            userId?.let {
                firestore.collection("users").document(it)
                    .set(updatedUser)  // Sobrescribir el documento con los datos actualizados
                    .addOnSuccessListener {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        finish() // Regresar a la actividad anterior
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

        }
    }
}
