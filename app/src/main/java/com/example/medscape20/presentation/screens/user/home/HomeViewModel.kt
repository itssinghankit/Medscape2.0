package com.example.medscape20.presentation.screens.user.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.BuildConfig
import com.example.medscape20.R
import com.example.medscape20.domain.models.ArticleModel
import com.example.medscape20.domain.usecase.user.home.HomeGetNewsArticlesUseCase
import com.example.medscape20.domain.usecase.user.home.HomeGetSavedDataStoreUseCase
import com.example.medscape20.domain.usecase.user.home.HomeGetUserDataUseCase
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
    val newsArticlesList: List<ArticleModel> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeGetUserDataUseCase: HomeGetUserDataUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val homeGetSavedDataStoreUseCase: HomeGetSavedDataStoreUseCase,
    private val homeGetNewsArticlesUseCase: HomeGetNewsArticlesUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow(HomeStates())
    val state: StateFlow<HomeStates> = _state.asStateFlow()

    init {
        getDataFromDatastore()
        getUserData()
//        getNewsArticles()
//        https://newsapi.org/v2/top-headlines?country=us&apiKey=6161697ad1174baca526057bbddd80a3
    }

    fun event(action: HomeEvents) {
        when (action) {

            HomeEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }
        }
    }
//    BASE_URL=https://newsapi.org/v2/
//    NEWS_API_KEY=&apiKey=6161697ad1174baca526057bbddd80a3
//    #everything?q=waste-management&apiKey=6161697ad1174baca526057bbddd80a3


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

    private fun createParameter(): Pair<String, Map<String, String>> {
        val path = "everything"
        val queryParams = mapOf("q" to "waste-management", "apiKey" to BuildConfig.NEWS_API_KEY)
        return Pair(path, queryParams)
    }

    private fun getNewsArticles() {

        val params = createParameter()

        viewModelScope.launch(Dispatchers.IO) {
            homeGetNewsArticlesUseCase(params).collect { result ->
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