@file:Suppress("DEPRECATION")

package com.example.tarpuy.ViewModel

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarpuy.adapter.ForecastAdapter
import com.example.tarpuy.R
import com.example.tarpuy.model.SharedPreferencesManager
import com.example.tarpuy.adapter.WeeklyForecastAdapter
import com.example.tarpuy.model.ForecastItem
import com.example.tarpuy.model.WeeklyForecastItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale


class MainScreen : AppCompatActivity() {
    private lateinit var weatherInfo: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var forecastRecyclerView: RecyclerView
    private lateinit var dailyWeatherRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cityNameText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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

        initViews()

        setupSearchView()
        setupBottomNavigation()
        setupToolbar()
        setupRecyclerViews()

        searchView = findViewById(R.id.searchView)
        cityNameText = findViewById(R.id.cityNameText)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Configuración del SearchView
        setupSearchView()
    }

    override fun onResume() {
        super.onResume()
        SharedPreferencesManager.getSavedCity(this)?.let { savedCity ->
            fetchWeather(savedCity)
            cityNameText.text = savedCity
        } ?: run {
            Toast.makeText(this, "No se ha guardado ninguna ciudad", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUIWithCity(city: String) {
        cityNameText.text = city
        fetchWeather(city)
    }

    private fun initViews() {
        searchView = findViewById(R.id.searchView)
        weatherInfo = findViewById(R.id.weather)
        weatherIcon = findViewById(R.id.weatherIcon)
        forecastRecyclerView = findViewById(R.id.forecastRecyclerView)
        dailyWeatherRecyclerView = findViewById(R.id.dailyWeatherRecyclerView)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        cityNameText = findViewById(R.id.cityNameText)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_clima
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

    private fun setupToolbar() {
        val textView: TextView = findViewById(R.id.toolbar_title)
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Recuperar username del intent o de SharedPreferences
        val usernameFromIntent = intent.getStringExtra("username")
        val username = if (!usernameFromIntent.isNullOrEmpty()) {
            // Guardar en SharedPreferences si viene del Intent
            editor.putString("username", usernameFromIntent)
            editor.apply()
            usernameFromIntent
        } else {
            // Recuperar de SharedPreferences si el Intent no trae un valor
            sharedPreferences.getString("username", "usuario")
        }

        // Actualizar la UI con el username
        textView.text = "Bienvenido, $username"

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Bienvenido, $username"
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    private fun setupRecyclerViews() {
        forecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dailyWeatherRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    fetchWeather(query)
                } else {
                    Toast.makeText(this@MainScreen, "Ingresa el nombre de la ciudad", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                bottomNavigationView.visibility = if (newText.isNullOrEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })
    }


    private fun loadSavedCityAndWeather() {
        val savedCity = sharedPreferences.getString("saved_city", null)
        savedCity?.let { fetchWeather(it) }
    }

    private fun fetchWeather(city: String) {
        val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=e3c54ae44ea4052e60a4fa2b30ffbe8f&units=metric"
        val forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=e3c54ae44ea4052e60a4fa2b30ffbe8f&units=metric"
        FetchDataTask().execute(weatherUrl, "weather")
        FetchDataTask().execute(forecastUrl, "forecast")

        sharedPreferences.edit().putString("saved_city", city).apply()
    }

    inner class FetchDataTask : AsyncTask<String, Void, String?>() {
        private var type: String = ""

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String): String? {
            val result = StringBuilder()
            type = params[1]
            return try {
                val url = URL(params[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()

                val inputStream = urlConnection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    result.append(line).append("\n")
                }
                reader.close()
                urlConnection.disconnect()
                result.toString()
            } catch (e: Exception) {
                Log.e("FetchDataTask", "Error en la conexión", e)
                null
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(this@MainScreen, "Error obteniendo datos", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                if (type == "weather") {
                    handleWeatherResponse(result)
                } else if (type == "forecast") {
                    handleForecastResponse(result)
                }
            } catch (e: Exception) {
                Log.e("FetchDataTask", "Error procesando datos", e)
                Toast.makeText(this@MainScreen, "Error procesando datos", Toast.LENGTH_SHORT).show()
            }
        }

        private fun handleWeatherResponse(result: String) {
            val jsonObject = JSONObject(result)
            val cityName = jsonObject.getString("name")
            val main = jsonObject.getJSONObject("main")
            val temperature = main.getDouble("temp")
            val weatherArray = jsonObject.getJSONArray("weather")
            val weather = weatherArray.getJSONObject(0)
            val humidity = main.getDouble("humidity")
            val weatherMain = weather.getString("main")

            val weatherDescription = translateWeatherDescription(weather.getString("description"))

            // Mostrar el clima actual de acuerdo a la hora
            weatherInfo.text = "Descripción: $weatherDescription\nTemp: ${String.format("%.1f", temperature)} °C\nHumedad: ${humidity.toInt()}%"
            val weatherIconResId = getWeatherIcon(weatherMain)
            weatherIcon.setImageResource(weatherIconResId)

            cityNameText.text = cityName
        }

        private fun handleForecastResponse(result: String) {
            val forecastMap = mutableMapOf<String, MutableList<ForecastItem>>()
            val jsonObject = JSONObject(result)
            val list = jsonObject.getJSONArray("list")

            for (i in 0 until list.length()) {
                val day = list.getJSONObject(i)
                val main = day.getJSONObject("main")
                val temperature = main.getDouble("temp")
                val dateTime = day.getString("dt_txt")
                val date = dateTime.substring(0, 10)
                val time = dateTime.substring(11, 16)

                // Filtrar solo la hora 12:00 para el pronóstico de la tarde
                if (time == "12:00") {
                    val weatherArray = day.getJSONArray("weather")
                    val weather = weatherArray.getJSONObject(0)
                    val weatherMain = weather.getString("main")
                    val iconResId = getWeatherIcon(weatherMain)

                    val forecastItem = ForecastItem(
                        description = "Temp:\n${String.format("%.1f", temperature)} °C",
                        iconResId = iconResId,
                        temperature = temperature,
                        time = time,
                        weatherMain = weatherMain
                    )
                    forecastMap.getOrPut(date) { mutableListOf() }.add(forecastItem)
                }

                // Agregar todas las horas para el pronóstico semanal
                val weatherArray = day.getJSONArray("weather")
                val weather = weatherArray.getJSONObject(0)
                val weatherMain = weather.getString("main")
                val iconResId = getWeatherIcon(weatherMain)

                val allForecastItem = ForecastItem(
                    description = "Temp: \n${String.format("%.1f", temperature)} °C",
                    iconResId = iconResId,
                    temperature = temperature,
                    time = time,
                    weatherMain = weatherMain
                )
                forecastMap.getOrPut(date) { mutableListOf() }.add(allForecastItem)
            }

            val dailyForecasts = mutableListOf<ForecastItem>()
            for ((date, forecasts) in forecastMap) {
                if (forecasts.isNotEmpty()) {
                    // Obtener el pronóstico de la tarde (12:00)
                    val afternoonForecast = forecasts.find { it.time == "12:00" }
                    afternoonForecast?.let {
                        // Convertir la fecha
                        val formattedDate = formatDateToDayAndNumber(date)
                        dailyForecasts.add(
                            ForecastItem(
                                description = "$formattedDate\nTemp: ${String.format("%.1f", it.temperature)} °C",
                                iconResId = it.iconResId,
                                temperature = it.temperature,
                                time = "12:00",
                                weatherMain = ""
                            )
                        )
                    }
                }
            }


            // Asigna el adaptador al RecyclerView para mostrar solo el clima de la tarde
            forecastRecyclerView.adapter = ForecastAdapter(dailyForecasts)

            // Mostrar todos los pronósticos por día
            showAllWeatherByDay(forecastMap)
        }

        // Método para convertir fecha
        private fun formatDateToDayAndNumber(date: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato original
            val outputFormat = SimpleDateFormat("EEEE, d", Locale.getDefault()) // Ejemplo: "Sunday, 24"
            val parsedDate = inputFormat.parse(date) // Parsear la fecha original
            return parsedDate?.let { outputFormat.format(it) } ?: date // Si no es válida, retorna la original
        }


        private fun translateWeatherDescription(description: String): String {
            return when (description) {
                "clear sky" -> "Cielo despejado"
                "few clouds" -> "Pocas nubes"
                "scattered clouds" -> "Nubes dispersas"
                "broken clouds" -> "Nubes rotas"
                "shower rain" -> "Lluvia de ducha"
                "rain" -> "Lluvia"
                "thunderstorm" -> "Tormenta eléctrica"
                "snow" -> "Nieve"
                "mist" -> "Niebla"
                "light rain" -> "Lluvia ligera"
                "moderate rain" -> "Lluvia moderada"
                else -> description
            }
        }
    }

    fun getWeatherIcon(weatherMain: String): Int {
        return when (weatherMain) {
            "Clear" -> R.drawable.ic_clear_sky
            "Clouds" -> R.drawable.ic_cloudy
            "Rain" -> R.drawable.ic_rain
            else -> R.drawable.ic_unknown
        }
    }

    private fun showAllWeatherByDay(forecastMap: Map<String, List<ForecastItem>>) {
        val weeklyForecasts = mutableListOf<WeeklyForecastItem>()

        for (i in 1..5) {
            val date = getDateStringFromToday(i)
            val forecasts = forecastMap[date] ?: emptyList()
            val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }
            val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
            val dayNumber = SimpleDateFormat("d", Locale.getDefault()).format(calendar.time)

            // Inicializa las variables para las temperaturas y los iconos
            var morningTemp = "No disponible"
            var morningIcon = R.drawable.ic_unknown // Icono por defecto
            var afternoonTemp = "No disponible"
            var afternoonIcon = R.drawable.ic_unknown // Icono por defecto
            var eveningTemp = "No disponible"
            var eveningIcon = R.drawable.ic_unknown // Icono por defecto
            var nightTemp = "No disponible"
            var nightIcon = R.drawable.ic_unknown // Icono por defecto

            for (forecast in forecasts) {
                val time = forecast.time
                val weatherMain = forecast.weatherMain // Suponiendo que `ForecastItem` tiene un campo `weatherMain`
                when {
                    time.startsWith("06:00") -> {
                        morningTemp = forecast.description
                        morningIcon = getWeatherIcon(weatherMain) // Uso de getWeatherIcon
                    }
                    time.startsWith("12:00") -> {
                        afternoonTemp = forecast.description
                        afternoonIcon = getWeatherIcon(weatherMain) // Uso de getWeatherIcon
                    }
                    time.startsWith("18:00") -> {
                        eveningTemp = forecast.description
                        eveningIcon = getWeatherIcon(weatherMain) // Uso de getWeatherIcon
                    }
                    time.startsWith("21:00") -> {
                        nightTemp = forecast.description
                        nightIcon = getWeatherIcon(weatherMain) // Uso de getWeatherIcon
                    }
                }
            }

            // Agrega el pronóstico semanal con las temperaturas y los iconos encontrados
            weeklyForecasts.add(
                WeeklyForecastItem(
                    day = "$dayName\n$dayNumber",
                    morningTemp, morningIcon,
                    afternoonTemp, afternoonIcon,
                    eveningTemp, eveningIcon,
                    nightTemp, nightIcon
                )
            )
        }

        // Asigna el adaptador al RecyclerView para mostrar todos los climas de la semana
        dailyWeatherRecyclerView.adapter = WeeklyForecastAdapter(weeklyForecasts)
    }

    private fun getDateStringFromToday(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }
}
