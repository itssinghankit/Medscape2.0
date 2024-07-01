package com.example.medscape20.data.remote.repository

import com.example.medscape20.data.remote.dto.Avatar.AvatarSaveAvatarDto
import com.example.medscape20.data.remote.dto.Avatar.AvatarSaveDetailsDto
import com.example.medscape20.data.remote.dto.Avatar.AvatarSignupDto
import com.example.medscape20.domain.repository.AuthRepository
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseAuth
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

    override suspend fun signup(
        avatarSignupDto: AvatarSignupDto
    ): Flow<ApiResult<FirebaseUser, DataError.Network>> = flow {
        try {
            emit(ApiResult.Loading)
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                avatarSignupDto.email,
                avatarSignupDto.password
            ).await()
            emit(ApiResult.Success(authResult.user!!))
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthUserCollisionException -> emit(ApiResult.Error(DataError.Network.ALREADY_CREATED))
                else -> emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }

        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveAvatar(avatarSaveAvatarDto: AvatarSaveAvatarDto): Flow<ApiResult<String, DataError.Network>> =
        flow{
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

    override suspend fun saveDetails(avatarSaveDetailsDto: AvatarSaveDetailsDto): Flow<ApiResult<Boolean, DataError.Network>> =
        flow {
            try {
                emit(ApiResult.Loading)
                val databaseRef =
                    firebaseDatabase.getReference(avatarSaveDetailsDto.uid).child("details")
                databaseRef.setValue(avatarSaveDetailsDto).await()
                emit(ApiResult.Success(true))

            } catch (e: Exception) {
                emit(ApiResult.Error(DataError.Network.INTERNAL_SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)

}