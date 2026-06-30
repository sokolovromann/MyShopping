package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences

interface CartsPreferencesRepository {

    fun observeCartsPreferences(): Flow<CartsPreferences>

    suspend fun updateCartsPreferences(preferences: CartsPreferences)
}