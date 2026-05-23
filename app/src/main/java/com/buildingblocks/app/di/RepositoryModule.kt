package com.buildingblocks.app.di

import com.buildingblocks.app.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindLegoSetRepository(impl: LegoSetRepositoryImpl): LegoSetRepository

    @Binds @Singleton
    abstract fun bindStorageLocationRepository(impl: StorageLocationRepositoryImpl): StorageLocationRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
