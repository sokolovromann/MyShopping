package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsView
import ru.sokolovromann.myshopping.core.domain.model.SortProducts
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct
import ru.sokolovromann.myshopping.core.domain.repository.UserPreferencesRepository

class UpdateProductsPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val observeProductsPreferencesUseCase: ObserveProductsPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: ProductsPreferences): Unit = withContext(ioDispatcher) {
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(view: ProductsView): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(view = view)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(sort: SortProducts): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(sort = sort)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(groupByStatus: GroupProductsByStatus): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(groupByStatus = groupByStatus)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(addingMode: ProductsAddingMode): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(addingMode = addingMode)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(calculateTotal: CalculateProductsTotal): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(calculateTotal = calculateTotal)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(strikethroughCompleted: StrikethroughCompletedProducts): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(strikethroughCompleted = strikethroughCompleted)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(afterCompleting: AfterCompletingProduct): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(afterCompleting = afterCompleting)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(afterTappingByCheckbox: AfterTappingByProductCheckbox): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(afterTappingByCheckbox = afterTappingByCheckbox)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(checkboxColor: CheckboxColor): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(checkboxColor = checkboxColor)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(afterTappingByItem: AfterTappingByProductItem): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(afterTappingByItem = afterTappingByItem)
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    suspend operator fun invoke(swipeProduct: SwipeProduct): Unit = withContext(ioDispatcher) {
        val preferences = when (swipeProduct) {
            is SwipeProduct.Left -> getPreferences().copy(swipeLeft = swipeProduct)
            is SwipeProduct.Right -> getPreferences().copy(swipeRight = swipeProduct)
        }
        userPreferencesRepository.updateProductsPreferences(preferences)
    }

    private suspend fun getPreferences() = observeProductsPreferencesUseCase().first()
}