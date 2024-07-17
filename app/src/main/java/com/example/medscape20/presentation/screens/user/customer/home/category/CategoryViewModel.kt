package com.example.medscape20.presentation.screens.user.customer.home.category

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.category.Subtopic
import com.example.medscape20.domain.usecase.user.customer.home.category.CategoryGetDataUseCase
import com.example.medscape20.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CategoryStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val title: String = "",
    val image: String? = null,
    val categoryDescription: List<Subtopic> = emptyList()
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryGetDataUseCase: CategoryGetDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryStates())
    val state: StateFlow<CategoryStates> = _state.asStateFlow()

    fun event(action: CategoryEvents) {
        when (action) {
            CategoryEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }
        }
    }

    fun getCategoryData(category: String) {

        viewModelScope.launch(Dispatchers.IO) {
            categoryGetDataUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            //can add more error types in future
                            else -> {
                                withContext(Dispatchers.Main) {
                                    _state.update {
                                        it.copy(
                                            isError = true,
                                            errMessage = R.string.error_internal_server,
                                            isLoading = false
                                        )
                                    }
                                }

                            }
                        }
                    }

                    ApiResult.Loading -> {
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(isLoading = true)
                            }
                        }

                    }

                    is ApiResult.Success -> {
                        for (item in result.data) {
                            if (item.topic == category) {
                                withContext(Dispatchers.Main) {
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            title = item.topic,
                                            image = item.image,
                                            categoryDescription = item.subtopic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}