package com.example.medscape20.domain.usecase.home.home

import com.example.medscape20.data.remote.repository.datastore.PreferencesKeys
import com.example.medscape20.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class HomeGetSavedDataStoreUseCase @Inject constructor(private val dataStoreRepository: DataStoreRepository) {
    suspend operator fun invoke():Pair<String,String> {
        Timber.d("CAlled")
        val name=dataStoreRepository.getString(PreferencesKeys.NAME).first()?:""
        Timber.d(name+"name")
        val avatar=dataStoreRepository.getString(PreferencesKeys.AVATAR).first()?:""
        Timber.d(name+avatar+"avatar and name")
        return Pair(name,avatar)
    }

}