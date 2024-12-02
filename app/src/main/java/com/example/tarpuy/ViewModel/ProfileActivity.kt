package com.example.tarpuy.ViewModel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.tarpuy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameTextView: TextView
    private lateinit var fullNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var dateOfBirthTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var editIcon: ImageView // Icono de lápiz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Mi perfil"
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a los TextViews
        usernameTextView = findViewById(R.id.usernameTextView)
        fullNameTextView = findViewById(R.id.fullNameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView)
        cityTextView = findViewById(R.id.cityTextView)
        genderTextView = findViewById(R.id.genderTextView)
        editIcon = findViewById(R.id.edit_icon) //icono de lápiz

        // Cargar datos del usuario
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        usernameTextView.text = document.getString("username") ?: ""
                        fullNameTextView.text = document.getString("fullName") ?: ""
                        emailTextView.text = document.getString("email") ?: ""
                        phoneNumberTextView.text = document.getString("phoneNumber") ?: ""
                        dateOfBirthTextView.text = document.getString("dateOfBirth") ?: ""
                        cityTextView.text = document.getString("city") ?: ""
                        genderTextView.text = document.getString("gender") ?: ""
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Listener para el icono de lápiz
        editIcon.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("username", usernameTextView.text.toString())
            intent.putExtra("fullName", fullNameTextView.text.toString())
            intent.putExtra("email", emailTextView.text.toString())
            intent.putExtra("phoneNumber", phoneNumberTextView.text.toString())
            intent.putExtra("dateOfBirth", dateOfBirthTextView.text.toString())
            intent.putExtra("city", cityTextView.text.toString())
            intent.putExtra("gender", genderTextView.text.toString())
            startActivity(intent)
        }
    }
}
