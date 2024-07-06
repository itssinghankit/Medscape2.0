package com.example.medscape20.domain.usecase.login

import com.example.medscape20.data.remote.dto.login.LoginGetUserDataResDto
import com.example.medscape20.domain.repository.DataStoreRepository
import com.example.medscape20.data.remote.repository.datastore.PreferencesKeys
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class LoginSaveDataUseCase @Inject constructor(private val dataStoreRepository: DataStoreRepository) {
    suspend operator fun invoke(data: LoginGetUserDataResDto){
//        Timber.d("entered")
//        Timber.d(data.name+"this is name")
//        dataStoreRepository.save(PreferencesKeys.NAME,data.name?:"")
//        Timber.d("Second called")
//        dataStoreRepository.save(PreferencesKeys.LAT,data.lat?:0.0)
//        dataStoreRepository.save(PreferencesKeys.LNG,data.lng?:0.0)
//        dataStoreRepository.save(PreferencesKeys.UID,data.uid?:"")
//        dataStoreRepository.save(PreferencesKeys.CITY,data.city?:"")
//        dataStoreRepository.save(PreferencesKeys.STATE,data.state?:"")
//        dataStoreRepository.save(PreferencesKeys.ADDRESS,data.address?:"")
//        dataStoreRepository.save(PreferencesKeys.AVATAR,data.avatar?:"")
//        dataStoreRepository.save(PreferencesKeys.EMAIL,data.email?:"")
//        dataStoreRepository.save(PreferencesKeys.GENDER,data.gender?:"")
//        dataStoreRepository.save(PreferencesKeys.LOCALITY,data.locality?:"")
//        dataStoreRepository.save(PreferencesKeys.MOBILE,data.mobile?:"")
//
//        val a=dataStoreRepository.getString(PreferencesKeys.NAME).first()?:""
//        Timber.d(data.name+"coming data")
//        Timber.d("datastore data $a")

    }

}