package com.example.medscape20.presentation.screens.user.customer.trash
sealed class TrashEvents {
    data object ResetErrorMessage: TrashEvents()
    data class OnTrashTypesSet(val trashTypeList:ArrayList<String>): TrashEvents()
    data object OnCancelClicked: TrashEvents()
}