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
import kotlin.apply
import kotlin.collections.isNotEmpty

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationCallback: LocationCallback

    private lateinit var progressBar: ProgressBar
    private lateinit var addTxtFld: EditText
    private lateinit var myLocBtn: FloatingActionButton
    private lateinit var doneBtn: FloatingActionButton

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
        progressBar = layout.findViewById<ProgressBar>(R.id.progress_circular)
        addTxtFld = layout.findViewById<EditText>(R.id.add_txt_fld)
        myLocBtn = layout.findViewById<FloatingActionButton>(R.id.my_loc_btn)
        doneBtn = layout.findViewById<FloatingActionButton>(R.id.done_btn)
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

    // 1
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        enableMyLocation()

    }

    // 2 4-a
    /** this function checks if the user has granted the location permission,
     * if not, it requests the permission
     * if permission granted location layer will be turned on by map.isMyLocationEnabled = true **/
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
                    Timber.d("turnOnLocationToggle-faillure-try")
                    // Create an IntentSenderRequest
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    // Launch the resolution with our ActivityResultLauncher
                    resolutionForLocationToggleResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("turnOnLocationToggle-faillure-catch")
                    // Ignore the error
                }
            }
        }
    }

    private fun getAddressFromLocation(latLng: LatLng) {

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0) ?: "Address not found"

                    withContext(Dispatchers.Main) {
                        addTxtFld.setText(addressText)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addTxtFld.setText("Could not get address")
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

        // Set up camera move listener
        map.setOnCameraIdleListener {
            //until we haven't found live location map remain in this state that is the reason
            //we need not to call getAddressFromLocation(center) for fetching address in text field.
            val center = map.cameraPosition.target
            getAddressFromLocation(center)
        }

        doneBtn.setOnClickListener {

            //send address back to signup details page
            val resultKey = "maps"
            val resultBundle = Bundle().apply {
                putString("address", addTxtFld.text.toString())
            }
            setFragmentResult(resultKey, resultBundle)

            findNavController().navigateUp()
        }
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
}