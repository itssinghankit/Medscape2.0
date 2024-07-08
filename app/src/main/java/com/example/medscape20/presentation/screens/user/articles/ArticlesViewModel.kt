package com.example.medscape20.presentation.screens.user.articles

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.BuildConfig
import com.example.medscape20.R
import com.example.medscape20.domain.models.ArticleModel
import com.example.medscape20.domain.usecase.user.articles.ArticlesGetNewsUseCase
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
import okhttp3.internal.toImmutableMap
import javax.inject.Inject

data class ArticlesStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val newsArticlesList: ArrayList<ArticleModel> = arrayListOf(),
    val category: String = NewsCategory.ALL.value,
    val countryAbbreviation: String = "all",
    val showResultNullError:Boolean=false
)

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articlesGetNewsArticlesUseCase: ArticlesGetNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ArticlesStates())
    val state: StateFlow<ArticlesStates> = _state.asStateFlow()

    val searchTxt = MutableStateFlow("")

    init {
        getNewsArticles()
    }

    fun event(action: ArticlesEvents) {
        when (action) {

            ArticlesEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }

            is ArticlesEvents.OnFilterSet -> {
                _state.update {
                    it.copy(
                        countryAbbreviation = action.countryAbbreviation,
                        category = action.category
                    )
                }
                getNewsArticles()
            }
        }
    }

    //creating parameters according to selected filters
    private fun createParameter(): Pair<String, Map<String, String>> {
        val path = state.value.category

        val queryParams = mutableMapOf<String,String>()
        if(path==NewsCategory.TOP_HEADLINES.value && state.value.countryAbbreviation!="all"){
            queryParams["country"] = state.value.countryAbbreviation
        }else if(path==NewsCategory.TOP_HEADLINES.value && state.value.countryAbbreviation=="all"){
            queryParams["q"] = "all"
        }
        else{
            queryParams["q"] = searchTxt.value.ifEmpty { "waste management" }
        }
        queryParams["apiKey"] = BuildConfig.NEWS_API_KEY

        return Pair(path, queryParams as Map<String,String>)
    }

    private fun getNewsArticles() {

        val params = createParameter()

        viewModelScope.launch(Dispatchers.IO) {
            articlesGetNewsArticlesUseCase(params).collect { result ->
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
                                    newsArticlesList = if (result.data.isEmpty()) arrayListOf() else result.data as ArrayList,
                                    showResultNullError = result.data.isEmpty()
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}