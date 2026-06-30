package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsView
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.GroupCartsByStatus
import ru.sokolovromann.myshopping.core.domain.model.SortCarts
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart
import ru.sokolovromann.myshopping.core.domain.repository.CartsPreferencesRepository

class UpdateCartsPreferencesUseCase @Inject constructor(
    private val cartsPreferencesRepository: CartsPreferencesRepository,
    private val observeCartsPreferencesUseCase: ObserveCartsPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: CartsPreferences): Unit =
        withContext(ioDispatcher) {
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(view: CartsView): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(view = view)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(sort: SortCarts): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(sort = sort)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(groupByStatus: GroupCartsByStatus): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(groupByStatus = groupByStatus)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(calculateTotal: CalculateProductsTotal): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(calculateProductsTotal = calculateTotal)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(afterAdding: AfterAddingCart): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterAdding = afterAdding)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(afterCompleting: AfterCompletingCart): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterCompleting = afterCompleting)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(afterArchiving: AfterArchivingCart): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterArchiving = afterArchiving)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(afterTappingByCheckbox: AfterTappingByCartCheckbox): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterTappingByCheckbox = afterTappingByCheckbox)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(checkboxColor: CheckboxColor): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(checkboxColor = checkboxColor)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(swipeCart: SwipeCart): Unit =
        withContext(ioDispatcher) {
            val preferences = when (swipeCart) {
                is SwipeCart.Left -> getPreferences().copy(swipeLeft = swipeCart)
                is SwipeCart.Right -> getPreferences().copy(swipeRight = swipeCart)
            }
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    suspend operator fun invoke(deletionFromTrash: DeletionCartFromTrash): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(deletionFromTrash = deletionFromTrash)
            cartsPreferencesRepository.updateCartsPreferences(preferences)
        }

    private suspend fun getPreferences() = observeCartsPreferencesUseCase().first()
}