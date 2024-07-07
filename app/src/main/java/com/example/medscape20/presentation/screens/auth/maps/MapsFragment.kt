package com.example.medscape20.presentation.screens.auth.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationCallback: LocationCallback

    private lateinit var progressBar: ProgressBar
    private lateinit var addTxtFld: EditText
    private lateinit var myLocBtn: FloatingActionButton
    private lateinit var doneBtn: FloatingActionButton

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var state: String? = null
    private var city: String? = null
    private var locality: String? = null

    //initially permission is not granted
    private var permissionDenied = false

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

    //start permission activity and then again enable location layer for current location
    private val startActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        enableMyLocation()
    }

    private val resolutionForLocationToggleResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    // User agreed to enable location
                    //but it will take a small time to find actual location using GPS
                    startLocationUpdates()
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
        progressBar = layout.findViewById(R.id.progress_circular)
        addTxtFld = layout.findViewById(R.id.add_txt_fld)
        myLocBtn = layout.findViewById(R.id.my_loc_btn)
        doneBtn = layout.findViewById(R.id.done_btn)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    // Update UI with location data
                    animateCameraToCurrLoc(location)
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        enableMyLocation()

    }

    /* this function checks if the user has granted the location permission,
       if not, it requests the permission
       if permission granted location layer will be turned on by map.isMyLocationEnabled = true */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        Timber.d("enableMyLocation")

        //Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            //turn on location toggle
            turnOnLocationToggle()
            return
        }

        //request permission if not granted - else part
        requestPermissionLauncher.launch(PERMISSIONS_ARRAY)
    }

    //turn on location toggle
    private fun turnOnLocationToggle() {

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(500)
            .build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location is already on
            startLocationUpdates()
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

    override fun onResume() {
        //after result launcher result is processed this callback is invoked and also if it goes to background
        super.onResume()

        if (permissionDenied) {

            // Permission was not granted, display error dialog.
            showPermissionRationaleDialog()
            permissionDenied = false
        }
    }

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
            .setNeutralButton("App Settings") { _, _ ->
                //navigate to app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    //to tell it that we want details of this package i.e our app
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivityResultLauncher.launch(intent)

            }.create().show()
    }

    private fun startLocationUpdates() {

        progressBar.visibility = View.VISIBLE

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            return
        }

    }

    fun animateCameraToCurrLoc(location: Location) {
        //for removing again and again location updates
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        progressBar.visibility = View.GONE

        val currentLoc = LatLng(location.latitude, location.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18f))

        myLocBtn.setOnClickListener {
            //setting the camera again to live location but there is a problem if we move
            //the location will get same as previous one also the last location is not updated
            //until another app or this app update its location manually
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18f))
            startLocationUpdates()
        }

        var currentTargetLat = location.latitude
        var currentTargetLng = location.longitude

        // Set up camera move listener
        map.setOnCameraIdleListener {
            //until we haven't found live location map remain in this state that is the reason
            //we need not to call getAddressFromLocation(center) for fetching address in text field.
            val center = map.cameraPosition.target
            currentTargetLat = center.latitude
            currentTargetLng = center.longitude

            getAddressFromLocation(center)
        }

        doneBtn.setOnClickListener {

            //send address back to signup details page
            val resultKey = MapData.RESULT_KEY.value
            val resultBundle = Bundle().apply {
                putString(MapData.ADDRESS.value, addTxtFld.text.toString())
                putString(MapData.STATE.value, state?.lowercase(Locale.getDefault()))
                putString(MapData.CITY.value, city?.lowercase(Locale.getDefault()))
                putString(MapData.LOCALITY.value, locality?.lowercase(Locale.getDefault()))
                putDouble(MapData.LATITUDE.value, currentTargetLat)
                putDouble(MapData.LONGITUDE.value, currentTargetLng)
            }
            setFragmentResult(resultKey, resultBundle)

            findNavController().navigateUp()
        }
    }

    private fun getAddressFromLocation(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    state = address.adminArea
                    city = address.locality
                    locality = address.subLocality
                    val addressText =
                        address.getAddressLine(0) ?: getString(R.string.error_address_not_found)

                    withContext(Dispatchers.Main) {
                        addTxtFld.setText(addressText)
                    }
                }
            } catch (e: Exception) {
                Timber.d(e.toString())
                withContext(Dispatchers.Main) {
                    addTxtFld.setText(getString(R.string.error_could_not_get_address))
                }
            }
        }
    }

    companion object {
        val PERMISSIONS_ARRAY = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

}

enum class MapData(val value: String) {
    RESULT_KEY("maps"),
    ADDRESS("address"),
    STATE("state"),
    CITY("city"),
    LOCALITY("locality"),
    LATITUDE("lat"),
    LONGITUDE("lng")
}