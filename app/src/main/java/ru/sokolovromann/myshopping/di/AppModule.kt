package ru.sokolovromann.myshopping.di

import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.*
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalDatabase
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalPreferences
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatabase
import ru.sokolovromann.myshopping.data.local.resources.AddEditProductsResources
import ru.sokolovromann.myshopping.data.local.resources.AutocompletesResources
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
import ru.sokolovromann.myshopping.data.repository.*
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = LocalDataStore.DATASTORE_NAME
    )

    @Singleton
    @Provides
    fun providesLocalDataStore(@ApplicationContext context: Context, dispatchers: AppDispatchers): LocalDataStore {
        return LocalDataStore(context.dataStore, dispatchers)
    }

    @Singleton
    @Provides
    fun providesAppVersion14LocalPreferences(@ApplicationContext context: Context): AppVersion14LocalPreferences {
        return AppVersion14LocalPreferences(context)
    }

    @Singleton
    @Provides
    fun providesLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return LocalDatabase.build(context)
    }

    @Singleton
    @Provides
    fun providesAppVersion14LocalDatabase(@ApplicationContext context: Context): AppVersion14LocalDatabase {
        return AppVersion14LocalDatabase(context)
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
    fun providesAddEditAutocompletePreferencesDao(localDataStore: LocalDataStore): AddEditAutocompletePreferencesDao {
        return AddEditAutocompletePreferencesDao(localDataStore)
    }

    @Provides
    fun providesAddEditProductPreferencesDao(localDataStore: LocalDataStore): AddEditProductPreferencesDao {
        return AddEditProductPreferencesDao(localDataStore)
    }

    @Provides
    fun providesArchivePreferencesDao(localDataStore: LocalDataStore): ArchivePreferencesDao {
        return ArchivePreferencesDao(localDataStore)
    }

    @Provides
    fun providesAutocompletesPreferencesDao(localDataStore: LocalDataStore): AutocompletesPreferencesDao {
        return AutocompletesPreferencesDao(localDataStore)
    }

    @Provides
    fun providesCopyProductPreferencesDao(localDataStore: LocalDataStore): CopyProductPreferencesDao {
        return CopyProductPreferencesDao(localDataStore)
    }

    @Provides
    fun providesEditCurrencySymbolDao(localDataStore: LocalDataStore): EditCurrencySymbolDao {
        return EditCurrencySymbolDao(localDataStore)
    }

    @Provides
    fun providesEditTaxRateDao(localDataStore: LocalDataStore): EditTaxRateDao {
        return EditTaxRateDao(localDataStore)
    }

    @Provides
    fun providesMainPreferencesDao(localDataStore: LocalDataStore): MainPreferencesDao {
        return MainPreferencesDao(localDataStore)
    }

    @Provides
    fun providesMoveProductPreferencesDao(localDataStore: LocalDataStore): MoveProductPreferencesDao {
        return MoveProductPreferencesDao(localDataStore)
    }

    @Provides
    fun providesProductsPreferencesDao(localDataStore: LocalDataStore): ProductsPreferencesDao {
        return ProductsPreferencesDao(localDataStore)
    }

    @Provides
    fun providesProductsWidgetsPreferencesDao(localDataStore: LocalDataStore): ProductsWidgetPreferencesDao {
        return ProductsWidgetPreferencesDao(localDataStore)
    }

    @Provides
    fun providesPurchasesNotificationPreferencesDao(localDataStore: LocalDataStore): PurchasesNotificationPreferencesDao {
        return PurchasesNotificationPreferencesDao(localDataStore)
    }

    @Provides
    fun providesPurchasesPreferencesDao(localDataStore: LocalDataStore): PurchasesPreferencesDao {
        return PurchasesPreferencesDao(localDataStore)
    }

    @Provides
    fun providesSettingsPreferencesDao(localDataStore: LocalDataStore): SettingsPreferencesDao {
        return SettingsPreferencesDao(localDataStore)
    }

    @Provides
    fun providesTrashPreferencesDao(localDataStore: LocalDataStore): TrashPreferencesDao {
        return TrashPreferencesDao(localDataStore)
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
        preferencesDao: PurchasesPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesRepositoryImpl {
        return PurchasesRepositoryImpl(localDatabase.purchasesDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesProductsRepository(repository: ProductsRepositoryImpl): ProductsRepository {
        return repository
    }

    @Provides
    fun providesProductsRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsRepositoryImpl {
        return ProductsRepositoryImpl(localDatabase.productsDao(), preferencesDao, mapping, dispatchers)
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
        preferencesDao: ProductsWidgetPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsWidgetRepositoryImpl {
        return ProductsWidgetRepositoryImpl(localDatabase.productsWidgetDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditProductRepository(repository: AddEditProductRepositoryImpl): AddEditProductRepository {
        return repository
    }

    @Provides
    fun providesAddEditProductRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: AddEditProductPreferencesDao,
        resources: AddEditProductsResources,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditProductRepositoryImpl {
        return AddEditProductRepositoryImpl(localDatabase.addEditProductDao(), resources, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesAutocompletesRepository(repository: AutocompletesRepositoryImpl): AutocompletesRepository {
        return repository
    }

    @Provides
    fun providesAutocompletesRepositoryImpl(
        localDatabase: LocalDatabase,
        resources: AutocompletesResources,
        preferencesDao: AutocompletesPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AutocompletesRepositoryImpl {
        return AutocompletesRepositoryImpl(localDatabase.autocompletesDao(), resources, preferencesDao, mapping, dispatchers)
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
        preferencesDao: AddEditAutocompletePreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditAutocompleteRepositoryImpl {
        return AddEditAutocompleteRepositoryImpl(localDatabase.addEditAutocompleteDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesSettingsRepository(repository: SettingsRepositoryImpl): SettingsRepository {
        return repository
    }

    @Provides
    fun providesSettingsRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: SettingsPreferencesDao,
        settingsResources: SettingsResources,
        autocompletesResources: AutocompletesResources,
        appVersion14LocalDatabase: AppVersion14LocalDatabase,
        appVersion14Preferences: AppVersion14LocalPreferences,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(localDatabase.settingsDao(),preferencesDao, settingsResources,
            autocompletesResources, appVersion14LocalDatabase, appVersion14Preferences, mapping, dispatchers)
    }

    @Provides
    fun providesEditCurrencySymbolRepository(
        repository: EditCurrencySymbolRepositoryImpl
    ): EditCurrencySymbolRepository {
        return repository
    }

    @Provides
    fun providesEditCurrencySymbolRepositoryImpl(
        currencyDao: EditCurrencySymbolDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ) : EditCurrencySymbolRepositoryImpl {
        return EditCurrencySymbolRepositoryImpl(currencyDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditTaxRateRepository(repository: EditTaxRateRepositoryImpl): EditTaxRateRepository {
        return repository
    }

    @Provides
    fun providesEditTaxRateRepositoryImpl(
        taxRateDao: EditTaxRateDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditTaxRateRepositoryImpl {
        return EditTaxRateRepositoryImpl(taxRateDao, mapping, dispatchers)
    }

    @Provides
    fun providesArchiveRepository(repository: ArchiveRepositoryImpl): ArchiveRepository {
        return repository
    }

    @Provides
    fun providesArchiveRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: ArchivePreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ArchiveRepositoryImpl {
        return ArchiveRepositoryImpl(localDatabase.archiveDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesTrashRepository(repository: TrashRepositoryImpl): TrashRepository {
        return repository
    }

    @Provides
    fun providesTrashRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: TrashPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): TrashRepositoryImpl {
        return TrashRepositoryImpl(localDatabase.trashDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesCopyProductRepository(repository: CopyProductRepositoryImpl): CopyProductRepository {
        return repository
    }

    @Provides
    fun providesCopyProductRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: CopyProductPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CopyProductRepositoryImpl {
        return CopyProductRepositoryImpl(localDatabase.copyProductDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesMoveProductRepository(repository: MoveProductRepositoryImpl): MoveProductRepository {
        return repository
    }

    @Provides
    fun providesMoveProductRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: MoveProductPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MoveProductRepositoryImpl {
        return MoveProductRepositoryImpl(localDatabase.moveProductDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesMainRepository(repository: MainRepositoryImpl): MainRepository {
        return repository
    }

    @Provides
    fun providesMainRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: MainPreferencesDao,
        mainResources: MainResources,
        autocompletesResources: AutocompletesResources,
        appVersion14LocalDatabase: AppVersion14LocalDatabase,
        appVersion14Preferences: AppVersion14LocalPreferences,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MainRepositoryImpl {
        return MainRepositoryImpl(localDatabase.mainDao(), preferencesDao, mainResources,
            autocompletesResources, appVersion14LocalDatabase, appVersion14Preferences, mapping, dispatchers)
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
        preferencesDao: PurchasesNotificationPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesNotificationRepositoryImpl {
        return PurchasesNotificationRepositoryImpl(localDatabase.purchasesNotificationDao(), preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditReminderRepository(repository: EditReminderRepositoryImpl): EditReminderRepository {
        return repository
    }

    @Provides
    fun providesEditReminderRepositoryImpl(
        localDatabase: LocalDatabase,
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditReminderRepositoryImpl {
        return EditReminderRepositoryImpl(localDatabase.editReminderDao(), preferencesDao, mapping, dispatchers)
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
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListNameRepositoryImpl {
        return EditShoppingListNameRepositoryImpl(localDatabase.editShoppingListNameDao(), preferencesDao, mapping, dispatchers)
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
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListTotalRepositoryImpl {
        return EditShoppingListTotalRepositoryImpl(localDatabase.editShoppingListTotalDao(), preferencesDao, mapping, dispatchers)
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
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CalculateChangeRepositoryImpl {
        return CalculateChangeRepositoryImpl(localDatabase.calculateChangeDao(), preferencesDao, mapping, dispatchers)
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
}