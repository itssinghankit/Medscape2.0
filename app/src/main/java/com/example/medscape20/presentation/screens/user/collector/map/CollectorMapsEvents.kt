package com.example.medscape20.presentation.screens.user.collector.map

import com.google.android.gms.maps.model.LatLng


sealed class CollectorMapsEvents {
    data object ResetErrorMessage: CollectorMapsEvents()
    data class OnNewFiltersSet(val radius:Double,val showAll:Boolean): CollectorMapsEvents()
    data object FilterSettedSuccessfully:CollectorMapsEvents()
    data class OnDisposedClicked(val position:Int):CollectorMapsEvents()
    data class SaveCurrentLocation(val currLoc:LatLng):CollectorMapsEvents()
}