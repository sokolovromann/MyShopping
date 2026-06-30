package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.LockProductField
import ru.sokolovromann.myshopping.core.domain.repository.AddEditProductPreferencesRepository

class UpdateAddEditProductPreferencesUseCase @Inject constructor(
    private val addEditProductPreferencesRepository: AddEditProductPreferencesRepository,
    private val observeAddEditProductPreferencesUseCase: ObserveAddEditProductPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: AddEditProductPreferences): Unit =
        withContext(ioDispatcher) {
            addEditProductPreferencesRepository.updateAddEditProductPreferences(preferences)
        }

    suspend operator fun invoke(lockField: LockProductField): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(lockField = lockField)
            addEditProductPreferencesRepository.updateAddEditProductPreferences(preferences)
        }

    suspend operator fun invoke(afterTappingByEnter: AfterTappingByProductEnter): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterTappingByEnter = afterTappingByEnter)
            addEditProductPreferencesRepository.updateAddEditProductPreferences(preferences)
        }

    suspend operator fun invoke(afterAdding: AfterAddingProduct): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterAdding = afterAdding)
            addEditProductPreferencesRepository.updateAddEditProductPreferences(preferences)
        }

    suspend operator fun invoke(afterEditing: AfterEditingProduct): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(afterEditing = afterEditing)
            addEditProductPreferencesRepository.updateAddEditProductPreferences(preferences)
        }

    suspend operator fun invoke(tax: Tax): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(tax = tax)
            addEditProductPreferencesRepository.updateAddEditProductPreferences(preferences)
        }

    private suspend fun getPreferences() = observeAddEditProductPreferencesUseCase().first()
}