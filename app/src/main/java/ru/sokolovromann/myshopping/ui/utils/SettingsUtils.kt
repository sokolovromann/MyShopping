package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.Settings
import ru.sokolovromann.myshopping.ui.compose.state.SettingsItem
import ru.sokolovromann.myshopping.ui.compose.state.SettingsUid
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Settings.getSettingsItems(): Map<UiText, List<SettingsItem>> {
    val map = mutableMapOf<UiText, List<SettingsItem>>()
    map[UiText.FromResources(R.string.settings_header_generalSettings)] = getGeneralSettingsItems()
    map[UiText.FromResources(R.string.settings_header_money)] = getMoneySettingsItems()
    map[UiText.FromResources(R.string.settings_header_purchases)] = getPurchasesSettingsItems()
    map[UiText.FromResources(R.string.settings_header_autocompletes)] = getAutocompletesSettingsItems()
    if (preferences.firstAppVersion == 14) {
        map[UiText.FromResources(R.string.settings_header_migration)] = getMigrationSettingsItems()
    }
    map[UiText.FromResources(R.string.settings_header_aboutApp)] = getAboutSettingsItems()

    return map.toMap()
}

private fun Settings.getGeneralSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.NightTheme,
            titleText = UiText.FromResources(R.string.settings_title_nightTheme),
            checked = preferences.nightTheme
        ),
        SettingsItem(
            uid = SettingsUid.FontSize,
            titleText = UiText.FromResources(R.string.settings_title_fontSize),
            bodyText = preferences.fontSize.toSettingsText()
        )
    )
}

private fun Settings.getMoneySettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.DisplayMoney,
            titleText = UiText.FromResources(R.string.settings_title_displayMoney),
            checked = preferences.displayMoney
        ),
        SettingsItem(
            uid = SettingsUid.Currency,
            titleText = UiText.FromResources(R.string.settings_title_currencySymbol),
            bodyText = UiText.FromResourcesWithArgs(
                R.string.settings_body_currencySymbol,
                preferences.currency.symbol
            )
        ),
        SettingsItem(
            uid = SettingsUid.DisplayCurrencyToLeft,
            titleText = UiText.FromResources(R.string.settings_title_displayCurrencySymbolToLeft),
            checked = preferences.currency.displayToLeft
        ),
        SettingsItem(
            uid = SettingsUid.TaxRate,
            titleText = UiText.FromResources(R.string.settings_title_taxRate),
            bodyText = UiText.FromString(preferences.taxRate.toString())
        )
    )
}

private fun Settings.getPurchasesSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.ShoppingsMultiColumns,
            titleText = UiText.FromResources(R.string.settings_title_shoppingsMultiColumns),
            bodyText = UiText.FromResources(R.string.settings_body_shoppingsMultiColumns),
            checked = preferences.shoppingsMultiColumns
        ),
        SettingsItem(
            uid = SettingsUid.ProductsMultiColumns,
            titleText = UiText.FromResources(R.string.settings_title_productsMultiColumns),
            bodyText = UiText.FromResources(R.string.settings_body_productsMultiColumns),
            checked = preferences.productsMultiColumns
        ),
        SettingsItem(
            uid = SettingsUid.DisplayCompletedPurchases,
            titleText = UiText.FromResources(R.string.settings_title_displayCompletedPurchases),
            bodyText = preferences.displayCompletedPurchases.toPurchasesSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.EditProductAfterCompleted,
            titleText = UiText.FromResources(R.string.settings_title_editProductAfterCompleted),
            bodyText = UiText.FromResources(R.string.settings_body_editProductAfterCompleted),
            checked = preferences.editProductAfterCompleted
        )
    )
}

private fun Settings.getAutocompletesSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.DisplayDefaultAutocomplete,
            titleText = UiText.FromResources(R.string.settings_title_displayDefaultAutocompletes),
            checked = preferences.displayDefaultAutocompletes
        ),
        SettingsItem(
            uid = SettingsUid.SaveProductToAutocompletes,
            titleText = UiText.FromResources(R.string.settings_title_saveProductToAutocompletes),
            bodyText = UiText.FromResources(R.string.settings_body_saveProductToAutocompletes),
            checked = preferences.saveProductToAutocompletes
        )
    )
}

private fun Settings.getMigrationSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.MigrateFromAppVersion14,
            titleText = UiText.FromResources(R.string.settings_title_migrateFromAppVersion14),
            bodyText = UiText.FromResources(R.string.settings_body_migrateFromAppVersion14)
        )
    )
}

private fun Settings.getAboutSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.NoUId,
            titleText = UiText.FromResources(R.string.settings_title_developer),
            bodyText = UiText.FromString(developerName)
        ),
        SettingsItem(
            uid = SettingsUid.Email,
            titleText = UiText.FromResources(R.string.settings_title_email),
            bodyText = UiText.FromResources(R.string.settings_body_email)
        ),
        SettingsItem(
            uid = SettingsUid.NoUId,
            titleText = UiText.FromResources(R.string.settings_title_appVersion),
            bodyText = UiText.FromString(appVersion)
        ),
        SettingsItem(
            uid = SettingsUid.Github,
            titleText = UiText.FromResources(R.string.settings_title_github),
            bodyText = UiText.FromResources(R.string.settings_body_github)
        )
    )
}