package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository

class UpdateGeneralPreferencesUseCase @Inject constructor(
    private val generalPreferencesRepository: GeneralPreferencesRepository,
    private val observeGeneralPreferencesUseCase: ObserveGeneralPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: GeneralPreferences): Unit =
        withContext(ioDispatcher) {
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    suspend operator fun invoke(theme: Theme): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(theme = theme)
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    suspend operator fun invoke(fontSize: FontSize): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(fontSize = fontSize)
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    suspend operator fun invoke(formattingMode: DateTimeFormattingMode): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(dateTimeFormattingMode = formattingMode)
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    suspend operator fun invoke(formattingMode: MoneyFormattingMode): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(moneyFormattingMode = formattingMode)
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    suspend operator fun invoke(currency: Currency): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(currency = currency)
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    suspend operator fun invoke(displayDelay: KeyboardDisplayDelay): Unit =
        withContext(ioDispatcher) {
            val preferences = getPreferences().copy(keyboardDisplayDelay = displayDelay)
            generalPreferencesRepository.updateGeneralPreferences(preferences)
        }

    private suspend fun getPreferences() = observeGeneralPreferencesUseCase().first()
}