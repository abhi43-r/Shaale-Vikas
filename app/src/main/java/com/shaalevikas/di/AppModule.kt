package com.shaalevikas.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.shaalevikas.repository.AuthRepository
import com.shaalevikas.repository.FirebaseAuthRepository
import com.shaalevikas.repository.FirebaseNeedsRepository
import com.shaalevikas.repository.NoOpStorageRepository
import com.shaalevikas.repository.NeedsRepository
import com.shaalevikas.repository.StorageRepository
import com.shaalevikas.utils.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideDispatchers(): AppDispatchers = AppDispatchers()

    @Provides
    @Singleton
    fun provideStorageRepository(): StorageRepository = NoOpStorageRepository()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = FirebaseAuthRepository(auth, firestore)

    @Provides
    @Singleton
    fun provideNeedsRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storageRepository: StorageRepository
    ): NeedsRepository = FirebaseNeedsRepository(firestore, auth, storageRepository)
}
