package com.example.tarpuy.ViewModel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tarpuy.R
import com.example.tarpuy.model.SharedPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import java.io.IOException
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: SearchView
    private lateinit var acceptButton: Button

    private fun saveCity(city: String) {
        SharedPreferencesManager.saveCity(this, city)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val username = intent.getStringExtra("username")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView = findViewById(R.id.searchView)
        acceptButton = findViewById(R.id.acceptButton)

        // Solicitar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Listener para el SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty() && ::map.isInitialized) {
                    searchLocation(query) // Buscar ubicación
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Listener para el botón Aceptar
        acceptButton.setOnClickListener {
            // Redirigir a la pantalla principal
            val intent = Intent(this, MainScreen::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Habilitar la ubicación en el mapa si se tienen permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }

        // Configurar el listener para el clic en el mapa
        map.setOnMapClickListener { latLng ->
            val cityName = getCityNameFromLocation(latLng)
            saveSelectedCity(cityName)
            // Limpiar marcadores anteriores
            map.clear()
            // Agregar un marcador en la ubicación clicada
            map.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
            // Mover la cámara a la ubicación clicada
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // Guardar la dirección
            saveLocation(latLng)
        }
    }

    private fun saveSelectedCity(cityName: String) {
        SharedPreferencesManager.saveCity(this, cityName)
    }

    private fun getCityNameFromLocation(latLng: LatLng): String {
        // Método para obtener el nombre de la ciudad desde las coordenadas
        // Aquí puedes utilizar geocodificación o una API para obtener el nombre de la ciudad
        return "Ciudad seleccionada"  // Ejemplo
    }

    private fun saveLocation(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: "Ciudad desconocida"
                SharedPreferencesManager.saveCity(this, city)
                Toast.makeText(this, "Ciudad guardada: $city", Toast.LENGTH_SHORT).show()
                fetchWeather(city) // Llamar a la función para obtener el clima
                Log.d("MapActivity", "Ciudad seleccionada: $city")
                val fullAddress = address.getAddressLine(0) // Obtener la dirección completa
                Toast.makeText(this, "Dirección guardada: $fullAddress", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al obtener la dirección: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(location, 1)!!

            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)

                // Mover la cámara a la ubicación
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                // Agregar un marcador en la ubicación
                map.addMarker(MarkerOptions().position(latLng).title(location))

                // Guardar el nombre de la ciudad en Firebase
                saveCityToFirebase(location)

            } else {
                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al buscar la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCityToFirebase(city: String) {
        val database = FirebaseDatabase.getInstance()
        val userReference = database.getReference("users")

        // Obtener el userId (si estás usando autenticación)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"

        // Guardar la ciudad en Firebase
        userReference.child(userId).child("city").setValue(city)
            .addOnSuccessListener {
                Log.d("Firebase", "Ciudad guardada exitosamente")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error al guardar la ciudad: ${exception.message}")
            }
    }



    private fun fetchWeather(city: String) {
        // Aquí se define la URL de la API del clima
        val apiKey = "TU_API_KEY" // Reemplaza con tu clave API
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"

        // Usar OkHttp para hacer la solicitud
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MapActivity", "Error en la solicitud de clima: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    // Aquí puedes procesar la respuesta del clima
                    Log.d("MapActivity", "Datos del clima: $responseData")
                } else {
                    Log.e("MapActivity", "Error en la respuesta: ${response.message}")
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, habilita la ubicación
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.isMyLocationEnabled = true
                }
            } else {
                // Maneja el caso en que el permiso no fue otorgado
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
