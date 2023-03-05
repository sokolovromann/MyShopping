package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.SettingsDao
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
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
            flow = settingsDao.getAppPreferences(),
            flow2 = resources.getSettingsResources(),
            transform = { preferencesEntity, resourcesEntity ->
                mapping.toSettings(preferencesEntity, resourcesEntity)
            }
        )
    }

    override suspend fun displayCompletedPurchasesFirst(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.FIRST)
        settingsDao.displayCompletedPurchases(displayCompleted)
    }

    override suspend fun displayCompletedPurchasesLast(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.LAST)
        settingsDao.displayCompletedPurchases(displayCompleted)
    }

    override suspend fun tinyFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.TINY)
        settingsDao.saveFontSize(fontSize)
    }

    override suspend fun smallFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.SMALL)
        settingsDao.saveFontSize(fontSize)
    }

    override suspend fun mediumFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.MEDIUM)
        settingsDao.saveFontSize(fontSize)
    }

    override suspend fun largeFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.LARGE)
        settingsDao.saveFontSize(fontSize)
    }

    override suspend fun hugeFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.HUGE)
        settingsDao.saveFontSize(fontSize)
    }

    override suspend fun invertNightTheme(): Unit = withContext(dispatchers.io) {
        settingsDao.invertNightTheme()
    }

    override suspend fun invertDisplayMoney(): Unit = withContext(dispatchers.io) {
        settingsDao.invertDisplayMoney()
    }

    override suspend fun invertDisplayCurrencyToLeft(): Unit = withContext(dispatchers.io) {
        settingsDao.invertDisplayCurrencyToLeft()
    }

    override suspend fun invertShoppingListsMultiColumns(): Unit = withContext(dispatchers.io) {
        settingsDao.invertShoppingsMultiColumns()
    }

    override suspend fun invertProductsMultiColumns(): Unit = withContext(dispatchers.io) {
        settingsDao.invertProductsMultiColumns()
    }

    override suspend fun invertEditProductAfterCompleted(): Unit = withContext(dispatchers.io) {
        settingsDao.invertEditProductAfterCompleted()
    }

    override suspend fun invertSaveProductToAutocompletes(): Unit = withContext(dispatchers.io) {
        settingsDao.invertSaveProductToAutocompletes()
    }

    override suspend fun invertDisplayDefaultAutocompletes(): Unit = withContext(dispatchers.io) {
        settingsDao.invertDisplayDefaultAutocompletes()
    }

    override suspend fun hideCompletedPurchases(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.HIDE)
        settingsDao.displayCompletedPurchases(displayCompleted)
    }
}