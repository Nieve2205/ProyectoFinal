package com.example.tarpuy.ViewModel

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.R
import com.example.tarpuy.model.ChatAdapter
import com.example.tarpuy.model.GeminiApiService
import com.example.tarpuy.model.Parts
import com.example.tarpuy.model.RequestModel
import com.example.tarpuy.model.Text
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatbotActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatAdapter: ChatAdapter // Asegúrate de tener un adaptador para el RecyclerView
    private lateinit var apiService: GeminiApiService // Declara apiService aquí
    private lateinit var bottomNavigationView: BottomNavigationView

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_chatbot
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_chatbot -> {
                    startActivity(Intent(this, ChatbotActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_sensor -> {
                    startActivity(Intent(this, SensorScreen::class.java))
                    finish()
                    true
                }
                R.id.navigation_clima -> {
                    startActivity(Intent(this, MainScreen::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot_screen)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter()
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                inputMessage.text.clear()
            }
        }

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GeminiApiService::class.java) // Inicializa apiService aquí

        setupBottomNavigation()
    }

    private fun sendMessage(message: String) {
        chatAdapter.addMessage("Tú: $message")

        CoroutineScope(Dispatchers.IO).launch {
            val response = getResponseFromGeminiAI(message)

            withContext(Dispatchers.Main) {
                chatAdapter.addMessage("Chatbot: $response")
            }
        }
    }

    private suspend fun getResponseFromGeminiAI(message: String): String {
        val prompt = "Responde solo a saludos y temas de agricultura y clima."
        val fullMessage = "$prompt\nUsuario: $message"

        val request = RequestModel(contents = listOf(Parts(listOf(Text(fullMessage))))) // Usa fullMessage aquí

        return try {
            val response = apiService.generateContent("AIzaSyDqqre6FPdkc6Zgnn-9N3GNJJWZo4HfdZc", request) // Asegúrate de que tu API key sea válida
            response.contents.first().parts.first().text
        } catch (e: Exception) {
            "Lo siento, no puedo responder a eso."
        }
    }
}
