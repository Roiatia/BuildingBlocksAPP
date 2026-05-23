package com.buildingblocks.app.di

import android.content.Context
import androidx.room.Room
import com.buildingblocks.app.data.local.AppDatabase
import com.buildingblocks.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides fun provideLegoSetDao(db: AppDatabase): LegoSetDao = db.legoSetDao()
    @Provides fun provideStorageLocationDao(db: AppDatabase): StorageLocationDao = db.storageLocationDao()
    @Provides fun provideMissingPartDao(db: AppDatabase): MissingPartDao = db.missingPartDao()
    @Provides fun provideBuildLogDao(db: AppDatabase): BuildLogDao = db.buildLogDao()
}
