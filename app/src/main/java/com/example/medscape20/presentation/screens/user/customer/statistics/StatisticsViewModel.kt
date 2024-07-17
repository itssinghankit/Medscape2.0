package com.example.medscape20.presentation.screens.user.customer.statistics

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.data.remote.dto.user.customer.statistics.income_waste.StatisticsIncomeWasteDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.india_waste_treatment.StatisticsIndiaWasteTreatmentDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.region_waste.StatisticsRegionWasteDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.waste_composition.StatisticsWasteCompositionDto
import com.example.medscape20.domain.usecase.user.customer.statistics.StatisticsGetIncomeWasteDataUseCase
import com.example.medscape20.domain.usecase.user.customer.statistics.StatisticsGetIndiaWasteTreatmentDataUseCase
import com.example.medscape20.domain.usecase.user.customer.statistics.StatisticsGetRegionWasteDataUseCase
import com.example.medscape20.domain.usecase.user.customer.statistics.StatisticsGetWasteCompositionDataUseCase
import com.example.medscape20.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

data class StatisticsStates(
    val isLoading: Boolean = false,
    val error: String = "",
    @StringRes val errorMessage: Int? = null,
    val incomeWasteData: StatisticsIncomeWasteDto?=null,
    val indiaWasteTreatmentData: StatisticsIndiaWasteTreatmentDto?=null,
    val wasteCompositionData: StatisticsWasteCompositionDto?=null,
    val regionWasteData: StatisticsRegionWasteDto?=null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getRegionWasteDataUseCase: StatisticsGetRegionWasteDataUseCase,
    private val getIncomeWasteDataUseCase: StatisticsGetIncomeWasteDataUseCase,
    private val getWasteCompositionDataUseCase: StatisticsGetWasteCompositionDataUseCase,
    private val getIndiaWasteTreatmentDataUseCase: StatisticsGetIndiaWasteTreatmentDataUseCase
) : ViewModel() {

    init {
        getRegionWasteData()
        getWasteCompositionData()
        getIncomeWasteData()
        getIndiaWasteTreatmentData()
    }

    private val _states= MutableStateFlow(StatisticsStates())
    val states:StateFlow<StatisticsStates> = _states.asStateFlow()

    private fun getRegionWasteData() {

        viewModelScope.launch(Dispatchers.IO) {
            getRegionWasteDataUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        Timber.d("error")
                    }

                    ApiResult.Loading -> {
                        Timber.d("loading")
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main){
                                it.copy(regionWasteData = result.data)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun getIncomeWasteData() {

        viewModelScope.launch(Dispatchers.IO) {
            getIncomeWasteDataUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        Timber.d("error")
                    }

                    ApiResult.Loading -> {
                        Timber.d("loading")
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main){
                                it.copy(incomeWasteData = result.data)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun getWasteCompositionData() {

        viewModelScope.launch(Dispatchers.IO) {
            getWasteCompositionDataUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        Timber.d("error")
                    }

                    ApiResult.Loading -> {
                        Timber.d("loading")
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main){
                                it.copy(wasteCompositionData = result.data)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun getIndiaWasteTreatmentData() {

        viewModelScope.launch(Dispatchers.IO) {
            getIndiaWasteTreatmentDataUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        Timber.d("error")
                    }

                    ApiResult.Loading -> {
                        Timber.d("loading")
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main){
                                it.copy(indiaWasteTreatmentData = result.data)
                            }
                        }
                    }
                }
            }

        }
    }
}