package com.example.medscape20.domain.repository

import com.example.medscape20.data.remote.dto.user.home.HomeGetUserDataResDto
import com.example.medscape20.data.remote.dto.user.home.category.CategoryResDto
import com.example.medscape20.data.remote.dto.user.home.statistics.income_waste.StatisticsIncomeWasteDto
import com.example.medscape20.data.remote.dto.user.home.statistics.india_waste_treatment.StatisticsIndiaWasteTreatmentDto
import com.example.medscape20.data.remote.dto.user.home.statistics.region_waste.StatisticsRegionWasteDto
import com.example.medscape20.data.remote.dto.user.home.statistics.waste_composition.StatisticsWasteCompositionDto
import com.example.medscape20.domain.models.ArticleModel
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserData(uid: String): Flow<ApiResult<HomeGetUserDataResDto, DataError.Network>>
    suspend fun getCategoryData(): Flow<ApiResult<ArrayList<CategoryResDto>, DataError.Network>>
    suspend fun getNewsArticles(params: Pair<String, Map<String, String>>): Flow<ApiResult<List<ArticleModel>, DataError.Network>>
    suspend fun getStatsRegionWaste(): Flow<ApiResult<StatisticsRegionWasteDto, DataError.Network>>
    suspend fun getStatsIncomeWaste(): Flow<ApiResult<StatisticsIncomeWasteDto, DataError.Network>>
    suspend fun getStatsWasteComposition(): Flow<ApiResult<StatisticsWasteCompositionDto, DataError.Network>>
    suspend fun getStatsIndiaWasteTreatment(): Flow<ApiResult<StatisticsIndiaWasteTreatmentDto, DataError.Network>>
}