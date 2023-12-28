package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.app.AppLocale
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Settings
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.utils.displayZerosAfterDecimal
import ru.sokolovromann.myshopping.ui.model.SettingItem
import ru.sokolovromann.myshopping.ui.model.SettingUid
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.model.UiString

object UiAppConfigMapper {

    fun toUiFontSize(fontSize: FontSize): UiFontSize {
        val default: UiFontSize = UiFontSize.Default
        val offset: Int = when (fontSize) {
            FontSize.SMALL -> -2
            FontSize.MEDIUM -> 0
            FontSize.LARGE -> 2
            FontSize.VERY_LARGE -> 4
            FontSize.HUGE -> 6
            FontSize.VERY_HUGE -> 8
        }
        return UiFontSize(
            itemTitle = default.itemTitle + offset,
            itemBody = default.itemBody + offset,
            itemsHeader = default.itemsHeader + offset,
            widgetHeader = default.widgetHeader + offset,
            widgetContent = default.widgetContent + offset,
            button = default.button + offset,
            textField = default.textField + offset
        )
    }

    fun toSettingItems(settingsWithConfig: SettingsWithConfig): Map<UiString, List<SettingItem>> {
        val map = mutableMapOf<UiString, List<SettingItem>>()
        map[UiString.FromResources(R.string.settings_header_generalSettings)] = toGeneralItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_money)] = toMoneyItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_purchases)] = toPurchasesItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_autocompletes)] = toAutocompletesItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_aboutApp)] = toAboutItems(settingsWithConfig.settings, settingsWithConfig.appConfig)
        return map.toMap()
    }

    private fun toGeneralItems(appConfig: AppConfig): List<SettingItem> {
        val userPreferences = appConfig.userPreferences
        return listOf(
            SettingItem(
                uid = SettingUid.NightTheme,
                title = UiString.FromResources(R.string.settings_title_nightTheme),
                body = UiString.FromResources(R.string.settings_body_nightTheme),
                checked = userPreferences.nightTheme
            ),
            SettingItem(
                uid = SettingUid.FontSize,
                title = UiString.FromResources(R.string.settings_title_fontSize),
                body = when (userPreferences.fontSize) {
                    FontSize.SMALL -> UiString.FromResources(R.string.settings_action_selectSmallFontSize)
                    FontSize.MEDIUM -> UiString.FromResources(R.string.settings_action_selectMediumFontSize)
                    FontSize.LARGE -> UiString.FromResources(R.string.settings_action_selectLargeFontSize)
                    FontSize.VERY_LARGE -> UiString.FromResources(R.string.settings_action_selectVeryLargeFontSize)
                    FontSize.HUGE -> UiString.FromResources(R.string.settings_action_selectHugeFontSize)
                    FontSize.VERY_HUGE -> UiString.FromResources(R.string.settings_action_selectVeryHugeFontSize)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.Backup,
                title = UiString.FromResources(R.string.settings_title_backup),
                body = UiString.FromResources(R.string.settings_body_backup),
                checked = null
            )
        )
    }

    private fun toMoneyItems(appConfig: AppConfig): List<SettingItem> {
        val userPreferences = appConfig.userPreferences
        return listOf(
            SettingItem(
                uid = SettingUid.DisplayMoney,
                title = UiString.FromResources(R.string.settings_title_displayMoney),
                body = UiString.FromResources(R.string.settings_body_displayMoney),
                checked = userPreferences.displayMoney
            ),
            SettingItem(
                uid = SettingUid.Currency,
                title = UiString.FromResources(R.string.settings_title_currencySymbol),
                body = UiString.FromResourcesWithArgs(R.string.settings_body_currencySymbol, userPreferences.currency.symbol),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.DisplayCurrencyToLeft,
                title = UiString.FromResources(R.string.settings_title_displayCurrencySymbolToLeft),
                body = UiString.FromString(""),
                checked = userPreferences.currency.displayToLeft
            ),
            SettingItem(
                uid = SettingUid.DisplayMoneyZeros,
                title = UiString.FromResources(R.string.settings_title_displayMoneyZeros),
                body = UiString.FromResources(R.string.settings_body_displayMoneyZeros),
                checked = userPreferences.moneyDecimalFormat.displayZerosAfterDecimal()
            ),
            SettingItem(
                uid = SettingUid.TaxRate,
                title = UiString.FromResources(R.string.settings_title_taxRate),
                body = UiString.FromString(appConfig.userPreferences.taxRate.getDisplayValue()),
                checked = null
            )
        )
    }

    private fun toPurchasesItems(appConfig: AppConfig): List<SettingItem> {
        val userPreferences = appConfig.userPreferences
        return listOf(
            SettingItem(
                uid = SettingUid.DisplayCompletedPurchases,
                title = UiString.FromResources(R.string.settings_title_displayCompletedPurchases),
                body = when (userPreferences.displayCompleted) {
                    DisplayCompleted.FIRST -> UiString.FromResources(R.string.settings_action_displayCompletedPurchasesFirst)
                    DisplayCompleted.LAST -> UiString.FromResources(R.string.settings_action_displayCompletedPurchasesLast)
                    DisplayCompleted.HIDE -> UiString.FromResources(R.string.settings_action_hideCompletedPurchases)
                    DisplayCompleted.NO_SPLIT -> UiString.FromResources(R.string.settings_action_noSplitCompletedPurchases)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.EditProductAfterCompleted,
                title = UiString.FromResources(R.string.settings_title_editProductAfterCompleted),
                body = UiString.FromResources(R.string.settings_body_editProductAfterCompleted),
                checked = userPreferences.editProductAfterCompleted
            ),
            SettingItem(
                uid = SettingUid.CompletedWithCheckbox,
                title = UiString.FromResources(R.string.settings_title_completedWithCheckbox),
                body = UiString.FromResources(R.string.settings_body_completedWithCheckbox),
                checked = userPreferences.completedWithCheckbox,
            ),
            SettingItem(
                uid = SettingUid.ColoredCheckbox,
                title = UiString.FromResources(R.string.settings_title_coloredCheckbox),
                body = UiString.FromResources(R.string.settings_body_coloredCheckbox),
                checked = userPreferences.coloredCheckbox
            ),
            SettingItem(
                uid = SettingUid.DisplayShoppingsProducts,
                title = UiString.FromResources(R.string.settings_title_displayShoppingsProducts),
                body = when (userPreferences.displayShoppingsProducts) {
                    DisplayProducts.VERTICAL -> UiString.FromResources(R.string.settings_action_displayShoppingsProductsColumns)
                    DisplayProducts.HORIZONTAL -> UiString.FromResources(R.string.settings_action_displayShoppingsProductsRow)
                    DisplayProducts.HIDE -> UiString.FromResources(R.string.settings_action_hideShoppingsProducts)
                    DisplayProducts.HIDE_IF_HAS_TITLE -> UiString.FromResources(R.string.settings_action_hideShoppingsProductsIfHasTitle)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.DisplayOtherFields,
                title = UiString.FromResources(R.string.settings_title_displayOtherFields),
                body = UiString.FromResources(R.string.settings_body_displayOtherFields),
                checked = userPreferences.displayOtherFields
            ),
            SettingItem(
                uid = SettingUid.EnterToSaveProducts,
                title = UiString.FromResources(R.string.settings_title_enterToSaveProduct),
                body = UiString.FromResources(R.string.settings_body_enterToSaveProduct),
                checked = userPreferences.enterToSaveProduct
            )
        )
    }

    private fun toAutocompletesItems(appConfig: AppConfig): List<SettingItem> {
        val items: MutableList<SettingItem> = mutableListOf()
        val userPreferences = appConfig.userPreferences

        if (AppLocale.isLanguageSupported()) {
            items.add(
                SettingItem(
                    uid = SettingUid.DisplayDefaultAutocomplete,
                    title = UiString.FromResources(R.string.settings_title_displayDefaultAutocompletes),
                    body = UiString.FromString(""),
                    checked = userPreferences.displayDefaultAutocompletes
                )
            )
        }

        items.add(
            SettingItem(
                uid = SettingUid.SaveProductToAutocompletes,
                title = UiString.FromResources(R.string.settings_title_saveProductToAutocompletes),
                body = UiString.FromResources(R.string.settings_body_saveProductToAutocompletes),
                checked = userPreferences.saveProductToAutocompletes
            )
        )

        return items.toList()
    }

    private fun toAboutItems(settings: Settings, appConfig: AppConfig): List<SettingItem> {
        return listOf(
            SettingItem(
                uid = SettingUid.Developer,
                title = UiString.FromResources(R.string.settings_title_developer),
                body = UiString.FromString(settings.developerName),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.Email,
                title = UiString.FromResources(R.string.settings_title_email),
                body = UiString.FromResources(R.string.settings_body_email),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.AppVersion,
                title = UiString.FromResources(R.string.settings_title_appVersion),
                body = UiString.FromString(appConfig.appBuildConfig.getDisplayName()),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.Github,
                title = UiString.FromResources(R.string.settings_title_github),
                body = UiString.FromResources(R.string.settings_body_github),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.PrivacyPolicy,
                title = UiString.FromResources(R.string.settings_title_privacy_policy),
                body = UiString.FromResources(R.string.settings_body_privacy_policy),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.TermsAndConditions,
                title = UiString.FromResources(R.string.settings_title_terms_and_conditions),
                body = UiString.FromResources(R.string.settings_body_terms_and_conditions),
                checked = null
            )
        )
    }
}