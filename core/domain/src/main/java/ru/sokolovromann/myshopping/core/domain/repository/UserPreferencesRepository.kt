package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.UserPreferences

interface UserPreferencesRepository {

    fun observeUserPreferences(): Flow<UserPreferences>

    fun observeGeneralPreferences(): Flow<GeneralPreferences>

    fun observeCartsPreferences(): Flow<CartsPreferences>

    fun observeProductsPreferences(): Flow<ProductsPreferences>

    fun observeProductsWidgetPreferences(): Flow<ProductsWidgetPreferences>

    fun observeAddEditProductPreferences(): Flow<AddEditProductPreferences>

    fun observeSuggestionsPreferences(): Flow<SuggestionsPreferences>

    fun observeBackupPreferences(): Flow<BackupPreferences>

    suspend fun updateGeneralPreferences(preferences: GeneralPreferences)

    suspend fun updateCartsPreferences(preferences: CartsPreferences)

    suspend fun updateProductsPreferences(preferences: ProductsPreferences)

    suspend fun updateProductsWidgetPreferences(preferences: ProductsWidgetPreferences)

    suspend fun updateAddEditProductPreferences(preferences: AddEditProductPreferences)

    suspend fun updateSuggestionsPreferences(preferences: SuggestionsPreferences)

    suspend fun updateBackupPreferences(preferences: BackupPreferences)
}