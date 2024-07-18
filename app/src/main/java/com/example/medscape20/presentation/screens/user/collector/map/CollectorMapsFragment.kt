package com.example.medscape20.presentation.screens.user.collector.map

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.databinding.FragmentCollectorMapsBinding
import com.example.medscape20.presentation.screens.auth.maps.MapsFragment
import com.example.medscape20.presentation.screens.user.collector.common.CustomerDetailBottomSheet
import com.example.medscape20.presentation.screens.user.collector.customers.CustomerDetailBottomSheetEnum
import com.example.medscape20.presentation.screens.user.collector.customers.CustomersEvents
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CollectorMapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding:FragmentCollectorMapsBinding?=null
    private val binding get() = _binding!!
    private val viewModel:CollectorMapsViewModel by viewModels()

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    //initially permission is not granted
    private var permissionDenied = false

    //for opening location on google maps
    private lateinit var mapLauncher: ActivityResultLauncher<Intent>

    //requesting permission using Activity Result API
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            //check if permission is granted or not
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

    //start permission activity and then again enable location layer for current location
    private val startActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        enableMyLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding= FragmentCollectorMapsBinding.inflate(layoutInflater, container, false)

        mapLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                //do nothing as we just have to show user on map
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    // Updating UI with location data
                    animateCameraToCurrLoc(location)
                }
            }
        }

        //the main logic
        //selected option form details bottom sheet
        setFragmentResultListener(CustomerDetailBottomSheetEnum.REQUEST_KEY_DETAILS.name) { _, bundle ->
            val selectedDisposeOption =
                bundle.getBoolean(CustomerDetailBottomSheetEnum.DISPOSED_OPTION.name)
            val currentItemPosition =
                bundle.getInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name)
            val selectedLocateOption =
                bundle.getBoolean(CustomerDetailBottomSheetEnum.LOCATE_OPTION.name)

            if (selectedLocateOption) {
                openLocationInGoogleMaps(
                    viewModel.state.value.newFilteredList[currentItemPosition].lat,
                    viewModel.state.value.newFilteredList[currentItemPosition].lng
                )
            }
            if (selectedDisposeOption) viewModel.event(
                CollectorMapsEvents.OnDisposedClicked(
                    currentItemPosition
                )
            )

        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.state.collect{state->
                    if(state.newFilteredList.isNotEmpty()){
                        addAllMarkers(state.newFilteredList)
                    }
                }
            }
        }



    }

    private fun addAllMarkers(newFilteredList: List<CustomersResDto>) {
        for (customer in newFilteredList) {
            val markerOptions = MarkerOptions()
                .position(LatLng(customer.lat, customer.lng))
                .title(customer.name)
            val marker = map.addMarker(markerOptions)
            marker?.tag = customer.uid  // Storing user ID in the marker's tag for later retrieval
        }

        //moving camera to show all markers
        val builder = LatLngBounds.Builder()
        for (customer in newFilteredList) {
            builder.include(LatLng(customer.lat, customer.lng))
        }
        val bounds = builder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val userId = marker.tag as? String ?: return false
        val customer = viewModel.state.value.newFilteredList.find { it.uid == userId } ?: return false
        val position = viewModel.state.value.newFilteredList.indexOf(customer)
        val bottomSheet = CustomerDetailBottomSheet().apply {
            arguments = Bundle().apply {
                putSerializable(CustomerDetailBottomSheetEnum.ARGUMENT_KEY_DETAILS.name, customer)
                putInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name, position)
            }
        }
        bottomSheet.show(parentFragmentManager, "detail")

        return true
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        enableMyLocation()
    }

    private fun openLocationInGoogleMaps(latitude: Double, longitude: Double) {

        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            mapLauncher.launch(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Google Maps app is not installed, open in browser instead
            val browserUri = Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            mapLauncher.launch(browserIntent)
        }
    }

    /* this function checks if the user has granted the location permission,
       if not, it requests the permission
       if permission granted location layer will be turned on by map.isMyLocationEnabled = true */
    private fun enableMyLocation() {

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

    private fun showPermissionRationaleDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_dialog_title))
            .setMessage(
                getString(R.string.permission_request_deny_message)
            )
            .setPositiveButton("OK") { _, _ ->
                //request the permission again
                requestPermissionLauncher.launch(MapsFragment.PERMISSIONS_ARRAY)

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

        binding.progressCircular.visibility = View.VISIBLE

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //showing the blue dot of my location
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false

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

        binding.progressCircular.visibility = View.GONE

        val currentLoc = LatLng(location.latitude, location.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18f))

        binding.myLocBtn.setOnClickListener {
            //setting the camera again to live location but there is a problem if we move
            //the location will get same as previous one also the last location is not updated
            //until another app or this app update its location manually
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 18f))
            startLocationUpdates()
        }

        //main logic starts here
        viewModel.event(CollectorMapsEvents.GetAllDumpingPeople)

        var currentTargetLat = location.latitude
        var currentTargetLng = location.longitude



    }

    override fun onResume() {
        //after result launcher result is processed this is invoked and also if it goes to background
        super.onResume()

        if (permissionDenied) {

            // Permission was not granted, display error dialog.
            showPermissionRationaleDialog()
            permissionDenied = false
        }
    }

    companion object {
        val PERMISSIONS_ARRAY = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }



}