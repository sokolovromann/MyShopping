package ru.sokolovromann.myshopping.data.local.dao

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data.local.entity.CurrencyResourcesEntity

class ResourcesDao(appContent: AppContent) {

    private val resources = appContent.getResources()

    fun getAutocompleteNames(): List<String> {
        return resources.getStringArray(R.array.data_text_defaultAutocompleteNames).toList()
    }

    fun searchAutocompleteNames(search: String): List<String> {
        return resources.getStringArray(R.array.data_text_defaultAutocompleteNames)
            .filter { it.lowercase().contains(search.lowercase()) }
    }

    fun getCurrency(): CurrencyResourcesEntity {
        return CurrencyResourcesEntity(
            defaultCurrency = resources.getString(R.string.data_text_defaultCurrency),
            displayDefaultCurrencyToLeft = resources.getBoolean(R.bool.data_value_displayDefaultCurrencyToLeft)
        )
    }
}