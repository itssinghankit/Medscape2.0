package com.example.medscape20

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapsFragment : Fragment(), OnMyLocationButtonClickListener, OnMyLocationClickListener,
    OnMapReadyCallback {

    private lateinit var progressBar: ProgressBar

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    //initially permission is not granted
    private var permissionDenied = false

    //3
    //requesting permission using Activity Result API
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            //check if permission is granted
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true || it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // Permission is granted. Enable the location layer
                enableMyLocation()
            } else {
                // Permission was denied. Display an error message
                // Display the missing permission error dialog when the fragments resume.
                // onPause -> Launcher result -> onResume
                //onResume will be called for further
                permissionDenied = true
            }
        }

    //6-a
    //start permission activity and then again enable location layer for current location
    private val startActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        enableMyLocation()
    }

    //
    private val resolutionForLocationToggleResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {

                    // User agreed to enable location
                    //but it will take a small time to find actual location using GPS
                    getCurrentLocation()
                }

                Activity.RESULT_CANCELED -> {
                    // User chose not to enable location
                    showPermissionRationaleDialog()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_maps, container, false)
        progressBar = layout.findViewById<ProgressBar>(R.id.progress_circular)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    // 1
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener {
            changeMarkerPosition(it)
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val leftPadding = 0 // Padding from the left edge
        val topPadding = 100 // Padding from the top edge
        val rightPadding = 32 // Padding from the right edge (shifts button to the left)
        val bottomPadding = 0 // Padding from the bottom edge (shifts button upwards)
        googleMap.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()

    }

    // 2 4-a
    /** this function checks if the user has granted the location permission,
     * if not, it requests the permission
     * if permission granted location layer will be turned on by map.isMyLocationEnabled = true **/
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        //Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //enable my location layer
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            //turn on location toggle
            turnOnLocationToggle()

            return
        }

        //request permission if not granted - else part
        requestPermissionLauncher.launch(PERMISSIONS_ARRAY)
    }

    //    4-b
    override fun onResume() {
        //after result launcher result is processed this callback is invoked and also if it goes to background
        super.onResume()

        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showPermissionRationaleDialog()
            permissionDenied = false
        }
    }

    // 5
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_dialog_title))
            .setMessage(
                getString(R.string.permission_request_deny_message)
            )
            .setPositiveButton("OK") { _, _ ->
                //request the permission again
                requestPermissionLauncher.launch(PERMISSIONS_ARRAY)

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                //navigate back to previous screen as permission is not granted
                findNavController().navigateUp()
            }
            .setNeutralButton("App Settings") { dialog, _ ->
                //navigate to app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    //to tell it that we want details of this package i.e our app
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivityResultLauncher.launch(intent)

            }.create().show()
    }

    override fun onMyLocationButtonClick(): Boolean {
//        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT)
//            .show()
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            //request permission
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//        map.setMyLocationEnabled(true)

        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    //turn on location toggle
    private fun turnOnLocationToggle() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location is already on
            getCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location is not on, but this can be fixed by showing the user a dialog
                try {
                    // Create an IntentSenderRequest
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    // Launch the resolution with our ActivityResultLauncher
                    resolutionForLocationToggleResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error
                }
            }
        }
    }

    // 6-b
    fun getCurrentLocation(attempts: Int = 30) {
        progressBar.visibility = View.VISIBLE
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(PERMISSIONS_ARRAY)
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    progressBar.visibility = View.GONE

                    val currentLoc = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(currentLoc).title("My Location"))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18f))

                } else if (attempts > 0) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        getCurrentLocation(attempts - 1)
                    }, 1000)

                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Turn on your location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
            }

    }

    fun changeMarkerPosition(it: LatLng) {
        map.clear()
        val markerOptions = MarkerOptions().position(it).title("New Location")
        map.addMarker(markerOptions)
    }

    companion object {
        val PERMISSIONS_ARRAY = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

}

