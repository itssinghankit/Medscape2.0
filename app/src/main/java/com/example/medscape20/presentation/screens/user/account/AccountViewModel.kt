package com.example.medscape20.presentation.screens.user.account

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AccountStates(
    val isLoading:Boolean=false,
    val isError:Boolean=false,
    @StringRes val errMessage:Int?=null,
    val navigateToAuth:Boolean=false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
):ViewModel(){

    private val _state = MutableStateFlow(AccountStates())
    val state:StateFlow<AccountStates> =_state.asStateFlow()

    fun event(action:AccountEvents){
        when(action){
            AccountEvents.OnSignOutClicked -> {
                firebaseAuth.signOut()
                _state.update {
                    it.copy(
                        navigateToAuth = true
                    )
                }
            }
        }
    }


}