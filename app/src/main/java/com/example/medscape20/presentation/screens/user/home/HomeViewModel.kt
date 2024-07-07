package com.example.medscape20.presentation.screens.user.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.domain.models.HomeArticleModel
import com.example.medscape20.domain.usecase.home.home.HomeGetNewsArticleUseCase
import com.example.medscape20.domain.usecase.home.home.HomeGetSavedDataStoreUseCase
import com.example.medscape20.domain.usecase.home.home.HomeGetUserDataUseCase
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

data class HomeStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val name: String = "",
    val avatar: String? = null,
    val newsArticlesList: List<HomeArticleModel> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeGetUserDataUseCase: HomeGetUserDataUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val homeGetSavedDataStoreUseCase: HomeGetSavedDataStoreUseCase,
    private val homeGetNewsArticleUseCase: HomeGetNewsArticleUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow(HomeStates())
    val state: StateFlow<HomeStates> = _state.asStateFlow()

    init {
        getDataFromDatastore()
        getUserData()
//        getNewsArticles()
    }

    fun event(action: HomeEvents) {
        when (action) {
            HomeEvents.GetUserData -> {
                getUserData()
            }

            HomeEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }
        }
    }

    private fun getUserData() {
        val uid = firebaseAuth.currentUser!!.uid
        viewModelScope.launch {
            homeGetUserDataUseCase(uid).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            //can add more error types in future
                            else -> {
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

                    ApiResult.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                name = result.data.name ?: "",
                                avatar = result.data.avatar
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getDataFromDatastore() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = homeGetSavedDataStoreUseCase()
            _state.update {
                withContext(Dispatchers.Main) {
                    it.copy(name = data.first, avatar = data.second)
                }

            }
        }
    }

    private fun getNewsArticles() {

        viewModelScope.launch(Dispatchers.IO) {
            homeGetNewsArticleUseCase().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.NO_INTERNET -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_no_internet_connection
                                        )
                                    }
                                }
                            }

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
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = false,
                                    newsArticlesList = result.data
                                )
                            }
                        }
                    }
                }

            }
        }
    }

}