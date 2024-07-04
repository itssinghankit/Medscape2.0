package com.example.medscape20.presentation.screens.user.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HomeStates(
    val isLoading:Boolean=false,
    val isError:Boolean=false,
    @StringRes val errMessage:Int?=null,
    val name:String="Ankit"
)

@HiltViewModel
class HomeViewModel @Inject constructor():ViewModel() {

    private val _state = MutableStateFlow(HomeStates())
    val state: StateFlow<HomeStates> = _state.asStateFlow()

    fun event(){

    }

}