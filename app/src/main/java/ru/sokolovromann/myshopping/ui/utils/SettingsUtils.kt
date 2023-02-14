package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.Settings
import ru.sokolovromann.myshopping.ui.compose.state.SettingsItem
import ru.sokolovromann.myshopping.ui.compose.state.SettingsUid
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Settings.getSettingsItems(): Map<UiText, List<SettingsItem>> {
    return mapOf(
        UiText.FromResources(R.string.settings_header_generalSettings) to getGeneralSettingsItems(),
        UiText.FromResources(R.string.settings_header_money) to getMoneySettingsItems(),
        UiText.FromResources(R.string.settings_header_purchases) to getPurchasesSettingsItems(),
        UiText.FromResources(R.string.settings_header_aboutApp) to getAboutSettingsItems()
    )
}

private fun Settings.getGeneralSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.NightTheme,
            titleText = UiText.FromResources(R.string.settings_title_nightTheme),
            checked = settingsValues.nightTheme
        ),
        SettingsItem(
            uid = SettingsUid.FontSize,
            titleText = UiText.FromResources(R.string.settings_title_fontSize),
            bodyText = settingsValues.fontSize.toSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.FirstLetterUppercase,
            titleText = UiText.FromResources(R.string.settings_title_firstLetterUppercase),
            bodyText = UiText.FromResources(R.string.settings_body_firstLetterUppercase),
            checked = settingsValues.firstLetterUppercase
        )
    )
}

private fun Settings.getMoneySettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.DisplayMoney,
            titleText = UiText.FromResources(R.string.settings_title_displayMoney),
            checked = settingsValues.displayMoney
        ),
        SettingsItem(
            uid = SettingsUid.Currency,
            titleText = UiText.FromResources(R.string.settings_title_currencySymbol),
            bodyText = UiText.FromResourcesWithArgs(
                R.string.settings_body_currencySymbol,
                settingsValues.currency.symbol
            )
        ),
        SettingsItem(
            uid = SettingsUid.DisplayCurrencyToLeft,
            titleText = UiText.FromResources(R.string.settings_title_displayCurrencySymbolToLeft),
            checked = settingsValues.currency.displayToLeft
        ),
        SettingsItem(
            uid = SettingsUid.TaxRate,
            titleText = UiText.FromResources(R.string.settings_title_taxRate),
            bodyText = UiText.FromString(settingsValues.taxRate.toString())
        )
    )
}

private fun Settings.getPurchasesSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.ShoppingsMultiColumns,
            titleText = UiText.FromResources(R.string.settings_title_shoppingsMultiColumns),
            bodyText = UiText.FromResources(R.string.settings_body_shoppingsMultiColumns),
            checked = settingsValues.shoppingsMultiColumns
        ),
        SettingsItem(
            uid = SettingsUid.ProductsMultiColumns,
            titleText = UiText.FromResources(R.string.settings_title_productsMultiColumns),
            bodyText = UiText.FromResources(R.string.settings_body_productsMultiColumns),
            checked = settingsValues.productsMultiColumns
        ),
        SettingsItem(
            uid = SettingsUid.DisplayAutocomplete,
            titleText = UiText.FromResources(R.string.settings_title_displayAutocomplete),
            bodyText = settingsValues.productsDisplayAutocomplete.toSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.DisplayDefaultAutocomplete,
            titleText = UiText.FromResources(R.string.settings_title_displayDefaultAutocomplete),
            checked = settingsValues.productsDisplayDefaultAutocomplete
        ),
        SettingsItem(
            uid = SettingsUid.DisplayCompleted,
            titleText = UiText.FromResources(R.string.settings_title_displayCompleted),
            bodyText = settingsValues.displayCompleted.toSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.EditCompleted,
            titleText = UiText.FromResources(R.string.settings_title_editCompletedProduct),
            bodyText = UiText.FromResources(R.string.settings_body_editCompletedProduct),
            checked = settingsValues.productsEditCompleted
        ),
        SettingsItem(
            uid = SettingsUid.AddProduct,
            titleText = UiText.FromResources(R.string.settings_title_addLastProduct),
            bodyText = UiText.FromResources(R.string.settings_body_addLastProduct),
            checked = settingsValues.productsAddLastProduct
        )
    )
}

private fun Settings.getAboutSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.NoUId,
            titleText = UiText.FromResources(R.string.settings_title_developer),
            bodyText = UiText.FromString(settingsValues.developerName)
        ),
        SettingsItem(
            uid = SettingsUid.Email,
            titleText = UiText.FromResources(R.string.settings_title_email),
            bodyText = UiText.FromResources(R.string.settings_body_email)
        ),
        SettingsItem(
            uid = SettingsUid.NoUId,
            titleText = UiText.FromResources(R.string.settings_title_appVersion),
            bodyText = UiText.FromString(settingsValues.appVersion)
        ),
        SettingsItem(
            uid = SettingsUid.Github,
            titleText = UiText.FromResources(R.string.settings_title_github),
            bodyText = UiText.FromResources(R.string.settings_body_github)
        )
    )
}