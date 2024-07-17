package com.example.medscape20.presentation.screens.user.collector.customers


sealed class CustomersEvents {
    data object ResetErrorMessage: CustomersEvents()
}