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
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
import ru.sokolovromann.myshopping.data.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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
    fun providesPurchasesNotificationPreferencesDao(localDataStore: LocalDataStore): PurchasesNotificationPreferencesDao {
        return PurchasesNotificationPreferencesDao(localDataStore)
    }

    @Provides
    fun providesPurchasesPreferencesDao(localDataStore: LocalDataStore): PurchasesPreferencesDao {
        return PurchasesPreferencesDao(localDataStore)
    }

    @Provides
    fun providesSettingsDao(localDataStore: LocalDataStore): SettingsDao {
        return SettingsDao(localDataStore)
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
        purchasesDao: PurchasesDao,
        preferencesDao: PurchasesPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesRepositoryImpl {
        return PurchasesRepositoryImpl(purchasesDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesProductsRepository(repository: ProductsRepositoryImpl): ProductsRepository {
        return repository
    }

    @Provides
    fun providesProductsRepositoryImpl(
        productsDao: ProductsDao,
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ProductsRepositoryImpl {
        return ProductsRepositoryImpl(productsDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditProductRepository(repository: AddEditProductRepository): AddEditProductRepository {
        return repository
    }

    @Provides
    fun providesAddEditProductRepositoryImpl(
        productDao: AddEditProductDao,
        preferencesDao: AddEditProductPreferencesDao,
        resources: AddEditProductsResources,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditProductRepositoryImpl {
        return AddEditProductRepositoryImpl(productDao, resources, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesAutocompletesRepository(repository: AutocompletesRepositoryImpl): AutocompletesRepository {
        return repository
    }

    @Provides
    fun providesAutocompletesRepositoryImpl(
        autocompletesDao: AutocompletesDao,
        preferencesDao: AutocompletesPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AutocompletesRepositoryImpl {
        return AutocompletesRepositoryImpl(autocompletesDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesAddEditAutocompleteRepository(
        repository: AddEditAutocompleteRepositoryImpl
    ): AddEditAutocompleteRepository {
        return repository
    }

    @Provides
    fun providesAddEditAutocompleteRepositoryImpl(
        autocompleteDao: AddEditAutocompleteDao,
        preferencesDao: AddEditAutocompletePreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): AddEditAutocompleteRepositoryImpl {
        return AddEditAutocompleteRepositoryImpl(autocompleteDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesSettingsRepository(repository: SettingsRepositoryImpl): SettingsRepository {
        return repository
    }

    @Provides
    fun providesSettingsRepositoryImpl(
        settingsDao: SettingsDao,
        resources: SettingsResources,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(settingsDao, resources, mapping, dispatchers)
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
        archiveDao: ArchiveDao,
        preferencesDao: ArchivePreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): ArchiveRepositoryImpl {
        return ArchiveRepositoryImpl(archiveDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesTrashRepository(repository: TrashRepositoryImpl): TrashRepository {
        return repository
    }

    @Provides
    fun providesTrashRepositoryImpl(
        trashDao: TrashDao,
        preferencesDao: TrashPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): TrashRepositoryImpl {
        return TrashRepositoryImpl(trashDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesCopyProductRepository(repository: CopyProductRepositoryImpl): CopyProductRepository {
        return repository
    }

    @Provides
    fun providesCopyProductRepositoryImpl(
        productDao: CopyProductDao,
        preferencesDao: CopyProductPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CopyProductRepositoryImpl {
        return CopyProductRepositoryImpl(productDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesMoveProductRepository(repository: MoveProductRepositoryImpl): MoveProductRepository {
        return repository
    }

    @Provides
    fun providesMoveProductRepositoryImpl(
        productDao: MoveProductDao,
        preferencesDao: MoveProductPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MoveProductRepositoryImpl {
        return MoveProductRepositoryImpl(productDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesMainRepository(repository: MainRepositoryImpl): MainRepository {
        return repository
    }

    @Provides
    fun providesMainRepositoryImpl(
        preferencesDao: MainPreferencesDao,
        resources: MainResources,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): MainRepositoryImpl {
        return MainRepositoryImpl(preferencesDao, resources, mapping, dispatchers)
    }

    @Provides
    fun providesPurchasesNotificationRepository(
        repository: PurchasesNotificationRepositoryImpl
    ): PurchasesNotificationRepository {
        return repository
    }

    @Provides
    fun providesPurchasesNotificationRepositoryImpl(
        notificationDao: PurchasesNotificationDao,
        preferencesDao: PurchasesNotificationPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): PurchasesNotificationRepositoryImpl {
        return PurchasesNotificationRepositoryImpl(notificationDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditReminderRepository(repository: EditReminderRepositoryImpl): EditReminderRepository {
        return repository
    }

    @Provides
    fun providesEditReminderRepositoryImpl(
        reminderDao: EditReminderDao,
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditReminderRepositoryImpl {
        return EditReminderRepositoryImpl(reminderDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesEditShoppingListNameRepository(
        repository: EditShoppingListNameRepositoryImpl
    ): EditShoppingListNameRepository {
        return repository
    }

    @Provides
    fun providesEditShoppingListNameRepositoryImpl(
        shoppingListDao: EditShoppingListNameDao,
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): EditShoppingListNameRepositoryImpl {
        return EditShoppingListNameRepositoryImpl(shoppingListDao, preferencesDao, mapping, dispatchers)
    }

    @Provides
    fun providesCalculateChangeRepository(
        repository: CalculateChangeRepositoryImpl
    ): CalculateChangeRepository {
        return repository
    }

    @Provides
    fun providesCalculateChangeRepositoryImpl(
        calculateChangeDao: CalculateChangeDao,
        preferencesDao: ProductsPreferencesDao,
        mapping: RepositoryMapping,
        dispatchers: AppDispatchers
    ): CalculateChangeRepositoryImpl {
        return CalculateChangeRepositoryImpl(calculateChangeDao, preferencesDao, mapping, dispatchers)
    }
}