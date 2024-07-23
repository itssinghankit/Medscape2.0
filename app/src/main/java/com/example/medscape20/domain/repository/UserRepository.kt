package com.example.medscape20.domain.repository

import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.data.remote.dto.user.customer.home.HomeGetUserDataResDto
import com.example.medscape20.data.remote.dto.user.customer.home.category.CategoryResDto
import com.example.medscape20.data.remote.dto.user.customer.home.sources.SourcesDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.income_waste.StatisticsIncomeWasteDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.india_waste_treatment.StatisticsIndiaWasteTreatmentDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.region_waste.StatisticsRegionWasteDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.waste_composition.StatisticsWasteCompositionDto
import com.example.medscape20.domain.models.ArticleModel
import com.example.medscape20.domain.models.CustomerHomePeopleCountModel
import com.example.medscape20.domain.models.TrashIsDumpedModel
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserData(uid: String): Flow<ApiResult<HomeGetUserDataResDto, DataError.Network>>
    suspend fun getCategoryData(): Flow<ApiResult<ArrayList<CategoryResDto>, DataError.Network>>
    suspend fun getSourcesData(): Flow<ApiResult<ArrayList<SourcesDto>, DataError.Network>>
    suspend fun getNewsArticles(params: Pair<String, Map<String, String>>): Flow<ApiResult<List<ArticleModel>, DataError.Network>>
    suspend fun getStatsRegionWaste(): Flow<ApiResult<StatisticsRegionWasteDto, DataError.Network>>
    suspend fun getStatsIncomeWaste(): Flow<ApiResult<StatisticsIncomeWasteDto, DataError.Network>>
    suspend fun getStatsWasteComposition(): Flow<ApiResult<StatisticsWasteCompositionDto, DataError.Network>>
    suspend fun getStatsIndiaWasteTreatment(): Flow<ApiResult<StatisticsIndiaWasteTreatmentDto, DataError.Network>>
    suspend fun updateDatabase(uid: String, updates:  HashMap<String, Any>): Flow<ApiResult<Unit, DataError.Network>>
    suspend fun isDumped(uid: String): Flow<ApiResult<TrashIsDumpedModel, DataError.Network>>
    suspend fun getDumpingPeopleCount(city:String,state:String):Flow<ApiResult<CustomerHomePeopleCountModel,DataError.Network>>
    suspend fun getDumpingPeoples():Flow<ApiResult<ArrayList<CustomersResDto>,DataError.Network>>
    suspend fun updatePasswordLoggedIn(newPassword:String,oldPassword:String,email:String):Flow<ApiResult<Unit,DataError.Network>>
}