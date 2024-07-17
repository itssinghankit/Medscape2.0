package com.example.medscape20.data.remote.repository

import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveAvatarReqDto
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveDetailsReqDto
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSignupReqDto
import com.example.medscape20.data.remote.dto.auth.login.LoginGetUserDataResDto
import com.example.medscape20.data.remote.dto.auth.login.LoginReqDto
import com.example.medscape20.data.remote.dto.user.customer.home.HomeGetUserDataResDto
import com.example.medscape20.domain.repository.AuthRepository
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImplementation @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase
) :
    AuthRepository {

    override suspend fun login(loginReqDto: LoginReqDto): Flow<ApiResult<Unit, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                firebaseAuth.signInWithEmailAndPassword(loginReqDto.email, loginReqDto.password)
                    .await()
                emit(ApiResult.Success(Unit))
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> emit(ApiResult.Error(DataError.Network.NOT_FOUND))
                    else -> emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
                }

            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getUserData(uid: String): Flow<ApiResult<LoginGetUserDataResDto, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val dataRef = firebaseDatabase.getReference("/users/$uid")
                val data = dataRef.get().await()
                val user = data.getValue(LoginGetUserDataResDto::class.java)!!

                emit(ApiResult.Success(user))
            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)


    override suspend fun signup(
        avatarSignupReqDto: AvatarSignupReqDto
    ): Flow<ApiResult<FirebaseUser, DataError.Network>> = flow {
        try {
            emit(ApiResult.Loading)
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                avatarSignupReqDto.email,
                avatarSignupReqDto.password
            ).await()
            emit(ApiResult.Success(authResult.user!!))
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthUserCollisionException -> emit(ApiResult.Error(DataError.Network.ALREADY_CREATED))
                else -> emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveAvatar(avatarSaveAvatarDto: AvatarSaveAvatarReqDto): Flow<ApiResult<String, DataError.Network>> =
        flow {
            try {

                emit(ApiResult.Loading)
                val imageRef = firebaseStorage.reference.child("avatars/${avatarSaveAvatarDto.uid}")
                val uploadTask = imageRef.putFile(avatarSaveAvatarDto.avatar).await()
                val imageUrl = uploadTask.metadata!!.reference!!.downloadUrl.await()
                emit(ApiResult.Success(imageUrl.toString()))

            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }.flowOn(Dispatchers.IO)

    override suspend fun saveDetails(avatarSaveDetailsDto: AvatarSaveDetailsReqDto): Flow<ApiResult<Boolean, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val databaseRef =
                    firebaseDatabase.getReference("users").child(avatarSaveDetailsDto.uid)
                databaseRef.setValue(avatarSaveDetailsDto).await()
                emit(ApiResult.Success(true))

            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)

}