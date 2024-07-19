package com.example.medscape20.presentation.screens.user.collector.map

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
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
import androidx.fragment.app.Fragment
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
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.sqrt

@AndroidEntryPoint
class CollectorMapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentCollectorMapsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CollectorMapsViewModel by viewModels()

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
        _binding = FragmentCollectorMapsBinding.inflate(layoutInflater, container, false)

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

        //the main logic - selected option from details bottom sheet
        setFragmentResultListener(CustomerDetailBottomSheetEnum.REQUEST_KEY_DETAILS.name) { _, bundle ->
            val selectedDisposeOption =
                bundle.getBoolean(CustomerDetailBottomSheetEnum.DISPOSED_OPTION.name)
            val currentItemPosition =
                bundle.getInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name)
            val selectedLocateOption =
                bundle.getBoolean(CustomerDetailBottomSheetEnum.LOCATE_OPTION.name)

            when {
                selectedLocateOption -> openLocationInGoogleMaps(
                    viewModel.state.value.newFilteredList[currentItemPosition].lat,
                    viewModel.state.value.newFilteredList[currentItemPosition].lng
                )

                selectedDisposeOption -> viewModel.event(
                    CollectorMapsEvents.OnDisposedClicked(currentItemPosition)
                )
            }

        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Timber.d("caled on map ready")
        map = googleMap
        map.setOnMarkerClickListener(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        enableMyLocation()

        setFragmentResultListener(CollectorMapsEnum.REQUEST_KEY.name) { _, bundle ->
            val radius = bundle.getDouble(CollectorMapsEnum.RADIUS.name)
            val showAll = bundle.getBoolean(CollectorMapsEnum.SHOW_ALL.name)
            viewModel.event(CollectorMapsEvents.OnNewFiltersSet(radius, showAll))
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    //if we removed any user by clicking on dumping button clear markers to re mark all markers
                    if (state.setFilter) {
                        if (::map.isInitialized) {
                            map.clear()  // Clear existing markers
                        }
                        binding.progressCircular.visibility = View.VISIBLE
                        addAllMarkers(viewModel.state.value.newFilteredList)
                    }
                    when (state.isLoading) {
                        true -> binding.progressCircular.visibility = View.VISIBLE
                        false -> binding.progressCircular.visibility = View.GONE
                    }

                }
            }
        }

    }

    private fun addAllMarkers(newFilteredList: List<CustomersResDto>) {

        val customIcon = getIconFromResource(R.drawable.ic_trash)
        val builder = LatLngBounds.Builder()

        newFilteredList.forEach { customer ->
            val position = LatLng(customer.lat, customer.lng)
            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(customer.name)
                    .icon(customIcon)
            )?.tag = customer.uid

            builder.include(position)
        }

        if (viewModel.filters.showAll) {
            if (newFilteredList.isNotEmpty()) {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
            } else {
                setMapViewForRadius(viewModel.filters.currentLoc!!, 0.5)
            }

        } else {
            setMapViewForRadius(viewModel.filters.currentLoc!!, viewModel.filters.radius)
        }

        //i am doing this so that i can apply new filter whenever available by changing setFilter state to true again
        viewModel.event(CollectorMapsEvents.FilterSettedSuccessfully)


    }

    override fun onMarkerClick(marker: Marker): Boolean {

        //show bottom sheet containing details of customer
        val customer =
            viewModel.state.value.newFilteredList.find { it.uid == marker.tag as? String }
                ?.takeIf { it.uid == marker.tag } ?: return false

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

    private fun openLocationInGoogleMaps(latitude: Double, longitude: Double) {
        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        runCatching {
            mapLauncher.launch(mapIntent)
        }.onFailure {
            //if google maps app is not installed
            val browserUri = Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")
            mapLauncher.launch(Intent(Intent.ACTION_VIEW, browserUri))
        }
    }

    //checks permission, if granted ask for turning on location toggle else request permission
    private fun enableMyLocation() {
        if (requireContext().isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
            requireContext().isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            turnOnLocationToggle()
            return
        }

        //permission not granted ask for permission
        requestPermissionLauncher.launch(PERMISSIONS_ARRAY)
    }

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

            //to prevent moving again and again with new loc
            var runOneTime = true

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    for (location in locationResult.locations) {
                        if (runOneTime) {
                            // Updating UI with location data
                            proceedWithCurrentLocation(location)
                            runOneTime = false
                        }
                        Timber.d("location updated")
                        //updating curr location in viewmodel
                        viewModel.event(
                            CollectorMapsEvents.SaveCurrentLocation(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        )

                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            return
        }

    }

    fun proceedWithCurrentLocation(location: Location) {

        binding.progressCircular.visibility = View.GONE

        val currentLoc = LatLng(location.latitude, location.longitude)
        viewModel.event(CollectorMapsEvents.SaveCurrentLocation(currentLoc))

        viewModel.getAllDumpingPeople()

        binding.myLocBtn.setOnClickListener {
            setMapViewForRadius(viewModel.filters.currentLoc!!, 0.5)
        }

        //filter bottom sheet
        binding.filterBtn.setOnClickListener {
            val bottomSheet = CollectorMapFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble(CollectorMapsEnum.RADIUS.name, viewModel.filters.radius)
                    putBoolean(CollectorMapsEnum.SHOW_ALL.name, viewModel.filters.showAll)
                }
            }
            bottomSheet.show(parentFragmentManager, "filter_customer_map")
        }

    }

    private fun setMapViewForRadius(center: LatLng, radiusInKm: Double) {

        // Create a LatLngBounds that includes the area within the radius
        val distanceFromCenterToBounds = radiusInKm * 1000 * sqrt(2.0)
        val southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToBounds, 225.0)
        val northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToBounds, 45.0)
        val bounds = LatLngBounds(southwestCorner, northeastCorner)

        // Move the camera
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
        binding.progressCircular.visibility = View.GONE
    }

    private fun getIconFromResource(drawable: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), drawable)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        val customIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
        return customIcon
    }

    //extension function for checking permission is granted or not
    private fun Context.isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    override fun onResume() {
        //after result launcher result is processed this is invoked and also if it goes to background
        super.onResume()

        if (permissionDenied) {

            // Permission was not granted, display error dialog.
            showPermissionRationaleDialog()
            permissionDenied = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        val PERMISSIONS_ARRAY = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

}

enum class CollectorMapsEnum {
    REQUEST_KEY,
    ARGUMENT_KEY,
    RADIUS,
    SHOW_ALL
}