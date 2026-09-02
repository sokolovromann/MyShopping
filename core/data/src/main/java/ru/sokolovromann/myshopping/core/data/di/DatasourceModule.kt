package ru.sokolovromann.myshopping.core.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.sokolovromann.myshopping.core.data.datasource.AddEditProductPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.BackupPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.CartsPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.GeneralPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStore
import ru.sokolovromann.myshopping.core.data.datasource.LocalRoomDatabase
import ru.sokolovromann.myshopping.core.data.datasource.LocalRoomDatabaseCallback
import ru.sokolovromann.myshopping.core.data.datasource.ProductsPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.ProductsWidgetPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.SuggestionsPreferencesMigration
import ru.sokolovromann.myshopping.core.data.datasource.UserConfigMigration
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15LocalDataStore
import ru.sokolovromann.myshopping.core.data.old.datasource.Api15LocalRoomDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideLocalRoomDatabase(
        @ApplicationContext context: Context,
        callback: LocalRoomDatabaseCallback
    ) = LocalRoomDatabase.build(context, callback)

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
    fun provideGeneralPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: GeneralPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.GENERAL_FILE_NAME)

    @Provides
    @Singleton
    @CartsPreferencesDataStore
    fun provideCartsPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: CartsPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.CARTS_FILE_NAME)

    @Provides
    @Singleton
    @ProductsPreferencesDataStore
    fun provideProductsPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: ProductsPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.PRODUCTS_FILE_NAME)

    @Provides
    @Singleton
    @ProductsWidgetPreferencesDataStore
    fun provideProductsWidgetPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: ProductsWidgetPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.PRODUCTS_WIDGET_FILE_NAME)

    @Provides
    @Singleton
    @AddEditProductPreferencesDataStore
    fun provideAddEditProductPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: AddEditProductPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.ADD_EDIT_PRODUCT_FILE_NAME)

    @Provides
    @Singleton
    @SuggestionsPreferencesDataStore
    fun provideSuggestionsPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: SuggestionsPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.SUGGESTIONS_FILE_NAME)

    @Provides
    @Singleton
    @BackupPreferencesDataStore
    fun provideBackupPreferencesDataStore(
        @ApplicationContext context: Context,
        migration: BackupPreferencesMigration
    ) = LocalDataStore.build(context, migration, LocalDataStore.BACKUP_FILE_NAME)

    @Provides
    @Singleton
    @UserConfigDataStore
    fun provideUserConfigDataStore(
        @ApplicationContext context: Context,
        migration: UserConfigMigration,
    ) = LocalDataStore.build(context, migration, LocalDataStore.USER_FILE_NAME)

    @Provides
    @Singleton
    fun provideApi15LocalRoomDatabase(@ApplicationContext context: Context) = Api15LocalRoomDatabase.build(context)

    @Provides
    @Singleton
    fun provideApi15Dao(database: Api15LocalRoomDatabase) = database.getApi15Dao()

    @Provides
    @Singleton
    @Api15DataStore
    fun provideApi15LocalDataStore(@ApplicationContext context: Context) = Api15LocalDataStore.build(context)
}