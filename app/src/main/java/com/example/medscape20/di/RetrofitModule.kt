package com.example.medscape20.di

import com.example.medscape20.BuildConfig
import com.example.medscape20.data.remote.MedscapeNewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMedscapeNewsApi(retrofit: Retrofit): MedscapeNewsApi {
       return retrofit.create(MedscapeNewsApi::class.java)
    }

}