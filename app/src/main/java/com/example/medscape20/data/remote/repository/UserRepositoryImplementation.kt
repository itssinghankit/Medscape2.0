package com.example.medscape20.data.remote.repository

import com.example.medscape20.data.mapper.toArticleList
import com.example.medscape20.data.mapper.toPeopleCountModel
import com.example.medscape20.data.mapper.toTrashIsDumpedModel
import com.example.medscape20.data.remote.MedscapeNewsApi
import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.data.remote.dto.user.collector.home.CustomerHomePeopleCountResDto
import com.example.medscape20.data.remote.dto.user.customer.home.category.CategoryResDto
import com.example.medscape20.data.remote.dto.user.customer.home.HomeGetUserDataResDto
import com.example.medscape20.data.remote.dto.user.customer.home.sources.SourcesDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.income_waste.StatisticsIncomeWasteDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.india_waste_treatment.StatisticsIndiaWasteTreatmentDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.region_waste.StatisticsRegionWasteDto
import com.example.medscape20.data.remote.dto.user.customer.statistics.waste_composition.StatisticsWasteCompositionDto
import com.example.medscape20.data.remote.dto.user.customer.trash.TrashIsDumpedResDto
import com.example.medscape20.domain.models.ArticleModel
import com.example.medscape20.domain.models.CustomerHomePeopleCountModel
import com.example.medscape20.domain.models.TrashIsDumpedModel
import com.example.medscape20.domain.repository.UserRepository
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImplementation @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase,
    private val medscapeNewsApi: MedscapeNewsApi
) : UserRepository {

    override suspend fun getUserData(uid: String): Flow<ApiResult<HomeGetUserDataResDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/users/$uid")
                val data = dataRef.get().await()
                Timber.d("data $data")
                val user = data.getValue(HomeGetUserDataResDto::class.java)!!

                emit(ApiResult.Success(user))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun getCategoryData(): Flow<ApiResult<ArrayList<CategoryResDto>, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/types")
                val data = dataRef.get().await()
                val ourList =
                    data.children.mapNotNull { it.getValue(CategoryResDto::class.java) } as ArrayList
                emit(ApiResult.Success(ourList))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun getSourcesData(): Flow<ApiResult<ArrayList<SourcesDto>, DataError.Network>> = flow {
        try {
            emit(ApiResult.Loading)
            val dataRef = firebaseDatabase.getReference("/sources")
            val data = dataRef.get().await()
            val ourList =
                data.children.mapNotNull { it.getValue(SourcesDto::class.java) } as ArrayList
            emit(ApiResult.Success(ourList))
        } catch (e: Exception) {
            emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getNewsArticles(params: Pair<String, Map<String, String>>): Flow<ApiResult<List<ArticleModel>, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val response = medscapeNewsApi.getNewsArticles(
                    path = params.first,
                    queryParams = params.second
                ).toArticleList()
                emit(ApiResult.Success(response))

            } catch (e: HttpException) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))

            } catch (e: IOException) {
                emit(ApiResult.Error(DataError.Network.NO_INTERNET))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getStatsRegionWaste(): Flow<ApiResult<StatisticsRegionWasteDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/stats/region_waste")
                val data = dataRef.get().await()
                val response =
                    data.getValue(StatisticsRegionWasteDto::class.java)
                emit(ApiResult.Success(response!!))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun getStatsIncomeWaste(): Flow<ApiResult<StatisticsIncomeWasteDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/stats/income_waste")
                val data = dataRef.get().await()
                val response =
                    data.getValue(StatisticsIncomeWasteDto::class.java)
                emit(ApiResult.Success(response!!))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun getStatsWasteComposition(): Flow<ApiResult<StatisticsWasteCompositionDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/stats/waste_composition")
                val data = dataRef.get().await()
                val response =
                    data.getValue(StatisticsWasteCompositionDto::class.java)
                emit(ApiResult.Success(response!!))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun getStatsIndiaWasteTreatment(): Flow<ApiResult<StatisticsIndiaWasteTreatmentDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/stats/india_waste_treatment")
                val data = dataRef.get().await()
                val response =
                    data.getValue(StatisticsIndiaWasteTreatmentDto::class.java)
                emit(ApiResult.Success(response!!))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun updateDatabase(
        uid: String,
        updates: HashMap<String, Any>
    ): Flow<ApiResult<Unit, DataError.Network>> = flow {
        try {
            emit(ApiResult.Loading)
            val databaseRef =
                firebaseDatabase.getReference("/users/$uid")
            databaseRef.updateChildren(updates).await()
            emit(ApiResult.Success(Unit))

        } catch (e: Exception) {
            emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isDumped(uid: String): Flow<ApiResult<TrashIsDumpedModel, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/users/$uid")
                val data = dataRef.get().await()
                val response =
                    data.getValue(TrashIsDumpedResDto::class.java)!!.toTrashIsDumpedModel()
                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getDumpingPeopleCount(
        city: String,
        state: String
    ): Flow<ApiResult<CustomerHomePeopleCountModel, DataError.Network>> = flow {
        try {
            emit(ApiResult.Loading)
            val dataRef = firebaseDatabase.getReference("/users")
            val data = dataRef.get().await()
            val response =
                data.children.mapNotNull { it.getValue(CustomerHomePeopleCountResDto::class.java) }
                    .toPeopleCountModel(city = city, state = state)
            emit(ApiResult.Success(response))
        } catch (e: Exception) {
            emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
        }
    }.flowOn(Dispatchers.IO)
    //TODO: }.filter { it.dump }

    override suspend fun getDumpingPeoples(): Flow<ApiResult<ArrayList<CustomersResDto>, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/users")
                val data = dataRef.get().await()
                val response =
                    data.children.mapNotNull { it.getValue(CustomersResDto::class.java) }
                        .filter { it.dump } as ArrayList

                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun updatePasswordLoggedIn(
        newPassword: String,
        oldPassword: String,
        email: String
    ): Flow<ApiResult<Unit, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val user = firebaseAuth.currentUser
                if (user != null && newPassword.isNotEmpty()) {
                    // Reauthenticate the user - security purpose
                    val credential = EmailAuthProvider.getCredential(email, oldPassword)
                    user.reauthenticate(credential).await()

                    user.updatePassword(newPassword).await()
                }

                emit(ApiResult.Success(Unit))
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                emit(ApiResult.Error(DataError.Network.BAD_REQUEST))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)

}
