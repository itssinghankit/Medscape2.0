package com.example.medscape20.presentation.screens.user.customer.home.sources

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.customer.home.sources.SourcesDto
import com.example.medscape20.domain.usecase.user.customer.home.sources.SourcesGetDataUseCase
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

data class SourcesStates(
    val isLoading: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val data: ArrayList<SourcesDto> = arrayListOf()
)

@HiltViewModel
class SourcesViewModel @Inject constructor(
    private val getSourcesGetDataUseCase: SourcesGetDataUseCase
) : ViewModel() {

    init {
        getSourcesData()
    }


    private val _states = MutableStateFlow(SourcesStates())
    val states: StateFlow<SourcesStates> = _states.asStateFlow()

    fun event(action: SourcesEvents) {
        when (action) {
            SourcesEvents.ResetErrorMessage -> {
                _states.update {
                    it.copy(errMessage = null)
                }
            }
        }

    }

    private fun getSourcesData() {
        viewModelScope.launch(Dispatchers.IO) {
            getSourcesGetDataUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _states.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isLoading = false,
                                            errMessage = R.string.error_internal_server
                                        )
                                    }
                                }
                            }

                            else -> {
                                _states.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isLoading = false,
                                            errMessage = R.string.error_unknown
                                        )
                                    }
                                }
                            }
                        }
                    }

                    ApiResult.Loading -> {
                        _states.update {
                            withContext(Dispatchers.Main) {
                                it.copy(isLoading = true)
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = false, data = result.data
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}