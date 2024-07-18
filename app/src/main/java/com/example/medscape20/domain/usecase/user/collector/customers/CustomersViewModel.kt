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
    val data: List<CustomersResDto> = emptyList(),
    val newFilteredList: List<CustomersResDto> = emptyList(),
    val collectorCity: String = "",
    val collectorState: String = ""
)

data class CustomersFilters(
    val cityFilter: Boolean = false,
    val stateFilter: Boolean = false,
    val generalFilter: Boolean = false,
    val medicalFilter: Boolean = false,
    val plasticFilter: Boolean = false,
    val metalFilter: Boolean = false,
)

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customersGetDumpingPeopleUseCase: CustomersGetDumpingPeopleUseCase,
    private val customersSetFilterUseCase: CustomersSetFilterUseCase
) : ViewModel() {

    init {
        getAllDumpingPeople()
    }

    private val _state = MutableStateFlow(CustomersStates())
    val state: StateFlow<CustomersStates> = _state.asStateFlow()

    private val _filter = MutableStateFlow(CustomersFilters())
    val filter: StateFlow<CustomersFilters> = _filter.asStateFlow()

    fun event(action: CustomersEvents) {
        when (action) {
            CustomersEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }

            is CustomersEvents.OnNewFiltersSet -> {
                _filter.update {
                    it.copy(
                        stateFilter = action.newFilters.contains(CustomersTrashFilters.STATE.name),
                        cityFilter = action.newFilters.contains(CustomersTrashFilters.CITY.name),
                        generalFilter = action.newFilters.contains(CustomersTrashFilters.GENERAL.name),
                        medicalFilter = action.newFilters.contains(CustomersTrashFilters.MEDICAL.name),
                        plasticFilter = action.newFilters.contains(CustomersTrashFilters.PLASTIC.name),
                        metalFilter = action.newFilters.contains(CustomersTrashFilters.METAL.name)
                    )
                }

                setNewFilter()

            }

            is CustomersEvents.SetCollectorCityState -> {
                _state.update {
                    it.copy(
                        collectorCity = action.city,
                        collectorState = action.state
                    )
                }
            }
        }
    }

    private fun setNewFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            val newFilteredList = CustomersFilters(
                cityFilter = filter.value.cityFilter,
                stateFilter = filter.value.stateFilter,
                generalFilter = filter.value.generalFilter,
                medicalFilter = filter.value.medicalFilter,
                plasticFilter = filter.value.plasticFilter,
                metalFilter = filter.value.metalFilter
            )
            val newDataList =
                customersSetFilterUseCase(
                    state.value.data,
                    newFilteredList,
                    state.value.collectorCity,
                    state.value.collectorState
                )

            _state.update {
                it.copy(newFilteredList = newDataList)
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

enum class CustomersTrashFilters {
    REQUEST_KEY,
    ARGUMENT_KEY,
    FILTERS_LIST,
    CITY,
    STATE,
    METAL,
    PLASTIC,
    GENERAL,
    MEDICAL
}