package com.example.mobileteamproj

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission


class InputLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var toolbar: Toolbar
    private lateinit var locationText: TextView
    private lateinit var setLocationButton: Button
    private lateinit var myLocationButton: ImageButton

    private var address: String? = null
        set(value) {
            field = value
            locationText.text = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestPermission()

        initView()

        initListener()

        initMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.geocoder = Geocoder(this)
        this.googleMap = googleMap.apply {
            uiSettings.isMyLocationButtonEnabled = false
            moveCamera(CameraUpdateFactory.zoomTo(15f))
            moveCamera(CameraUpdateFactory.newLatLng(SEOUL_LOCATION))
        }

        this.googleMap.setOnCameraIdleListener {
            val target = this.googleMap.cameraPosition.target
            val location = geocoder.getFromLocation(target.latitude, target.longitude, 1)
            location.firstOrNull()?.let { address ->
                this.address = address.getAddressLine(0)
            }
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        locationText = findViewById(R.id.location_text)
        setLocationButton = findViewById(R.id.set_location_button)
        myLocationButton = findViewById(R.id.my_location_button)
    }

    private fun initListener() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        setLocationButton.setOnClickListener {
            val location = googleMap.cameraPosition.target
            val latitude = location.latitude.toString()
            val longitude = location.longitude.toString()

            val intent = Intent().apply {
                putExtra(LATITUDE_KEY, latitude)
                putExtra(LONGITUDE_KEY, longitude)
                putExtra(ADDRESS_KEY, address)
            }

            setResult(RESULT_OK, intent)
            finish()
        }

        myLocationButton.setOnClickListener {
            requestPermission()
        }
    }

    private fun requestPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionGranted() {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener {
                            val latLng = LatLng(it.latitude, it.longitude)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        }
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                }
            })
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .check()
    }

    companion object {
        const val LATITUDE_KEY = "LATITUDE_KEY"
        const val LONGITUDE_KEY = "LONGITUDE_KEY"
        const val ADDRESS_KEY = "ADDRESS_KEY"

        var SEOUL_LOCATION = LatLng(37.5666805, 126.9784147)
    }
}