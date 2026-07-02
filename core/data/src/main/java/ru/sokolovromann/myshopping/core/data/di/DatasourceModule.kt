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
    @GeneralPreferencesDataStore
    fun provideGeneralPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.GENERAL_FILE_NAME)

    @Provides
    @Singleton
    @CartsPreferencesDataStore
    fun provideCartsPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.CARTS_FILE_NAME)

    @Provides
    @Singleton
    @ProductsPreferencesDataStore
    fun provideProductsPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.PRODUCTS_FILE_NAME)

    @Provides
    @Singleton
    @ProductsWidgetPreferencesDataStore
    fun provideProductsWidgetPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.PRODUCTS_WIDGET_FILE_NAME)

    @Provides
    @Singleton
    @AddEditProductPreferencesDataStore
    fun provideAddEditProductPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.ADD_EDIT_PRODUCT_FILE_NAME)

    @Provides
    @Singleton
    @SuggestionsPreferencesDataStore
    fun provideSuggestionsPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.SUGGESTIONS_FILE_NAME)

    @Provides
    @Singleton
    @BackupPreferencesDataStore
    fun provideBackupPreferencesDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.BACKUP_FILE_NAME)

    @Provides
    @Singleton
    @UserConfigDataStore
    fun provideUserConfigDataStore(@ApplicationContext context: Context) =
        LocalDataStore.build(context, LocalDataStore.USER_FILE_NAME)
}