package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.AfterProductCompleted
import ru.sokolovromann.myshopping.data.model.AfterSaveProduct
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.utils.displayZerosAfterDecimal
import ru.sokolovromann.myshopping.data39.suggestions.AddSuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.ui.model.SettingItem
import ru.sokolovromann.myshopping.ui.model.SettingUid
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.theme.FontSizeOffset
import ru.sokolovromann.myshopping.widget.WidgetFontSizeOffset

object UiAppConfigMapper {

    fun toFontSizeOffset(fontSize: FontSize): FontSizeOffset {
        val offset: Int = when (fontSize) {
            FontSize.SMALL -> -2
            FontSize.MEDIUM -> 0
            FontSize.LARGE -> 2
            FontSize.VERY_LARGE -> 4
            FontSize.HUGE -> 6
            FontSize.VERY_HUGE -> 8
        }
        return FontSizeOffset(offset)
    }

    fun toWidgetFontSizeOffset(fontSize: FontSize): WidgetFontSizeOffset {
        val offset: Int = when (fontSize) {
            FontSize.SMALL -> -2
            FontSize.MEDIUM -> 0
            FontSize.LARGE -> 2
            FontSize.VERY_LARGE -> 4
            FontSize.HUGE -> 6
            FontSize.VERY_HUGE -> 8
        }
        return WidgetFontSizeOffset(offset)
    }

