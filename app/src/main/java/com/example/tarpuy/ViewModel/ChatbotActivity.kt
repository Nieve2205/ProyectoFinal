package com.example.tarpuy.ViewModel

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tarpuy.R
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.adapter.MessageAdapter
import com.example.tarpuy.model.Message
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class ChatbotActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var bottomNavigationView: BottomNavigationView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot_screen)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        editText = findViewById(R.id.edit_text)
        button = findViewById(R.id.button)
        recyclerView = findViewById(R.id.recycler_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        adapter = MessageAdapter(messages)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            val userInput = editText.text.toString()
            if (userInput.isNotEmpty()) {
                addMessage(userInput)
                sendMessageToGemini(userInput)
                editText.text.clear()
            }
        }
        setupKeyboardListener()

        // Configurar el listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_sensor -> {
                    val intent = Intent(this, SensorScreen::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_chatbot -> {
                    val intent = Intent(this, ChatbotActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_clima -> {
                    // Aquí puedes agregar la lógica para la pantalla del clima
                    val intent = Intent(this, MainScreen::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun setupKeyboardListener() {
        val rootView = findViewById<View>(R.id.linearLayout)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val visibleHeight = rect.height()
            bottomNavigationView.visibility = if (screenHeight - visibleHeight > screenHeight * 0.15) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun addMessage(text: String) {
        messages.add(Message(text))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun sendMessageToGemini(message: String) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyDqqre6FPdkc6Zgnn-9N3GNJJWZo4HfdZc"
        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", message)
                        })
                    })
                })
            })
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    addMessage("Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            addMessage("Error: ${it.message}")
                        }
                    } else {
                        val responseData = it.body?.string()
                        val jsonResponse = JSONObject(responseData)

                        val generatedText = jsonResponse.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")

                        runOnUiThread {
                            addMessage(generatedText)
                        }
                    }
                }
            }
        })
    }
}