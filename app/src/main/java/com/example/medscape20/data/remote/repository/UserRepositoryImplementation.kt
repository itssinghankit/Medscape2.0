package com.example.medscape20.data.remote.repository

import com.example.medscape20.data.mapper.toArticleList
import com.example.medscape20.data.remote.MedscapeNewsApi
import com.example.medscape20.data.remote.dto.user.home.HomeGetUserDataResDto
import com.example.medscape20.data.remote.dto.user.home.category.CategoryResDto
import com.example.medscape20.domain.models.ArticleModel
import com.example.medscape20.domain.repository.UserRepository
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseAuth
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

    override suspend fun getNewsArticles(): Flow<ApiResult<List<ArticleModel>, DataError.Network>> =
        flow{
            try {
                emit(ApiResult.Loading)
                val response=medscapeNewsApi.getNewsArticles().toArticleList()
                emit(ApiResult.Success(response))

            } catch (e: HttpException ) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))

            } catch (e: IOException) {
                emit(ApiResult.Error(DataError.Network.NO_INTERNET))
            }
        }.flowOn(Dispatchers.IO)

}
