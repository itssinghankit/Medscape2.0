package com.example.medscape20.data.remote.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.medscape20.domain.repository.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class DataStoreRepositoryImplementation @Inject constructor(@ApplicationContext private val context: Context) :
    DataStoreRepository {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

    override suspend fun getString(preferencesKeys: PreferencesKeys): Flow<String?> =
        context.datastore.data.catch {
            if (this is Exception) {
                emit(emptyPreferences())

            }
        }.map { preference ->
            Timber.d("preference key" + preferencesKeys.key)
            val datastoreKey = stringPreferencesKey(preferencesKeys.key)
            val value = preference[datastoreKey] ?: ""
            Timber.d("value" + value)
            value
        }

    override suspend fun getBoolean(preferencesKeys: PreferencesKeys): Flow<Boolean?> =
        context.datastore.data.catch {
            if (this is Exception) {
                emit(emptyPreferences())

            }
        }.map { preference ->
            val datastoreKey = booleanPreferencesKey(preferencesKeys.key)
            preference[datastoreKey] ?: false
        }

    override suspend fun getDouble(preferencesKeys: PreferencesKeys): Flow<Double?> =
        context.datastore.data.catch {
            if (this is Exception) {
                emit(emptyPreferences())

            }
        }.map { preference ->
            val datastoreKey = doublePreferencesKey(preferencesKeys.key)
            preference[datastoreKey] ?: 0.0
        }

    override suspend fun save(preferencesKeys: PreferencesKeys, value: String) {
        Timber.d("datastore ${context.datastore}")
        Timber.d("preferces save called $value")
        val datastoreKey = stringPreferencesKey(preferencesKeys.key)
        Timber.d(context.toString())
        try {
            context.datastore.edit { preference ->
                Timber.d("inside edit")
                preference[datastoreKey] = value
                Timber.d(preference.toString())
            }
        } catch (e: Exception) {
            Timber.d("inside catch")
            Timber.d(e)
        }
        Timber.d("end save")
    }

    override suspend fun save(preferencesKeys: PreferencesKeys, value: Boolean) {
        val datastoreKey = booleanPreferencesKey(preferencesKeys.key)
        context.datastore.edit { preference ->
            preference[datastoreKey] = value
        }
    }

    override suspend fun save(preferencesKeys: PreferencesKeys, value: Double) {
        val datastoreKey = doublePreferencesKey(preferencesKeys.key)
        context.datastore.edit { preference ->
            preference[datastoreKey] = value
        }
    }

    override suspend fun deleteString(preferencesKeys: PreferencesKeys) {
        val datastoreKey = stringPreferencesKey(preferencesKeys.key)
        context.datastore.edit {
            it.remove(datastoreKey)
        }
    }

    companion object {
        const val DATASTORE_NAME = "datastore"
    }

}


