package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.app.AppLocale
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.FontSize

@Deprecated("Use model/AutocompletesWithConfig")
data class Autocompletes(
    private val autocompletes: List<Autocomplete> = listOf(),
    private val appConfig: AppConfig = AppConfig()
) {

    private val userPreferences = appConfig.userPreferences

    fun groupAutocompletesByName(): Map<String, List<Autocomplete>> {
        return autocompletes.sortAutocompletes().groupBy { it.name.toSearch() }
    }

    fun getNames(): List<String> {
        return groupAutocompletesByName().keys.toList()
    }

    fun getUidsByNames(names: List<String>): List<String> {
        return autocompletes
            .filter { names.contains(it.name.toSearch()) }
            .map { it.uid }
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun getMaxAutocompletesQuantities(): Int {
        return userPreferences.maxAutocompletesQuantities
    }

    fun getMaxAutocompletesMoneys(): Int {
        return userPreferences.maxAutocompletesMoneys
    }

    fun getMaxAutocompletesOthers(): Int {
        return userPreferences.maxAutocompletesOthers
    }

    fun isDisplayMoney(): Boolean {
        return userPreferences.displayMoney
    }

    fun isLocationEnabled(): Boolean {
        return AppLocale.isLanguageSupported() && userPreferences.displayDefaultAutocompletes
    }

    fun isSmartphoneScreen(): Boolean {
        return appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium
    }

    fun isMultiColumns(): Boolean {
        return !isSmartphoneScreen()
    }

    fun isAutocompletesEmpty(): Boolean {
        return autocompletes.isEmpty()
    }
}