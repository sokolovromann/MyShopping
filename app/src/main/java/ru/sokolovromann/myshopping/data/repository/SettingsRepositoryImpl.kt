package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.SettingsDao
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
import ru.sokolovromann.myshopping.data.repository.model.DisplayAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Settings
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao,
    private val resources: SettingsResources,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : SettingsRepository {

    override suspend fun getSettings(): Flow<Settings> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = settingsDao.getSettings(),
            flow2 = settingsDao.getSettingsPreferences(),
            flow3 = resources.getSettingsResources(),
            transform = { entity, preferencesEntity, resourcesEntity ->
                mapping.toSettings(entity, preferencesEntity, resourcesEntity)
            }
        )
    }

    override suspend fun displayProductsAutocompleteAll(): Unit = withContext(dispatchers.io) {
        val displayAutocomplete = mapping.toDisplayAutocompleteName(DisplayAutocomplete.ALL)
        settingsDao.displayProductAutocomplete(displayAutocomplete)
    }

    override suspend fun displayProductAutocompleteName(): Unit = withContext(dispatchers.io) {
        val displayAutocomplete = mapping.toDisplayAutocompleteName(DisplayAutocomplete.NAME)
        settingsDao.displayProductAutocomplete(displayAutocomplete)
    }

    override suspend fun displayCompletedFirst(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.FIRST)
        settingsDao.displayCompleted(displayCompleted)
    }

    override suspend fun displayCompletedLast(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.LAST)
        settingsDao.displayCompleted(displayCompleted)
    }

    override suspend fun tinyFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.TINY)
        settingsDao.fontSizeSelected(fontSize)
    }

    override suspend fun smallFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.SMALL)
        settingsDao.fontSizeSelected(fontSize)
    }

    override suspend fun mediumFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.MEDIUM)
        settingsDao.fontSizeSelected(fontSize)
    }

    override suspend fun largeFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.LARGE)
        settingsDao.fontSizeSelected(fontSize)
    }

    override suspend fun hugeFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.HUGE)
        settingsDao.fontSizeSelected(fontSize)
    }

    override suspend fun invertNightTheme(): Unit = withContext(dispatchers.io) {
        settingsDao.invertNightTheme()
    }

    override suspend fun invertDisplayMoney(): Unit = withContext(dispatchers.io) {
        settingsDao.invertDisplayMoney()
    }

    override suspend fun invertDisplayCurrencyToLeft(): Unit = withContext(dispatchers.io) {
        settingsDao.invertCurrencyDisplayToLeft()
    }

    override suspend fun invertFirstLetterUppercase(): Unit = withContext(dispatchers.io) {
        settingsDao.invertFirstLetterUppercase()
    }

    override suspend fun invertShoppingListsMultiColumns(): Unit = withContext(dispatchers.io) {
        settingsDao.invertShoppingsMultiColumns()
    }

    override suspend fun invertProductsMultiColumns(): Unit = withContext(dispatchers.io) {
        settingsDao.invertProductsMultiColumns()
    }

    override suspend fun invertProductsEditCompleted(): Unit = withContext(dispatchers.io) {
        settingsDao.invertProductsEditCompleted()
    }

    override suspend fun invertProductsAddLastProduct(): Unit = withContext(dispatchers.io) {
        settingsDao.invertProductsAddLastProduct()
    }

    override suspend fun hideProductsAutocomplete(): Unit = withContext(dispatchers.io) {
        val displayAutocomplete = mapping.toDisplayAutocompleteName(DisplayAutocomplete.HIDE)
        settingsDao.displayProductAutocomplete(displayAutocomplete)
    }

    override suspend fun hideCompleted(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.HIDE)
        settingsDao.displayCompleted(displayCompleted)
    }
}