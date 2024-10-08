package com.example.medscape20.di

import com.example.medscape20.data.remote.MedscapeNewsApi
import com.example.medscape20.data.remote.repository.AuthRepositoryImplementation
import com.example.medscape20.data.remote.repository.UserRepositoryImplementation
import com.example.medscape20.domain.repository.AuthRepository
import com.example.medscape20.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseStorage: FirebaseStorage,
        firebaseDatabase: FirebaseDatabase
    ): AuthRepository {
        return AuthRepositoryImplementation(firebaseAuth, firebaseStorage,firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firebaseStorage: FirebaseStorage,
        firebaseDatabase: FirebaseDatabase,
        medscapeNewsApi: MedscapeNewsApi
    ):UserRepository{
        return UserRepositoryImplementation(firebaseAuth,firebaseStorage,firebaseDatabase,medscapeNewsApi)
    }
}


