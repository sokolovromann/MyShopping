package ru.sokolovromann.myshopping.old.api15.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.old.api15.datasource.LocalDataStore
import ru.sokolovromann.myshopping.old.api15.datasource.LocalRoomDatabase

@Module
@InstallIn(SingletonComponent::class)
internal object DataSourceModule {

    @Provides
    @Singleton
    fun provideLocalRoomDatabase(@ApplicationContext context: Context) = LocalRoomDatabase.build(context)

    @Provides
    @Singleton
    fun provideApi15Dao(database: LocalRoomDatabase) = database.getApi15Dao()

    @Provides
    @Singleton
    @Api15DataStore
    fun provideLocalDataStore(@ApplicationContext context: Context) = LocalDataStore.build(context)
}