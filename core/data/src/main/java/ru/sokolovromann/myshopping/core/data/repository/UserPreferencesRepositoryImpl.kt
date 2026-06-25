package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStore
import ru.sokolovromann.myshopping.core.data.mapper.AddEditProductPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.BackupPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.CartsPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.GeneralPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.ProductsPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.ProductsWidgetPreferencesMapper
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.UserPreferences
import ru.sokolovromann.myshopping.core.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val localDataStore: LocalDataStore,
    private val generalPreferencesMapper: GeneralPreferencesMapper,
    private val cartsPreferencesMapper: CartsPreferencesMapper,
    private val productsPreferencesMapper: ProductsPreferencesMapper,
    private val productsWidgetPreferencesMapper: ProductsWidgetPreferencesMapper,
    private val addEditProductPreferencesMapper: AddEditProductPreferencesMapper,
    private val suggestionsPreferencesMapper: SuggestionsPreferencesMapper,
    private val backupPreferencesMapper: BackupPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserPreferencesRepository {

    override fun observeUserPreferences(): Flow<UserPreferences> = combine(
        flows = arrayOf(
            observeGeneralPreferences(),
            observeCartsPreferences(),
            observeProductsPreferences(),
            observeProductsWidgetPreferences(),
            observeAddEditProductPreferences(),
            observeSuggestionsPreferences(),
            observeBackupPreferences(),
        ),
        transform = { toUserPreferences(it) }
    ).flowOn(ioDispatcher)

    override fun observeGeneralPreferences(): Flow<GeneralPreferences> =
        localDataStore.getGeneralPreferencesDataStore().data
            .map { generalPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override fun observeCartsPreferences(): Flow<CartsPreferences> =
        localDataStore.getCartsPreferencesDataStore().data
            .map { cartsPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override fun observeProductsPreferences(): Flow<ProductsPreferences> =
        localDataStore.getProductsPreferencesDataStore().data
            .map { productsPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override fun observeProductsWidgetPreferences(): Flow<ProductsWidgetPreferences> =
        localDataStore.getProductsWidgetPreferencesDataStore().data
            .map { productsWidgetPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override fun observeAddEditProductPreferences(): Flow<AddEditProductPreferences> =
        localDataStore.getAddEditProductPreferencesDataStore().data
            .map { addEditProductPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override fun observeSuggestionsPreferences(): Flow<SuggestionsPreferences> =
        localDataStore.getSuggestionsPreferencesDataStore().data
            .map { suggestionsPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override fun observeBackupPreferences(): Flow<BackupPreferences> =
        localDataStore.getBackupPreferencesDataStore().data
            .map { backupPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateGeneralPreferences(preferences: GeneralPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getGeneralPreferencesDataStore().edit {
                val newPreferences = generalPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    override suspend fun updateCartsPreferences(preferences: CartsPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getCartsPreferencesDataStore().edit {
                val newPreferences = cartsPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    override suspend fun updateProductsPreferences(preferences: ProductsPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getProductsPreferencesDataStore().edit {
                val newPreferences = productsPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    override suspend fun updateProductsWidgetPreferences(preferences: ProductsWidgetPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getProductsWidgetPreferencesDataStore().edit {
                val newPreferences = productsWidgetPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    override suspend fun updateAddEditProductPreferences(preferences: AddEditProductPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getAddEditProductPreferencesDataStore().edit {
                val newPreferences = addEditProductPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    override suspend fun updateSuggestionsPreferences(preferences: SuggestionsPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getSuggestionsPreferencesDataStore().edit {
                val newPreferences = suggestionsPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    override suspend fun updateBackupPreferences(preferences: BackupPreferences): Unit =
        withContext(ioDispatcher) {
            localDataStore.getBackupPreferencesDataStore().edit {
                val newPreferences = backupPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }

    private fun toUserPreferences(array: Array<Any>) = UserPreferences(
        array[0] as GeneralPreferences,
        array[1] as CartsPreferences,
        array[2] as ProductsPreferences,
        array[3] as ProductsWidgetPreferences,
        array[4] as AddEditProductPreferences,
        array[5] as SuggestionsPreferences,
        array[6] as BackupPreferences
    )
}