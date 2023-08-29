package ru.sokolovromann.myshopping.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.*
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data.local.datasource.AppRoomDatabase
import ru.sokolovromann.myshopping.data.local.datasource.AppSQLiteOpenHelper
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.*
import ru.sokolovromann.myshopping.media.BackupMediaStore
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesAppContent(@ApplicationContext context: Context): AppContent {
        return AppContent(context)
    }

    @Singleton
    @Provides
    fun providesAppRoomDatabase(@ApplicationContext context: Context): AppRoomDatabase {
        return AppRoomDatabase.build(context)
    }

    @Singleton
    @Provides
    fun providesAppSQLiteOpenHelper(@ApplicationContext context: Context): AppSQLiteOpenHelper {
        return AppSQLiteOpenHelper(context)
    }

    @Singleton
    @Provides
    fun providesLocalDatasource(
        appRoomDatabase: AppRoomDatabase,
        appSQLiteOpenHelper: AppSQLiteOpenHelper,
        appContent: AppContent
    ): LocalDatasource {
        return LocalDatasource(appRoomDatabase, appSQLiteOpenHelper, appContent)
    }

    @Singleton
    @Provides
    fun providesAppDispatchers(): AppDispatchers {
        return AppDispatchers()
    }

    @Provides
    fun providesRepositoryMapping(): RepositoryMapping {
        return RepositoryMapping()
    }

    @Provides
    fun providesPurchasesRepository(repository: PurchasesRepositoryImpl): PurchasesRepository {
        return repository
    }

    @Provides
    fun providesPurchasesRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesRepositoryImpl {
        return PurchasesRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesProductsRepository(repository: ProductsRepositoryImpl): ProductsRepository {
        return repository
    }

    @Provides
    fun providesProductsRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsRepositoryImpl {
        return ProductsRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesProductsWidgetRepository(
        repository: ProductsWidgetRepositoryImpl
    ): ProductsWidgetRepository {
        return repository
    }

    @Provides
    fun providesProductsWidgetRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsWidgetRepositoryImpl {
        return ProductsWidgetRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditProductRepository(repository: AddEditProductRepositoryImpl): AddEditProductRepository {
        return repository
    }

    @Provides
    fun providesAddEditProductRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditProductRepositoryImpl {
        return AddEditProductRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesAutocompletesRepository(repository: AutocompletesRepositoryImpl): AutocompletesRepository {
        return repository
    }

    @Provides
    fun providesAutocompletesRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AutocompletesRepositoryImpl {
        return AutocompletesRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditAutocompleteRepository(
        repository: AddEditAutocompleteRepositoryImpl
    ): AddEditAutocompleteRepository {
        return repository
    }

    @Provides
    fun providesAddEditAutocompleteRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditAutocompleteRepositoryImpl {
        return AddEditAutocompleteRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesSettingsRepository(repository: SettingsRepositoryImpl): SettingsRepository {
        return repository
    }

    @Provides
    fun providesSettingsRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesEditCurrencySymbolRepository(
        repository: EditCurrencySymbolRepositoryImpl
    ): EditCurrencySymbolRepository {
        return repository
    }

    @Provides
    fun providesEditCurrencySymbolRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ) : EditCurrencySymbolRepositoryImpl {
        return EditCurrencySymbolRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesEditTaxRateRepository(repository: EditTaxRateRepositoryImpl): EditTaxRateRepository {
        return repository
    }

    @Provides
    fun providesEditTaxRateRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditTaxRateRepositoryImpl {
        return EditTaxRateRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesArchiveRepository(repository: ArchiveRepositoryImpl): ArchiveRepository {
        return repository
    }

    @Provides
    fun providesArchiveRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ArchiveRepositoryImpl {
        return ArchiveRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesTrashRepository(repository: TrashRepositoryImpl): TrashRepository {
        return repository
    }

    @Provides
    fun providesTrashRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): TrashRepositoryImpl {
        return TrashRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesCopyProductRepository(repository: CopyProductRepositoryImpl): CopyProductRepository {
        return repository
    }

    @Provides
    fun providesCopyProductRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CopyProductRepositoryImpl {
        return CopyProductRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesMoveProductRepository(repository: MoveProductRepositoryImpl): MoveProductRepository {
        return repository
    }

    @Provides
    fun providesMoveProductRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MoveProductRepositoryImpl {
        return MoveProductRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesMainRepository(repository: MainRepositoryImpl): MainRepository {
        return repository
    }

    @Provides
    fun providesMainRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MainRepositoryImpl {
        return MainRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesPurchasesNotificationRepository(
        repository: PurchasesNotificationRepositoryImpl
    ): PurchasesNotificationRepository {
        return repository
    }

    @Provides
    fun providesPurchasesNotificationRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesNotificationRepositoryImpl {
        return PurchasesNotificationRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesEditReminderRepository(repository: EditReminderRepositoryImpl): EditReminderRepository {
        return repository
    }

    @Provides
    fun providesEditReminderRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditReminderRepositoryImpl {
        return EditReminderRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesEditShoppingListNameRepository(
        repository: EditShoppingListNameRepositoryImpl
    ): EditShoppingListNameRepository {
        return repository
    }

    @Provides
    fun providesEditShoppingListNameRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListNameRepositoryImpl {
        return EditShoppingListNameRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesEditShoppingListTotalRepository(
        repository: EditShoppingListTotalRepositoryImpl
    ): EditShoppingListTotalRepository {
        return repository
    }

    @Provides
    fun providesEditShoppingListTotalRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListTotalRepositoryImpl {
        return EditShoppingListTotalRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesCalculateChangeRepository(
        repository: CalculateChangeRepositoryImpl
    ): CalculateChangeRepository {
        return repository
    }

    @Provides
    fun providesCalculateChangeRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CalculateChangeRepositoryImpl {
        return CalculateChangeRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesBackupRepository(repository: BackupRepositoryImpl): BackupRepository {
        return repository
    }

    @Provides
    fun providesBackupRepositoryImpl(
        localDatasource: LocalDatasource,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): BackupRepositoryImpl {
        return BackupRepositoryImpl(localDatasource, mapping, dispatchers)
    }

    @Provides
    fun providesPurchasesAlarmManager(
        @ApplicationContext context: Context
    ): PurchasesAlarmManager {
        return PurchasesAlarmManager(context)
    }

    @Provides
    fun providesPurchasesNotificationManager(
        @ApplicationContext context: Context
    ): PurchasesNotificationManager {
        return PurchasesNotificationManager(context)
    }

    @Provides
    fun providesBackupMediaStore(@ApplicationContext context: Context): BackupMediaStore {
        return BackupMediaStore(context)
    }
}