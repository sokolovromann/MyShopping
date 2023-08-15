package ru.sokolovromann.myshopping.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.AppBase64
import ru.sokolovromann.myshopping.data.AppJson
import ru.sokolovromann.myshopping.data.local.dao.*
import ru.sokolovromann.myshopping.data.local.datasource.LocalAppConfigDatasource
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatabase
import ru.sokolovromann.myshopping.data.local.datasource.CodeVersion14LocalDatabase
import ru.sokolovromann.myshopping.data.local.files.BackupFiles
import ru.sokolovromann.myshopping.data.local.resources.AddEditProductsResources
import ru.sokolovromann.myshopping.data.local.resources.AutocompletesResources
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
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
    fun providesLocalAppConfigDatasource(
        @ApplicationContext context: Context
    ): LocalAppConfigDatasource {
        return LocalAppConfigDatasource(context)
    }

    @Singleton
    @Provides
    fun providesAppConfigDao(datasource: LocalAppConfigDatasource, dispatchers: AppDispatchers): AppConfigDao {
        return AppConfigDao(datasource, dispatchers)
    }

    @Singleton
    @Provides
    fun providesLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return LocalDatabase.build(context)
    }

    @Singleton
    @Provides
    fun providesCodeVersion14LocalDatabase(@ApplicationContext context: Context): CodeVersion14LocalDatabase {
        return CodeVersion14LocalDatabase(context)
    }

    @Singleton
    @Provides
    fun providesAppDispatchers(): AppDispatchers {
        return AppDispatchers()
    }

    @Provides
    fun providesResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun providesRepositoryMapping(): RepositoryMapping {
        return RepositoryMapping()
    }

    @Provides
    fun providesAddEditProductsResources(resources: Resources): AddEditProductsResources {
        return AddEditProductsResources(resources)
    }

    @Provides
    fun providesAutocompletesResources(resources: Resources): AutocompletesResources {
        return AutocompletesResources(resources)
    }

    @Provides
    fun providesSettingsResources(resources: Resources): SettingsResources {
        return SettingsResources(resources)
    }

    @Provides
    fun providesMainResources(resources: Resources): MainResources {
        return MainResources(resources)
    }

    @Provides
    fun providesPurchasesRepository(repository: PurchasesRepositoryImpl): PurchasesRepository {
        return repository
    }

    @Provides
    fun providesPurchasesRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesRepositoryImpl {
        return PurchasesRepositoryImpl(localDatabase.purchasesDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesProductsRepository(repository: ProductsRepositoryImpl): ProductsRepository {
        return repository
    }

    @Provides
    fun providesProductsRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsRepositoryImpl {
        return ProductsRepositoryImpl(localDatabase.productsDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesProductsWidgetRepository(
        repository: ProductsWidgetRepositoryImpl
    ): ProductsWidgetRepository {
        return repository
    }

    @Provides
    fun providesProductsWidgetRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsWidgetRepositoryImpl {
        return ProductsWidgetRepositoryImpl(localDatabase.productsWidgetDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditProductRepository(repository: AddEditProductRepositoryImpl): AddEditProductRepository {
        return repository
    }

    @Provides
    fun providesAddEditProductRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        resources: AddEditProductsResources,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditProductRepositoryImpl {
        return AddEditProductRepositoryImpl(localDatabase.addEditProductDao(), resources, appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesAutocompletesRepository(repository: AutocompletesRepositoryImpl): AutocompletesRepository {
        return repository
    }

    @Provides
    fun providesAutocompletesRepositoryImpl(
        localDatabase: LocalDatabase,
        resources: AutocompletesResources,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AutocompletesRepositoryImpl {
        return AutocompletesRepositoryImpl(localDatabase.autocompletesDao(), resources, appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditAutocompleteRepository(
        repository: AddEditAutocompleteRepositoryImpl
    ): AddEditAutocompleteRepository {
        return repository
    }

    @Provides
    fun providesAddEditAutocompleteRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditAutocompleteRepositoryImpl {
        return AddEditAutocompleteRepositoryImpl(localDatabase.addEditAutocompleteDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesSettingsRepository(repository: SettingsRepositoryImpl): SettingsRepository {
        return repository
    }

    @Provides
    fun providesSettingsRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        settingsResources: SettingsResources,
        autocompletesResources: AutocompletesResources,
        codeVersion14LocalDatabase: CodeVersion14LocalDatabase,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(localDatabase.settingsDao(),appConfigDao, settingsResources,
            autocompletesResources, codeVersion14LocalDatabase, mapping, dispatchers)
    }

    @Provides
    fun providesEditCurrencySymbolRepository(
        repository: EditCurrencySymbolRepositoryImpl
    ): EditCurrencySymbolRepository {
        return repository
    }

    @Provides
    fun providesEditCurrencySymbolRepositoryImpl(
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ) : EditCurrencySymbolRepositoryImpl {
        return EditCurrencySymbolRepositoryImpl(appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditTaxRateRepository(repository: EditTaxRateRepositoryImpl): EditTaxRateRepository {
        return repository
    }

    @Provides
    fun providesEditTaxRateRepositoryImpl(
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditTaxRateRepositoryImpl {
        return EditTaxRateRepositoryImpl(appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesArchiveRepository(repository: ArchiveRepositoryImpl): ArchiveRepository {
        return repository
    }

    @Provides
    fun providesArchiveRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ArchiveRepositoryImpl {
        return ArchiveRepositoryImpl(localDatabase.archiveDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesTrashRepository(repository: TrashRepositoryImpl): TrashRepository {
        return repository
    }

    @Provides
    fun providesTrashRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): TrashRepositoryImpl {
        return TrashRepositoryImpl(localDatabase.trashDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesCopyProductRepository(repository: CopyProductRepositoryImpl): CopyProductRepository {
        return repository
    }

    @Provides
    fun providesCopyProductRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CopyProductRepositoryImpl {
        return CopyProductRepositoryImpl(localDatabase.copyProductDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesMoveProductRepository(repository: MoveProductRepositoryImpl): MoveProductRepository {
        return repository
    }

    @Provides
    fun providesMoveProductRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MoveProductRepositoryImpl {
        return MoveProductRepositoryImpl(localDatabase.moveProductDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesMainRepository(repository: MainRepositoryImpl): MainRepository {
        return repository
    }

    @Provides
    fun providesMainRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mainResources: MainResources,
        autocompletesResources: AutocompletesResources,
        codeVersion14LocalDatabase: CodeVersion14LocalDatabase,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MainRepositoryImpl {
        return MainRepositoryImpl(localDatabase.mainDao(), appConfigDao, mainResources,
            autocompletesResources, codeVersion14LocalDatabase, mapping, dispatchers)
    }

    @Provides
    fun providesPurchasesNotificationRepository(
        repository: PurchasesNotificationRepositoryImpl
    ): PurchasesNotificationRepository {
        return repository
    }

    @Provides
    fun providesPurchasesNotificationRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesNotificationRepositoryImpl {
        return PurchasesNotificationRepositoryImpl(localDatabase.purchasesNotificationDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditReminderRepository(repository: EditReminderRepositoryImpl): EditReminderRepository {
        return repository
    }

    @Provides
    fun providesEditReminderRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditReminderRepositoryImpl {
        return EditReminderRepositoryImpl(localDatabase.editReminderDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditShoppingListNameRepository(
        repository: EditShoppingListNameRepositoryImpl
    ): EditShoppingListNameRepository {
        return repository
    }

    @Provides
    fun providesEditShoppingListNameRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListNameRepositoryImpl {
        return EditShoppingListNameRepositoryImpl(localDatabase.editShoppingListNameDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditShoppingListTotalRepository(
        repository: EditShoppingListTotalRepositoryImpl
    ): EditShoppingListTotalRepository {
        return repository
    }

    @Provides
    fun providesEditShoppingListTotalRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListTotalRepositoryImpl {
        return EditShoppingListTotalRepositoryImpl(localDatabase.editShoppingListTotalDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesCalculateChangeRepository(
        repository: CalculateChangeRepositoryImpl
    ): CalculateChangeRepository {
        return repository
    }

    @Provides
    fun providesCalculateChangeRepositoryImpl(
        localDatabase: LocalDatabase,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CalculateChangeRepositoryImpl {
        return CalculateChangeRepositoryImpl(localDatabase.calculateChangeDao(), appConfigDao, mapping, dispatchers)
    }

    @Provides
    fun providesBackupRepository(repository: BackupRepositoryImpl): BackupRepository {
        return repository
    }

    @Provides
    fun providesBackupRepositoryImpl(
        localDatabase: LocalDatabase,
        files: BackupFiles,
        appConfigDao: AppConfigDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): BackupRepositoryImpl {
        return BackupRepositoryImpl(localDatabase.backupDao(), files, appConfigDao, mapping, dispatchers)
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
    fun providesBackupFiles(
        @ApplicationContext context: Context,
        json: AppJson,
        base64: AppBase64,
        dispatchers: AppDispatchers
    ): BackupFiles {
        return BackupFiles(context, json, base64, dispatchers)
    }

    @Provides
    fun providesBackupMediaStore(@ApplicationContext context: Context): BackupMediaStore {
        return BackupMediaStore(context)
    }

    @Provides
    fun providesAppJson(): AppJson {
        return AppJson()
    }

    @Provides
    fun providesAppBase64(): AppBase64 {
        return AppBase64()
    }
}