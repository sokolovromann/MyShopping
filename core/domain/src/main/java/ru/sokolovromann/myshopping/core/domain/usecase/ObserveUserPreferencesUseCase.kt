package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.UserPreferences

class ObserveUserPreferencesUseCase @Inject constructor(
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    private val observeCartsPreferencesUseCase: ObserveCartsPreferencesUseCase,
    private val observeProductsPreferencesUseCase: ObserveProductsPreferencesUseCase,
    private val observeProductsWidgetPreferencesUseCase: ObserveProductsWidgetPreferencesUseCase,
    private val observeAddEditProductPreferencesUseCase: ObserveAddEditProductPreferencesUseCase,
    private val suggestionsPreferencesUseCase: ObserveSuggestionsPreferencesUseCase,
    private val backupPreferencesUseCase: ObserveBackupPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<UserPreferences> = combine(
        flows = arrayOf(
            observeGeneralPreferencesUseCase(),
            observeCartsPreferencesUseCase(),
            observeProductsPreferencesUseCase(),
            observeProductsWidgetPreferencesUseCase(),
            observeAddEditProductPreferencesUseCase(),
            suggestionsPreferencesUseCase(),
            backupPreferencesUseCase(),
        ),
        transform = { it.toUserPreferences() }
    ).flowOn(ioDispatcher)

    private fun Array<Any>.toUserPreferences() = UserPreferences(
        get(0) as GeneralPreferences,
        get(1) as CartsPreferences,
        get(2) as ProductsPreferences,
        get(3) as ProductsWidgetPreferences,
        get(4) as AddEditProductPreferences,
        get(5) as SuggestionsPreferences,
        get(6) as BackupPreferences
    )
}