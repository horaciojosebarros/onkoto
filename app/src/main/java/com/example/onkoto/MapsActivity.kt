package com.example.onkoto

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.onkoto.model.UserDto
import com.example.onkoto.service.UserService
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.StrictMode

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.onkoto.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/* root class */
class MapsActivity : AppCompatActivity() , OnMapReadyCallback  {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obter o fragmento do mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?

        // Verificar se o fragmento foi encontrado e configurar o callback
        mapFragment?.getMapAsync(this)

        // Inicializa o cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        enableEdgeToEdge()

        setContentView(R.layout.activity_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editTextView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button: Button = findViewById(R.id.buttonSubmit)

        button.setOnClickListener {
            buttonAction()
        }
    }

    private fun buttonAction() {
        val loginEditText = findViewById<EditText>(R.id.editTextLogin).text.toString()
        val nameEditText = findViewById<EditText>(R.id.editTextName).text.toString()

        val userService = UserService()
        val userDto = UserDto(id = "", login = loginEditText.toString(), name = nameEditText.toString())
        Log.w("Name:",loginEditText.toString())
        userService.sendUser(userDto)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Solicitar localização atual
        requestLocation()
    }

    private fun requestLocation() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            locationPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            requestPermissionLauncher.launch(locationPermission)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    updateMapLocation(it)
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                requestLocation()
            }
        }

    private fun updateMapLocation(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(userLatLng).title("Localização Atual"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

        // Exibe as coordenadas
        binding.coordinatesTextView.text = getString(R.string.coordinates_format, location.latitude, location.longitude)

    }
}