package com.example.partyapp.fragment
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.partyapp.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class MapScreenFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var pref: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private var locationUpdateState = false


    private lateinit var lastLocation: Location

    private lateinit var myMarker : Marker

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        db = FirebaseFirestore.getInstance()
        pref = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                lastLocation = p0!!.lastLocation

                if(pref.getString("email", "")!! != "")
                {
                    db.collection("users").document(pref.getString("email", "")!!).update("latitude", lastLocation.latitude)
                    db.collection("users").document(pref.getString("email", "")!!).update("longitude", lastLocation.longitude)

                    if(myMarker != null)
                        myMarker.position = LatLng(lastLocation.latitude, lastLocation.longitude)

                    if(mMap != null)
                        getUsers(mMap)
                }

            }
        }

        createLocationRequest()
    }

    private fun getUsers(mMap: GoogleMap)
    {
        mMap.clear()

        db.collection("users").get().addOnSuccessListener { result ->
            for(document in result){
                if(document.data["latitude"] != null && document.data["longitude"] != null)
                {
                    val location = LatLng(document.data["latitude"].toString().toDouble(),  document.data["longitude"].toString().toDouble() )

                    mMap.addMarker(MarkerOptions().position(location).title(document.data["email"].toString()))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener {
                location ->
            if(location != null)
            {
                lastLocation = location

                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                myMarker = mMap.addMarker(MarkerOptions().position(currentLatLng).title("me"))
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 15000
        locationRequest.fastestInterval = 15000

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireContext())
        val tesk = client.checkLocationSettings(builder.build())

        tesk.addOnSuccessListener {
            locationUpdateState = true

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
}