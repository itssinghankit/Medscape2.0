package com.example.medscape20.domain.repository

import com.example.medscape20.data.remote.dto.user.home.HomeGetUserDataResDto
import com.example.medscape20.data.remote.dto.user.home.articles.NewsResDto
import com.example.medscape20.data.remote.dto.user.home.category.CategoryResDto
import com.example.medscape20.domain.models.HomeArticleModel
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserData(uid:String): Flow<ApiResult<HomeGetUserDataResDto, DataError.Network>>
    suspend fun getCategoryData():Flow<ApiResult<ArrayList<CategoryResDto>,DataError.Network>>
    suspend fun getHomeNewsArticles():Flow<ApiResult<List<HomeArticleModel>,DataError.Network>>

}