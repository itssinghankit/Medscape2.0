package com.example.medscape20.presentation.screens.user.collector.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.user.collector.home.CustomerHomeGetDumpingPeopleCountUseCase
import com.example.medscape20.domain.usecase.user.collector.home.CustomerHomeGetUserDataUseCase
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

data class CollectorHomeStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val city: String? = null,
    val state: String? = null,
    val name: String? = null,
    val avatar: String? = null,
    val totalCount:Int?=null,
    val stateCount:Int?=null,
    val cityCount:Int?=null
)

@HiltViewModel
class CollectorHomeViewModel @Inject constructor(
    private val customerHomeGetUserDataUseCase: CustomerHomeGetUserDataUseCase,
    private val customerHomeGetDumpingPeopleCountUseCase: CustomerHomeGetDumpingPeopleCountUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CollectorHomeStates())
    val state: StateFlow<CollectorHomeStates> = _state.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                getUserId()
            }
        }
    }


    fun event(action: CollectorHomeEvents) {

        when (action) {
            CollectorHomeEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }

            CollectorHomeEvents.OnLogOutClicked ->{
                firebaseAuth.signOut()
            }
        }

    }

    private fun getUserId() {
        val uid = firebaseAuth.currentUser?.uid!!
        viewModelScope.launch(Dispatchers.IO) {

            customerHomeGetUserDataUseCase(uid).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
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
                                            isError = true,
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
                                it.copy(isLoading = true)
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    state = result.data.state,
                                    city = result.data.city,
                                    name = result.data.name,
                                    avatar = result.data.avatar
                                )
                            }
                        }
                        getPeopleCount()
                    }
                }
            }

        }
    }

    private fun getPeopleCount() {

        viewModelScope.launch(Dispatchers.IO) {
            customerHomeGetDumpingPeopleCountUseCase(
                state.value.city!!,
                state.value.state!!
            ).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
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
                                            isError = true,
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
                                it.copy(isLoading = true)
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = false,
                                    totalCount = result.data.totalCount,
                                    stateCount = result.data.stateCount,
                                    cityCount = result.data.cityCount
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}