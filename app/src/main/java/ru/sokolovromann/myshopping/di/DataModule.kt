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
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatabase
import ru.sokolovromann.myshopping.data.local.resources.AddEditProductsResources
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
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
    fun providesLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return LocalDatabase.build(context)
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
}