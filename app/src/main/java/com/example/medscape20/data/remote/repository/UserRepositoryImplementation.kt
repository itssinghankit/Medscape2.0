package com.example.medscape20.data.remote.repository

import com.example.medscape20.data.remote.dto.user.home.HomeGetUserDataResDto
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
import javax.inject.Inject

class UserRepositoryImplementation @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase
) : UserRepository {

    override suspend fun getUserData(uid: String): Flow<ApiResult<HomeGetUserDataResDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/users/$uid")
                val data = dataRef.get().await()
                val user = data.getValue(HomeGetUserDataResDto::class.java)!!

                emit(ApiResult.Success(user))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

}