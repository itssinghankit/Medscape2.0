package com.example.medscape20.domain.usecase.user.collector.customers

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.presentation.screens.user.collector.customers.CustomersEvents
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CustomersStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val data: List<CustomersResDto> = emptyList()
)

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customersGetDumpingPeopleUseCase: CustomersGetDumpingPeopleUseCase
) : ViewModel() {

    init {
        getAllDumpingPeople()
    }

    private val _state = MutableStateFlow(CustomersStates())
    val state: StateFlow<CustomersStates> = _state.asStateFlow()

    fun event(action: CustomersEvents) {
        when (action) {
            CustomersEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }
        }
    }


    private fun getAllDumpingPeople() {
        viewModelScope.launch(Dispatchers.IO) {
            customersGetDumpingPeopleUseCase().collect { result ->
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
                                    data = result.data,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}