package com.example.medscape20.domain.repository

import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveAvatarReqDto
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveDetailsReqDto
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSignupReqDto
import com.example.medscape20.data.remote.dto.auth.login.LoginGetUserDataResDto
import com.example.medscape20.data.remote.dto.auth.login.LoginReqDto
import com.example.medscape20.data.remote.dto.user.customer.home.HomeGetUserDataResDto
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(loginReqDto: LoginReqDto):Flow<ApiResult<Unit,DataError.Network>>
    suspend fun getUserData(uid:String): Flow<ApiResult<LoginGetUserDataResDto, DataError.Network>>
    suspend fun signup(avatarSignupReqDto: AvatarSignupReqDto): Flow<ApiResult<FirebaseUser, DataError.Network>>
    suspend fun saveAvatar(avatarSaveAvatarDto: AvatarSaveAvatarReqDto): Flow<ApiResult<String,DataError.Network>>
    suspend fun saveDetails(avatarSaveDetailsDto: AvatarSaveDetailsReqDto):Flow<ApiResult<Boolean,DataError.Network>>

}