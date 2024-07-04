package com.example.medscape20.di

import android.content.Context
import com.example.medscape20.domain.repository.DataStoreRepository
import com.example.medscape.data.repository.datastore.DataStoreRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepositoryImplementation(context)
    }

}