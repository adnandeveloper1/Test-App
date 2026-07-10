package com.nexappra.testapp.di

import com.nexappra.testapp.data.repository.AuthRepository
import com.nexappra.testapp.data.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.nexappra.testapp.data.repository.NotificationRepository
import com.nexappra.testapp.data.repository.NotificationRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        implementation: NotificationRepositoryImpl
    ): NotificationRepository
}
