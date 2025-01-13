package com.example.onkoto

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.onkoto.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()

        // Initialize binding
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure the SupportMapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startLocationUpdates()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Request location updates
        requestLocation()
    }

    private fun requestLocation() {
        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            mMap.isMyLocationEnabled = true
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                requestLocation()
            }
        }

    private fun startLocationUpdates() {
        this.locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            LocationRequest.PRIORITY_HIGH_ACCURACY, // Priority
            10000 // Interval in milliseconds
        ).apply {
            setMinUpdateIntervalMillis(5000) // Fastest interval
            setMaxUpdates(1) // Optional: Limit to one update
        }.build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("MapsActivity", "Permissions denied")
            return
        }
        fusedLocationClient.requestLocationUpdates(
            this.locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let {
                        updateMapLocation(it)
                    }
                }
            },
            mainLooper
        )
    }

    private fun updateMapLocation(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        Log.d("MapsActivity", "Updating map with location: $userLatLng")

        try {
            mMap.addMarker(MarkerOptions().position(userLatLng).title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

            // Update TextView with coordinates
            binding.coordinatesTextView.text =
                getCompleteAddress(location.latitude.toDouble(), location.longitude.toDouble())

        } catch (e: Exception) {
            Log.e("MapsActivity", "Error updating map location", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
        }
    }

    private fun shareOnWhatsApp(locationUrl: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Minha localização atual: $locationUrl")
            setPackage("com.whatsapp") // Garante que abrirá diretamente o WhatsApp
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "O WhatsApp não está instalado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCompleteAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this)
        val addressList: List<Address>?
        val addressText = StringBuilder()

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]

                // Append address lines
                for (i in 0..address.maxAddressLineIndex) {
                    addressText.append(address.getAddressLine(i)).append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Address not available"
        }

        return addressText.toString()
    }



}