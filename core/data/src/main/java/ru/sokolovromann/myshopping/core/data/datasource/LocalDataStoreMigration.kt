package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.R
import ru.sokolovromann.myshopping.core.data.mapper.AddEditProductPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.BackupPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.CartsPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.GeneralPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.ProductsPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.ProductsWidgetPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.UserConfigMapper
import ru.sokolovromann.myshopping.core.data.old.Api15DataStoreMigrationManager
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.UserConfig
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo

abstract class LocalDataStoreMigration(
    private val migrationManager: Api15DataStoreMigrationManager,
    private val ioDispatcher: CoroutineDispatcher
) : DataMigration<Preferences> {

    abstract suspend fun migrateFromApi15(): Preferences

    abstract suspend fun addDefault(): Preferences

    override suspend fun cleanUp(): Unit =
        withContext(ioDispatcher) {}

    override suspend fun migrate(currentData: Preferences): Preferences =
        withContext(ioDispatcher) {
            if (migrationManager.exists()) {
                migrateFromApi15()
            } else {
                addDefault()
            }
        }

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        withContext(ioDispatcher) {
            val isCurrentEmpty = currentData.asMap().isEmpty()
            val api15Exists = migrationManager.exists() && isCurrentEmpty
            api15Exists || isCurrentEmpty
        }
}

class GeneralPreferencesMigration @Inject constructor(
    @ApplicationContext private val context: Context,
    private val generalPreferencesMapper: GeneralPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getGeneralPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) { getDefaultPreferences() }

    private suspend fun getDefaultPreferences(): Preferences =
        withContext(ioDispatcher) {
            val dateTimeFormattingMode = generalPreferencesMapper.toDateTimeFormattingMode(
                formattingMode = context.resources.getString(R.string.data_default_date_time_formatting_mode),
                is24HourFormat = context.resources.getString(R.string.data_default_is_24_hour_time_format)
            )
            val currency = generalPreferencesMapper.toCurrency(
                sign = context.resources.getString(R.string.data_default_currency),
                displaySide = context.resources.getString(R.string.data_default_currency_display_side)
            )
            val default = UserPreferencesDefaults.General.copy(
                dateTimeFormattingMode = dateTimeFormattingMode,
                currency = currency
            )
            generalPreferencesMapper.toPreferences(default)
        }
}

class CartsPreferencesMigration @Inject constructor(
    private val cartsPreferencesMapper: CartsPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getCartsPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            cartsPreferencesMapper.toPreferences(UserPreferencesDefaults.Carts)
        }
}

class ProductsPreferencesMigration @Inject constructor(
    private val productsPreferencesMapper: ProductsPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getProductsPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            productsPreferencesMapper.toPreferences(UserPreferencesDefaults.Products)
        }
}

class ProductsWidgetPreferencesMigration @Inject constructor(
    private val productsWidgetPreferencesMapper: ProductsWidgetPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getProductsWidgetPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            productsWidgetPreferencesMapper.toPreferences(UserPreferencesDefaults.ProductsWidget)
        }
}

class AddEditProductPreferencesMigration @Inject constructor(
    private val addEditProductPreferencesMapper: AddEditProductPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getAddEditProductPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            addEditProductPreferencesMapper.toPreferences(UserPreferencesDefaults.AddEditProduct)
        }
}

class SuggestionsPreferencesMigration @Inject constructor(
    private val suggestionsPreferencesMapper: SuggestionsPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getSuggestionsPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            suggestionsPreferencesMapper.toPreferences(UserPreferencesDefaults.Suggestions)
        }
}

class BackupPreferencesMigration @Inject constructor(
    private val backupPreferencesMapper: BackupPreferencesMapper,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getBackupPreferences() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            backupPreferencesMapper.toPreferences(UserPreferencesDefaults.Backup)
        }
}

class UserConfigMigration @Inject constructor(
    private val userConfigMapper: UserConfigMapper,
    private val buildInfo: BuildInfo,
    private val migrationManager: Api15DataStoreMigrationManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalDataStoreMigration(migrationManager, ioDispatcher) {

    override suspend fun migrateFromApi15(): Preferences =
        withContext(ioDispatcher) { migrationManager.getUserConfig() }

    override suspend fun addDefault(): Preferences =
        withContext(ioDispatcher) {
            val userConfig = UserConfig(buildInfo.getApi())
            userConfigMapper.toPreferences(userConfig)
        }
}