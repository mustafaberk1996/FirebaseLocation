package com.example.firebaselocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.firebaselocation.MainActivity.Companion.MARKER_ICON
import com.example.firebaselocation.MainActivity.Companion.USER_NAME

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.firebaselocation.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.remote.WatchChange
import java.security.Permission
import java.security.Permissions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    private var userName = ""
    private var markerIcon = R.mipmap.marker1


    private val locationRequest = LocationRequest.Builder(5000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()
    private lateinit var locationCallback:LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        when {
            it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)->{
                startLocationUpdates()
            }
            it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)->{
                //course location izni alindi
            }
            else->{
                //location izni alinamadi
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object:LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                //datayi firebase e  kaydet
                locationResult.locations.forEach {
                    println("location ---> $it")
                    val userLocation = UserLocation()
                    userLocation.userName = userName
                    userLocation.lat =it.latitude
                    userLocation.long =it.longitude
                    userLocation.markerIcon = markerIcon
                    FirebaseFirestore.getInstance().collection("locations").add(userLocation)

                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestLocationPermission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        }else{
         startLocationUpdates()
        }


        listenLocations()



        userName = intent.getStringExtra(USER_NAME).orEmpty()
        markerIcon = intent.getIntExtra(MARKER_ICON, R.mipmap.marker1)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)






    }

    private fun listenLocations() {
        FirebaseFirestore.getInstance().collection("locations").addSnapshotListener { value, error ->


            println("firebase listen error $error")

            value?.documentChanges?.forEach {
                val userLocation = it.document.toObject(UserLocation::class.java)
                when(it.type){
                    DocumentChange.Type.ADDED->{
                        println("${userLocation.userName} eklendi")
                        mMap.addMarker(
                            MarkerOptions().position(LatLng(userLocation.lat, userLocation.long))
                                .title(userLocation.userName).icon(BitmapDescriptorFactory.fromResource(userLocation.markerIcon))
                        )
                    }
                    DocumentChange.Type.MODIFIED->{
                        println("${userLocation.userName} degisti")
                    }
                    DocumentChange.Type.REMOVED->{
                        println("${userLocation.userName} silindi")
                    }
                }
            }


        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}