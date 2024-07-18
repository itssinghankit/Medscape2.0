package com.example.medscape20.presentation.screens.user.collector.map


sealed class CollectorMapsEvents {
    data object ResetErrorMessage: CollectorMapsEvents()
    data class OnNewFiltersSet(val newFilters:ArrayList<String>): CollectorMapsEvents()
    data object GetAllDumpingPeople: CollectorMapsEvents()
    data class OnDisposedClicked(val position:Int):CollectorMapsEvents()
}