    fun toSettingItems(
        settingsWithConfig: SettingsWithConfig,
        suggestionsConfig: SuggestionsConfig
    ): Map<UiString, List<SettingItem>> {
        val map = mutableMapOf<UiString, List<SettingItem>>()
        map[UiString.FromResources(R.string.settings_header_generalSettings)] = toGeneralItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_money)] = toMoneyItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_purchases)] = toPurchasesItems(settingsWithConfig.appConfig)
        map[UiString.FromResources(R.string.settings_header_autocompletes)] = toAutocompletesItems(suggestionsConfig)
        return map.toMap()
    }

    private fun toGeneralItems(appConfig: AppConfig): List<SettingItem> {
        val userPreferences = appConfig.userPreferences
        return listOf(
            SettingItem(
                uid = SettingUid.NightTheme,
                title = UiString.FromResources(R.string.settings_title_nightTheme),
                body = when (userPreferences.nightTheme) {
                    NightTheme.DISABLED -> UiString.FromResources(R.string.settings_action_selectDisabledNightTheme)
                    NightTheme.APP -> UiString.FromResources(R.string.settings_action_selectAppNightTheme)
                    NightTheme.WIDGET -> UiString.FromResources(R.string.settings_action_selectWidgetNightTheme)
                    NightTheme.APP_AND_WIDGET -> UiString.FromResources(R.string.settings_action_selectAppAndWidgetNightTheme)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.FontSize,
                title = UiString.FromResources(R.string.settings_title_fontSize),
                body = UiString.FromResources(R.string.settings_body_fontSize),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.Backup,
                title = UiString.FromResources(R.string.settings_title_backup),
                body = UiString.FromResources(R.string.settings_body_backup),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.AutomaticallyEmptyTrash,
                title = UiString.FromResources(R.string.settings_title_automaticallyEmptyTrash),
                body = UiString.FromResources(R.string.settings_body_automaticallyEmptyTrash),
                checked = userPreferences.automaticallyEmptyTrash
            )
        )
    }

    private fun toMoneyItems(appConfig: AppConfig): List<SettingItem> {
        val userPreferences = appConfig.userPreferences
        val items = mutableListOf(
            SettingItem(
                uid = SettingUid.DisplayMoney,
                title = UiString.FromResources(R.string.settings_title_displayMoney),
                body = UiString.FromResources(R.string.settings_body_displayMoney),
                checked = userPreferences.displayMoney
            )
        )

        if (userPreferences.displayMoney) {
            items.add(
                SettingItem(
                    uid = SettingUid.Currency,
                    title = UiString.FromResources(R.string.settings_title_currencySymbol),
                    body = UiString.FromResourcesWithArgs(R.string.settings_body_currencySymbol, userPreferences.currency.symbol),
                    checked = null
                )
            )
            items.add(
                SettingItem(
                    uid = SettingUid.DisplayCurrencyToLeft,
                    title = UiString.FromResources(R.string.settings_title_displayCurrencySymbolToLeft),
                    body = UiString.FromString(""),
                    checked = userPreferences.currency.displayToLeft
                )
            )
            items.add(
                SettingItem(
                    uid = SettingUid.DisplayMoneyZeros,
                    title = UiString.FromResources(R.string.settings_title_displayMoneyZeros),
                    body = UiString.FromResources(R.string.settings_body_displayMoneyZeros),
                    checked = userPreferences.moneyDecimalFormat.displayZerosAfterDecimal()
                )
            )
            items.add(
                SettingItem(
                    uid = SettingUid.TaxRate,
                    title = UiString.FromResources(R.string.settings_title_taxRate),
                    body = UiString.FromString(appConfig.userPreferences.taxRate.getDisplayValue()),
                    checked = null
                )
            )
        }

        return items
    }

    private fun toPurchasesItems(appConfig: AppConfig): List<SettingItem> {
        val userPreferences = appConfig.userPreferences
        return listOf(
            SettingItem(
                uid = SettingUid.DisplayCompletedPurchases,
                title = UiString.FromResources(R.string.settings_title_displayCompletedPurchases),
                body = UiString.FromResources(R.string.settings_body_displayCompletedPurchases),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.DisplayEmptyShoppings,
                title = UiString.FromResources(R.string.settings_title_displayEmptyShoppings),
                body = UiString.FromResources(R.string.settings_body_displayEmptyShoppings),
                checked = userPreferences.displayEmptyShoppings
            ),
            SettingItem(
                uid = SettingUid.StrikethroughCompletedProducts,
                title = UiString.FromResources(R.string.settings_title_strikethroughCompletedProducts),
                body = UiString.FromString(""),
                checked = userPreferences.strikethroughCompletedProducts
            ),
            SettingItem(
                uid = SettingUid.AfterProductCompleted,
                title = UiString.FromResources(R.string.settings_title_afterProductCompleted),
                body = when (userPreferences.afterProductCompleted) {
                    AfterProductCompleted.NOTHING -> UiString.FromResources(R.string.settings_action_nothingAfterProductCompleted)
                    AfterProductCompleted.EDIT -> UiString.FromResources(R.string.settings_action_editAfterProductCompleted)
                    AfterProductCompleted.DELETE -> UiString.FromResources(R.string.settings_action_deleteAfterProductCompleted)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.AfterShoppingCompleted,
                title = UiString.FromResources(R.string.settings_title_afterShoppingCompleted),
                body = when (userPreferences.afterShoppingCompleted) {
                    AfterShoppingCompleted.NOTHING -> UiString.FromResources(R.string.settings_action_nothingAfterShoppingCompleted)
                    AfterShoppingCompleted.ARCHIVE -> UiString.FromResources(R.string.settings_action_archiveAfterShoppingCompleted)
                    AfterShoppingCompleted.DELETE -> UiString.FromResources(R.string.settings_action_deleteAfterShoppingCompleted)
                    AfterShoppingCompleted.DELETE_PRODUCTS -> UiString.FromResources(R.string.settings_action_deleteProductsAfterShoppingCompleted)
                    AfterShoppingCompleted.DELETE_LIST_AND_PRODUCTS -> UiString.FromResources(R.string.settings_action_deleteListAndProductsAfterShoppingCompleted)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.ArchiveShoppingsAsCompleted,
                title = UiString.FromResources(R.string.settings_title_archiveAsCompleted),
                body = UiString.FromResources(R.string.settings_body_archiveAsCompleted),
                checked = userPreferences.archiveAsCompleted
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
                uid = SettingUid.DisplayOtherFields,
                title = UiString.FromResources(R.string.settings_title_displayOtherFields),
                body = UiString.FromResources(R.string.settings_body_displayOtherFields),
                checked = userPreferences.displayOtherFields
            ),
            SettingItem(
                uid = SettingUid.DisplayListOfAutocompletes,
                title = UiString.FromResources(R.string.settings_title_displayListOfAutocompletes),
                body = UiString.FromResources(R.string.settings_body_displayListOfAutocompletes),
                checked = userPreferences.displayListOfAutocompletes
            ),
            SettingItem(
                uid = SettingUid.EnterToSaveProducts,
                title = UiString.FromResources(R.string.settings_title_enterToSaveProduct),
                body = UiString.FromResources(R.string.settings_body_enterToSaveProduct),
                checked = userPreferences.enterToSaveProduct
            ),
            SettingItem(
                uid = SettingUid.AfterSaveProduct,
                title = UiString.FromResources(R.string.settings_title_afterSaveProduct),
                body = when (userPreferences.afterSaveProduct) {
                    AfterSaveProduct.NOTHING -> UiString.FromResources(R.string.settings_action_nothingAfterSaveProduct)
                    AfterSaveProduct.CLOSE_SCREEN -> UiString.FromResources(R.string.settings_action_closeAfterSaveProduct)
                    AfterSaveProduct.OPEN_NEW_SCREEN -> UiString.FromResources(R.string.settings_action_openAfterSaveProduct)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.AfterAddShopping,
                title = UiString.FromResources(R.string.settings_title_afterAddShopping),
                body = when (userPreferences.afterAddShopping) {
                    AfterAddShopping.OPEN_PRODUCTS_SCREEN -> UiString.FromResources(R.string.settings_action_openProductsAfterAddShopping)
                    AfterAddShopping.OPEN_EDIT_SHOPPING_NAME_SCREEN -> UiString.FromResources(R.string.settings_action_openEditNameAfterAddShopping)
                    AfterAddShopping.OPEN_ADD_PRODUCT_SCREEN -> UiString.FromResources(R.string.settings_action_openAddProductAfterAddShopping)
                },
                checked = null
            ),
            SettingItem(
                uid = SettingUid.SwipeProduct,
                title = UiString.FromResources(R.string.settings_title_swipeProduct),
                body = UiString.FromResources(R.string.settings_body_swipeProduct),
                checked = null
            ),
            SettingItem(
                uid = SettingUid.SwipeShopping,
                title = UiString.FromResources(R.string.settings_title_swipeShopping),
                body = UiString.FromResources(R.string.settings_body_swipeShopping),
                checked = null
            )
        )
    }

    private fun toAutocompletesItems(suggestionsConfig: SuggestionsConfig): List<SettingItem> {
        val items: MutableList<SettingItem> = mutableListOf()

        items.add(
            SettingItem(
                uid = SettingUid.AddAutocompletes,
                title = UiString.FromResources(R.string.settings_action_addAutocompletes),
                body = when (suggestionsConfig.add) {
                    AddSuggestionWithDetails.SuggestionAndDetails -> UiString.FromResources(R.string.settings_action_addAutocompletesAll)
                    AddSuggestionWithDetails.Suggestion -> UiString.FromResources(R.string.settings_action_addAutocompletesName)
                    AddSuggestionWithDetails.DoNotAdd -> UiString.FromResources(R.string.settings_action_doNotAddAutocompletes)
                },
                checked = null
            )
        )

        items.add(
            SettingItem(
                uid = SettingUid.MaxAutocompletes,
                title = UiString.FromResources(R.string.settings_title_maxAutocompletes),
                body = UiString.FromResources(R.string.settings_body_maxAutocompletes),
                checked = null
            )
        )

        return items.toList()
    }
}