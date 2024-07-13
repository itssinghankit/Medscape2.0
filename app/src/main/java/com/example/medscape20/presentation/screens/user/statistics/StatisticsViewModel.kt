package com.example.medscape20.presentation.screens.user.statistics

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.domain.usecase.user.statistics.StatisticsGetIncomeWasteDataUseCase
import com.example.medscape20.domain.usecase.user.statistics.StatisticsGetIndiaWasteTreatmentDataUseCase
import com.example.medscape20.domain.usecase.user.statistics.StatisticsGetRegionWasteDataUseCase
import com.example.medscape20.domain.usecase.user.statistics.StatisticsGetWasteCompositionDataUseCase
import com.example.medscape20.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class StatisticsStates(
    val isLoading: Boolean = false,
    val error: String = "",
    @StringRes val errorMessage: Int? = null

)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getRegionWasteDataUseCase: StatisticsGetRegionWasteDataUseCase,
    private val getIncomeWasteDataUseCase: StatisticsGetIncomeWasteDataUseCase,
    private val getWasteCompositionDataUseCase: StatisticsGetWasteCompositionDataUseCase,
    private val getIndiaWasteTreatmentDataUseCase: StatisticsGetIndiaWasteTreatmentDataUseCase
) : ViewModel() {
    init {
//        getRegionWasteData()
    }

    private val _states= MutableStateFlow(StatisticsStates())
    val states:StateFlow<StatisticsStates> = _states.asStateFlow()

    fun getRegionWasteData() {
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
                        Timber.d("success")
                        Timber.d(result.data.toString())
                    }
                }
            }

        }
    }

    fun getIncomeWasteData() {
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
                        Timber.d("success")
                        Timber.d(result.data.toString())
                    }
                }
            }

        }
    }

    fun getWasteCompositionData() {
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
                        Timber.d("success")
                        Timber.d(result.data.toString())
                    }
                }
            }

        }
    }
    fun getIndiaWasteTreatmentData() {
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
                        Timber.d("success")
                        Timber.d(result.data.toString())
                    }
                }
            }

        }
    }
}