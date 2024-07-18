package com.example.medscape20.presentation.screens.user.collector.customers


sealed class CustomersEvents {
    data object ResetErrorMessage: CustomersEvents()
    data class OnNewFiltersSet(val newFilters:ArrayList<String>): CustomersEvents()
    data class SetCollectorCityState(val city:String, val state:String): CustomersEvents()
    data class OnDisposedClicked(val position:Int):CustomersEvents()
    data object OnLocateClicked:CustomersEvents()
}