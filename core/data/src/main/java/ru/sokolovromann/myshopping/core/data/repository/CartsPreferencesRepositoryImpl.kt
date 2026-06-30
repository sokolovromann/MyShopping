package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.di.CartsPreferencesDataStore
import ru.sokolovromann.myshopping.core.data.mapper.CartsPreferencesMapper
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.CartsPreferencesRepository

class CartsPreferencesRepositoryImpl @Inject constructor(
    @CartsPreferencesDataStore private val cartsPreferencesDataStore: DataStore<Preferences>,
    private val cartsPreferencesMapper: CartsPreferencesMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CartsPreferencesRepository {

    override fun observeCartsPreferences(): Flow<CartsPreferences> =
        cartsPreferencesDataStore.data
            .map { cartsPreferencesMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateCartsPreferences(preferences: CartsPreferences): Unit =
        withContext(ioDispatcher) {
            cartsPreferencesDataStore.edit {
                val newPreferences = cartsPreferencesMapper.toPreferences(preferences)
                it.plusAssign(newPreferences)
            }
        }
}