package com.example.medscape20.domain.repository


import com.example.medscape20.data.repository.datastore.PreferencesKeys
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun getString(preferencesKeys: PreferencesKeys): Flow<String?>
    suspend fun getBoolean(preferencesKeys: PreferencesKeys):Flow<Boolean?>
    suspend fun getDouble(preferencesKeys: PreferencesKeys):Flow<Double?>

    //overloading save function
    suspend fun save(preferencesKeys:PreferencesKeys, value: String)
    suspend fun save(preferencesKeys:PreferencesKeys, value: Boolean)
    suspend fun save(preferencesKeys:PreferencesKeys, value: Double)

    suspend fun deleteString(preferencesKeys: PreferencesKeys)

}