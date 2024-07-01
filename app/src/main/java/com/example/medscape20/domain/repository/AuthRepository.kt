package com.example.medscape20.domain.repository

import android.net.Uri
import com.example.medscape20.data.remote.dto.Avatar.AvatarSaveAvatarDto
import com.example.medscape20.data.remote.dto.Avatar.AvatarSaveDetailsDto
import com.example.medscape20.data.remote.dto.Avatar.AvatarSignupDto
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signup(avatarSignupDto:AvatarSignupDto): Flow<ApiResult<FirebaseUser, DataError.Network>>
    suspend fun saveAvatar(avatarSaveAvatarDto: AvatarSaveAvatarDto): Flow<ApiResult<String,DataError.Network>>
    suspend fun saveDetails(avatarSaveDetailsDto: AvatarSaveDetailsDto):Flow<ApiResult<Boolean,DataError.Network>>

}