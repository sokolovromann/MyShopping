package ru.sokolovromann.myshopping.feature.settings.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.repository.AddEditProductPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.BackupPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.CartsPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.ProductsPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.ProductsWidgetPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsPreferencesRepository
import ru.sokolovromann.myshopping.feature.settings.domain.model.UserPreferences

class ObserveUserPreferencesUseCase @Inject constructor(
    private val generalPreferencesRepository: GeneralPreferencesRepository,
    private val cartsPreferencesRepository: CartsPreferencesRepository,
    private val productPreferencesRepository: ProductsPreferencesRepository,
    private val productsWidgetPreferencesRepository: ProductsWidgetPreferencesRepository,
    private val addEditProductPreferencesRepository: AddEditProductPreferencesRepository,
    private val suggestionsPreferencesRepository: SuggestionsPreferencesRepository,
    private val backupPreferencesRepository: BackupPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke() = combine(
        flows = arrayListOf(
            generalPreferencesRepository.observeGeneralPreferences(),
            cartsPreferencesRepository.observeCartsPreferences(),
            productPreferencesRepository.observeProductsPreferences(),
            productsWidgetPreferencesRepository.observeProductsWidgetPreferences(),
            addEditProductPreferencesRepository.observeAddEditProductPreferences(),
            suggestionsPreferencesRepository.observeSuggestionsPreferences(),
            backupPreferencesRepository.observeBackupPreferences()
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