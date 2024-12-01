package com.example.tarpuy.ViewModel

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.R
import com.example.tarpuy.model.ChatAdapter
import com.example.tarpuy.model.Message
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class ChatbotScreen : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private val chatMessages = mutableListOf<Message>()
    private lateinit var bottomNavigationView: BottomNavigationView

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_chatbot
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_alert -> {
                    startActivity(Intent(this, AlertScreen::class.java))
                    finish()
                    true
                }
                R.id.navigation_chatbot -> {
                    startActivity(Intent(this, ChatbotScreen::class.java))
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

        // Usar la propiedad de clase en lugar de una variable local
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val rootView = findViewById<FrameLayout>(android.R.id.content)

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // Si el teclado está visible, oculta el BottomNavigationView
            if (keypadHeight > screenHeight * 0.15) {
                bottomNavigationView.visibility = View.GONE
            } else {
                // Si el teclado no está visible, muestra el BottomNavigationView
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        // Inicializar vistas
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        // Configurar RecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        val chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.adapter = chatAdapter

        // Botón para enviar mensaje
        sendButton.setOnClickListener {
            val userMessage = inputMessage.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                // Agregar mensaje del usuario
                chatMessages.add(Message(userMessage, isUser = true))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)

                // Generar respuesta del chatbot
                val botResponse = generateBotResponse(userMessage)
                chatMessages.add(Message(botResponse, isUser = false))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)

                inputMessage.text.clear()
            }
        }

        setupBottomNavigation()
    }

    // Filtrar y generar respuesta del chatbot
    private fun generateBotResponse(message: String): String {
        return try {
            val response = fetchChatbotResponse(message)
            response ?: "Lo siento, no pude entender tu consulta."
        } catch (e: Exception) {
            "Ocurrió un error al procesar tu consulta: ${e.message}"
        }
    }

    private fun fetchChatbotResponse(userMessage: String): String? {
        val apiKey = "AIzaSyDqqre6FPdkc6Zgnn-9N3GNJJWZo4HfdZc" // Reemplaza con tu API Key de Gemini AI
        val url = "https://api.gemini-ai.com/v1/chat" // Endpoint ficticio, ajusta según documentación de Gemini AI
        val client = OkHttpClient()

        val jsonBody = JSONObject().apply {
            put("message", userMessage)
            put("userId", "USER_ID") // Opcional, puede ser un identificador único para el usuario
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create("application/json".toMediaType(), jsonBody.toString()))
            .build()

        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            val jsonResponse = JSONObject(response.body?.string() ?: "")
            jsonResponse.getString("response") // Ajusta según la estructura real de la API
        } else {
            "No se pudo obtener una respuesta del servidor."
        }
    }
}

