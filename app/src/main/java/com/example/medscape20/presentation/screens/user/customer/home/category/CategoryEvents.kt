package com.example.medscape20.presentation.screens.user.customer.home.category

sealed class CategoryEvents {
    data object ResetErrorMessage: CategoryEvents()
}