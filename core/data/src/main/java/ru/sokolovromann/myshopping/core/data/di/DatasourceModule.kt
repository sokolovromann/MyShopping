package ru.sokolovromann.myshopping.core.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStore
import ru.sokolovromann.myshopping.core.data.datasource.LocalRoomDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideLocalRoomDatabase(@ApplicationContext context: Context) = LocalRoomDatabase.build(context)

    @Provides
    @Singleton
    fun provideCartsDao(database: LocalRoomDatabase) = database.getCartsDao()

    @Provides
    @Singleton
    fun provideProductsDao(database: LocalRoomDatabase) = database.getProductsDao()

    @Provides
    @Singleton
    fun provideSuggestionsDao(database: LocalRoomDatabase) = database.getSuggestionsDao()

    @Provides
    @Singleton
    fun provideFabricsDao(database: LocalRoomDatabase) = database.getFabricsDao()

    @Provides
    @Singleton
    fun provideLocalDataStore(@ApplicationContext context: Context) = LocalDataStore(context)
}