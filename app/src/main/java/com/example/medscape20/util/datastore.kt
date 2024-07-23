package com.example.medscape20.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object UserPreferences {
    private val Context.dataStoreInternal: DataStore<Preferences> by preferencesDataStore(name = "user_info")

    fun getDataStore(context: Context): DataStore<Preferences> {
        return context.dataStoreInternal
    }
}