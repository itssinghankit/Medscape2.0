package com.example.medscape20.presentation.screens.user.customer.account

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.customer.home.HomeGetUserDataResDto
import com.example.medscape20.domain.usecase.user.customer.account.AccountGetUserDataUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AccountStates(
    val isLoading: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val navigateToAuth: Boolean = false,
    val userDetails: HomeGetUserDataResDto? = null,
    val name: String = "",
    val email: String = "",
    val avatar: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val getUserDataUseCase: AccountGetUserDataUseCase
) : ViewModel() {

    init {
        firebaseAuth.addAuthStateListener { auth ->
            if (auth.currentUser != null) getUserDetails()
        }
    }

    private val _state = MutableStateFlow(AccountStates())
    val state: StateFlow<AccountStates> = _state.asStateFlow()

    fun event(action: AccountEvents) {
        when (action) {
            AccountEvents.OnSignOutClicked -> {
                firebaseAuth.signOut()
                _state.update {
                    it.copy(
                        navigateToAuth = true
                    )
                }
            }

            is AccountEvents.OnAvatarUpdation -> {
                val updatedDetails = state.value.userDetails?.copy(avatar = action.url)
                _state.update {
                    it.copy(userDetails = updatedDetails, avatar = action.url)
                }
            }

            is AccountEvents.OnDetailsUpdation -> {
                val updatedDetails = state.value.userDetails?.copy(
                    name = action.data.name,
                )
                _state.update {
                    it.copy(
                        userDetails = updatedDetails,
                        name = action.data.name,
                    )
                }
            }

            AccountEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(errMessage = null)
                }
            }
        }
    }

    private fun getUserDetails() {
        val uid = firebaseAuth.currentUser!!.uid
        viewModelScope.launch(Dispatchers.IO) {

            getUserDataUseCase(uid).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {

                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            errMessage = R.string.error_internal_server,
                                            isLoading = false
                                        )
                                    }

                                }
                            }

                            else -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            errMessage = R.string.error_unknown,
                                            isLoading = false
                                        )
                                    }

                                }

                            }
                        }
                    }

                    ApiResult.Loading -> {
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    userDetails = result.data,
                                    isLoading = false,
                                    name = result.data.name ?: "",
                                    email = result.data.email ?: "",
                                    avatar = result.data.avatar ?: "",
                                )
                            }

                        }
                    }
                }

            }
        }
    }


}