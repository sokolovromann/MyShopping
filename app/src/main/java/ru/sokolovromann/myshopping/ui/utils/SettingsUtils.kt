package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.Settings
import ru.sokolovromann.myshopping.data.utils.displayZerosAfterDecimal
import ru.sokolovromann.myshopping.ui.compose.state.SettingsItem
import ru.sokolovromann.myshopping.ui.compose.state.SettingsUid
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import java.util.Locale

fun Settings.getSettingsItems(): Map<UiText, List<SettingsItem>> {
    val map = mutableMapOf<UiText, List<SettingsItem>>()
    map[UiText.FromResources(R.string.settings_header_generalSettings)] = getGeneralSettingsItems()
    map[UiText.FromResources(R.string.settings_header_money)] = getMoneySettingsItems()
    map[UiText.FromResources(R.string.settings_header_purchases)] = getPurchasesSettingsItems()
    map[UiText.FromResources(R.string.settings_header_autocompletes)] = getAutocompletesSettingsItems()
    map[UiText.FromResources(R.string.settings_header_aboutApp)] = getAboutSettingsItems()

    return map.toMap()
}

private fun Settings.getGeneralSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.NightTheme,
            titleText = UiText.FromResources(R.string.settings_title_nightTheme),
            bodyText = UiText.FromResources(R.string.settings_body_nightTheme),
            checked = isNightTheme()
        ),
        SettingsItem(
            uid = SettingsUid.FontSize,
            titleText = UiText.FromResources(R.string.settings_title_fontSize),
            bodyText = getFontSize().toSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.Backup,
            titleText = UiText.FromResources(R.string.settings_title_backup),
            bodyText = UiText.FromResources(R.string.settings_body_backup)
        )
    )
}

private fun Settings.getMoneySettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.DisplayMoney,
            titleText = UiText.FromResources(R.string.settings_title_displayMoney),
            checked = displayMoney()
        ),
        SettingsItem(
            uid = SettingsUid.Currency,
            titleText = UiText.FromResources(R.string.settings_title_currencySymbol),
            bodyText = UiText.FromResourcesWithArgs(
                R.string.settings_body_currencySymbol,
                getCurrency().symbol
            )
        ),
        SettingsItem(
            uid = SettingsUid.DisplayCurrencyToLeft,
            titleText = UiText.FromResources(R.string.settings_title_displayCurrencySymbolToLeft),
            checked = getCurrency().displayToLeft
        ),
        SettingsItem(
            uid = SettingsUid.DisplayMoneyZeros,
            titleText = UiText.FromResources(R.string.settings_title_displayMoneyZeros),
            bodyText = UiText.FromResources(R.string.settings_body_displayMoneyZeros),
            checked = getMoneyDecimalFormat().displayZerosAfterDecimal()
        ),
        SettingsItem(
            uid = SettingsUid.TaxRate,
            titleText = UiText.FromResources(R.string.settings_title_taxRate),
            bodyText = UiText.FromString(getTaxRate().getDisplayValue())
        )
    )
}

private fun Settings.getPurchasesSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.DisplayCompletedPurchases,
            titleText = UiText.FromResources(R.string.settings_title_displayCompletedPurchases),
            bodyText = getDisplayCompleted().toPurchasesSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.EditProductAfterCompleted,
            titleText = UiText.FromResources(R.string.settings_title_editProductAfterCompleted),
            bodyText = UiText.FromResources(R.string.settings_body_editProductAfterCompleted),
            checked = editProductAfterCompleted()
        ),
        SettingsItem(
            uid = SettingsUid.CompletedWithCheckbox,
            titleText = UiText.FromResources(R.string.settings_title_completedWithCheckbox),
            bodyText = UiText.FromResources(R.string.settings_body_completedWithCheckbox),
            checked = isCompletedWithCheckbox(),
        ),
        SettingsItem(
            uid = SettingsUid.ColoredCheckbox,
            titleText = UiText.FromResources(R.string.settings_title_coloredCheckbox),
            bodyText = UiText.FromResources(R.string.settings_body_coloredCheckbox),
            checked = isColoredCheckbox()
        ),
        SettingsItem(
            uid = SettingsUid.DisplayShoppingsProducts,
            titleText = UiText.FromResources(R.string.settings_title_displayShoppingsProducts),
            bodyText = getShoppingsProducts().toPurchasesSettingsText()
        ),
        SettingsItem(
            uid = SettingsUid.DisplayOtherFields,
            titleText = UiText.FromResources(R.string.settings_title_displayOtherFields),
            bodyText = UiText.FromResources(R.string.settings_body_displayOtherFields),
            checked = displayOtherFields()
        ),
        SettingsItem(
            uid = SettingsUid.EnterToSaveProducts,
            titleText = UiText.FromResources(R.string.settings_title_enterToSaveProduct),
            bodyText = UiText.FromResources(R.string.settings_body_enterToSaveProduct),
            checked = isEnterToSaveProduct()
        )
    )
}

private fun Settings.getAutocompletesSettingsItems(): List<SettingsItem> {
    val displayDefaultAutocompletes = SettingsItem(
        uid = SettingsUid.DisplayDefaultAutocomplete,
        titleText = UiText.FromResources(R.string.settings_title_displayDefaultAutocompletes),
        checked = displayDefaultAutocompletes()
    )

    val saveProductsToAutocompletes = SettingsItem(
        uid = SettingsUid.SaveProductToAutocompletes,
        titleText = UiText.FromResources(R.string.settings_title_saveProductToAutocompletes),
        bodyText = UiText.FromResources(R.string.settings_body_saveProductToAutocompletes),
        checked = saveProductToAutocompletes()
    )

    return if (Locale.getDefault().isSupported()) {
        listOf(displayDefaultAutocompletes, saveProductsToAutocompletes)
    } else {
        listOf(saveProductsToAutocompletes)
    }
}

private fun Settings.getMigrationSettingsItems(): List<SettingsItem> {
    return listOf(
        SettingsItem(
            uid = SettingsUid.MigrateFromCodeVersion14,
            titleText = UiText.FromResources(R.string.settings_title_migrateFromCodeVersion14),
            bodyText = UiText.FromResources(R.string.settings_body_migrateFromCodeVersion14)
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
        ),
        SettingsItem(
            uid = SettingsUid.PrivacyPolicy,
            titleText = UiText.FromResources(R.string.settings_title_privacy_policy),
            bodyText = UiText.FromResources(R.string.settings_body_privacy_policy)
        ),
        SettingsItem(
            uid = SettingsUid.TermsAndConditions,
            titleText = UiText.FromResources(R.string.settings_title_terms_and_conditions),
            bodyText = UiText.FromResources(R.string.settings_body_terms_and_conditions)
        )
    )
}