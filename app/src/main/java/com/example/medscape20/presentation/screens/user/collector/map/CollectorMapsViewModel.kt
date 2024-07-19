package com.example.medscape20.presentation.screens.user.collector.map

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.domain.usecase.user.collector.maps.CollectorMapGetDumpingPeopleUseCase
import com.example.medscape20.domain.usecase.user.collector.maps.CollectorMapsDisposedWasteUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CollectorMapsStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val setFilter: Boolean = false,
    val currentItemPosition: Int? = null,
    val data: List<CustomersResDto> = emptyList(),
    val newFilteredList: List<CustomersResDto> = emptyList(),
)

data class CollectorMapFilters(
    val showAll:Boolean=false,
    val radius:Double = 0.5,
    val currentLoc:LatLng?=null
)

@HiltViewModel
class CollectorMapsViewModel @Inject constructor(
    private val collectorMapGetDumpingPeopleUseCase: CollectorMapGetDumpingPeopleUseCase,
    private val collectorMapsDisposedWasteUseCase: CollectorMapsDisposedWasteUseCase
): ViewModel(){

    private val _state = MutableStateFlow(CollectorMapsStates())
    val state: StateFlow<CollectorMapsStates> = _state.asStateFlow()

    private var _filters=CollectorMapFilters()
    val filters get() = _filters


    fun event(action: CollectorMapsEvents){
        when(action){
            CollectorMapsEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }

            is CollectorMapsEvents.OnNewFiltersSet -> {
                _filters = _filters.copy(radius = action.radius, showAll = action.showAll)
                _state.update { it.copy(setFilter = true) }
            }

            CollectorMapsEvents.FilterSettedSuccessfully -> {
                _state.update { it.copy(setFilter = false) }
            }

            is CollectorMapsEvents.OnDisposedClicked ->{
                val updates = hashMapOf<String, Any>(
                    "dump" to false,
                    "metal" to false,
                    "general" to false,
                    "medical" to false,
                    "plastic" to false
                )
                updateDataBase(updates, action.position)
            }

            is CollectorMapsEvents.SaveCurrentLocation -> {

                   _filters = _filters.copy(currentLoc = action.currLoc)

            }
        }
    }

    fun getAllDumpingPeople() {

        viewModelScope.launch(Dispatchers.IO) {
            collectorMapGetDumpingPeopleUseCase().collect { result ->
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
                                    data = result.data,
                                    newFilteredList = result.data,
                                    setFilter = true
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    private fun updateDataBase(updates: HashMap<String, Any>, position: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            val uid =_state.value.newFilteredList[position].uid
            collectorMapsDisposedWasteUseCase(uid, updates).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {

                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_internal_server
                                        )
                                    }
                                }
                            }

                            else -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_unknown
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
                        val newUpdatedList = state.value.newFilteredList.toMutableList().also {
                            it.removeAt(position)
                        }
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = false,
                                    newFilteredList = newUpdatedList,
                                    setFilter = true
                                )
                            }
                        }

                    }
                }

            }
        }
    }

}