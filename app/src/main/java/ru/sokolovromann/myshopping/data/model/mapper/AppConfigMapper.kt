package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.data.local.entity.AppBuildConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.DeviceConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.UserPreferencesEntity
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.AfterProductCompleted
import ru.sokolovromann.myshopping.data.model.AfterSaveProduct
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.AppBuildConfig
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.DeviceConfig
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.SwipeProduct
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.data.model.UserPreferencesDefaults
import java.text.DecimalFormat

object AppConfigMapper {

    fun toAppConfigEntity(appConfig: AppConfig): AppConfigEntity {
        return AppConfigEntity(
            deviceConfig = toDeviceConfigEntity(appConfig.deviceConfig),
            appBuildConfig = toAppBuildConfigEntity(appConfig.appBuildConfig),
            userPreferences = toUserPreferencesEntity(appConfig.userPreferences)
        )
    }

    fun toAppConfig(entity: AppConfigEntity): AppConfig {
        return AppConfig(
            deviceConfig = toDeviceConfig(entity.deviceConfig),
            appBuildConfig = toAppBuildConfig(entity.appBuildConfig),
            userPreferences = toUserPreferences(entity.userPreferences)
        )
    }

    fun toSettingsWithConfig(
        appConfigEntity: AppConfigEntity
    ): SettingsWithConfig {
        return SettingsWithConfig(
            appConfig = toAppConfig(appConfigEntity)
        )
    }

    private fun toAppBuildConfigEntity(appBuildConfig: AppBuildConfig): AppBuildConfigEntity {
        return AppBuildConfigEntity(
            appFirstTime = "",
            userCodeVersion = appBuildConfig.userCodeVersion
        )
    }

    private fun toAppBuildConfig(entity: AppBuildConfigEntity): AppBuildConfig {
        return AppBuildConfig(
            appId = BuildConfig.APPLICATION_ID,
            appVersionName = BuildConfig.VERSION_NAME,
            userCodeVersion = toUserCodeVersion(entity)
        )
    }

    private fun toDeviceConfigEntity(deviceConfig: DeviceConfig): DeviceConfigEntity {
        return DeviceConfigEntity(
            screenWidthDp = deviceConfig.screenWidthDp,
            screenHeightDp = deviceConfig.screenHeightDp
        )
    }

    private fun toDeviceConfig(entity: DeviceConfigEntity): DeviceConfig {
        return DeviceConfig(
            screenWidthDp = toScreenDpOrDefault(entity.screenWidthDp),
            screenHeightDp = toScreenDpOrDefault(entity.screenHeightDp)
        )
    }

    private fun toUserPreferencesEntity(userPreferences: UserPreferences): UserPreferencesEntity {
        return UserPreferencesEntity(
            nightTheme = userPreferences.nightTheme.isAppNightTheme(),
            widgetNightTheme = userPreferences.nightTheme.isWidgetNightTheme(),
            fontSize = userPreferences.appFontSize.toString(),
            widgetFontSize = userPreferences.widgetFontSize.toString(),
            shoppingsMultiColumns = userPreferences.shoppingsMultiColumns,
            shoppingsSortBy = userPreferences.shoppingsSort.toString(),
            shoppingsSortAscending = userPreferences.shoppingsSort.ascending,
            shoppingsSortFormatted = userPreferences.shoppingsSortFormatted,
            productsMultiColumns = userPreferences.productsMultiColumns,
            displayCompleted = userPreferences.appDisplayCompleted.toString(),
            widgetDisplayCompleted = userPreferences.widgetDisplayCompleted.toString(),
            strikethroughCompletedProducts = userPreferences.strikethroughCompletedProducts,
            displayTotal = userPreferences.displayTotal.toString(),
            displayLongTotal = userPreferences.displayLongTotal,
            displayOtherFields = userPreferences.displayOtherFields,
            coloredCheckbox = userPreferences.coloredCheckbox,
            displayShoppingsProducts = userPreferences.displayShoppingsProducts.toString(),
            purchasesSeparator = userPreferences.purchasesSeparator,
            editProductAfterCompleted = userPreferences.editProductAfterCompleted,
            lockProductElement = userPreferences.lockProductElement.toString(),
            completedWithCheckbox = userPreferences.completedWithCheckbox,
            enterToSaveProduct = userPreferences.enterToSaveProduct,
            displayDefaultAutocompletes = userPreferences.displayDefaultAutocompletes,
            maxAutocompletesNames = userPreferences.maxAutocompletesNames,
            maxAutocompletesQuantities = userPreferences.maxAutocompletesQuantities,
            maxAutocompletesMoneys = userPreferences.maxAutocompletesMoneys,
            maxAutocompletesOthers = userPreferences.maxAutocompletesOthers,
            saveProductToAutocompletes = userPreferences.saveProductToAutocompletes,
            displayMoney = userPreferences.displayMoney,
            currency = userPreferences.currency.symbol,
            displayCurrencyToLeft = userPreferences.currency.displayToLeft,
            taxRate = userPreferences.taxRate.value,
            taxRateAsPercent = userPreferences.taxRate.asPercent,
            minMoneyFractionDigits = userPreferences.moneyDecimalFormat.minimumFractionDigits,
            minQuantityFractionDigits = userPreferences.quantityDecimalFormat.minimumFractionDigits,
            maxMoneyFractionDigits = userPreferences.moneyDecimalFormat.maximumFractionDigits,
            maxQuantityFractionDigits = userPreferences.quantityDecimalFormat.maximumFractionDigits,
            automaticallyEmptyTrash = userPreferences.automaticallyEmptyTrash,
            displayListOfAutocompletes = userPreferences.displayListOfAutocompletes,
            afterSaveProduct = userPreferences.afterSaveProduct.toString(),
            afterAddShopping = userPreferences.afterAddShopping.toString(),
            afterProductCompleted = userPreferences.afterProductCompleted.toString(),
            afterShoppingCompleted = userPreferences.afterShoppingCompleted.toString(),
            displayEmptyShoppings = userPreferences.displayEmptyShoppings,
            swipeProductLeft = userPreferences.swipeProductLeft.toString(),
            swipeProductRight = userPreferences.swipeProductRight.toString(),
            swipeShoppingLeft = userPreferences.swipeShoppingLeft.toString(),
            swipeShoppingRight = userPreferences.swipeShoppingRight.toString()
        )
    }

    private fun toUserPreferences(entity: UserPreferencesEntity): UserPreferences {
        val widgetFontSize = entity.widgetFontSize ?: (entity.fontSize ?: "")
        return UserPreferences(
            nightTheme = toNightThemeOrDefault(entity.nightTheme, entity.widgetNightTheme),
            appFontSize = FontSize.valueOfOrDefault(entity.fontSize),
            widgetFontSize = FontSize.valueOfOrDefault(widgetFontSize),
            shoppingsMultiColumns = toMultiColumnsOrDefault(entity.shoppingsMultiColumns),
            shoppingsSort = Sort(
                sortBy = SortBy.valueOfOrDefault(entity.shoppingsSortBy),
                ascending = toShoppingsSortAscending(entity.shoppingsSortAscending)
            ),
            shoppingsSortFormatted = toShoppingsSortFormatted(entity.shoppingsSortFormatted),
            productsMultiColumns = toMultiColumnsOrDefault(entity.productsMultiColumns),
            appDisplayCompleted = DisplayCompleted.valueOfOrDefault(entity.displayCompleted),
            widgetDisplayCompleted = DisplayCompleted.valueOfOrDefault(entity.widgetDisplayCompleted),
            strikethroughCompletedProducts = toStrikethroughCompletedProductsOrDefault(entity.strikethroughCompletedProducts),
            displayTotal = DisplayTotal.valueOfOrDefault(entity.displayTotal),
            displayLongTotal = toDisplayLongTotalOrDefault(entity.displayLongTotal),
            displayOtherFields = toDisplayOtherFieldsOrDefault(entity.displayOtherFields),
            coloredCheckbox = toColoredCheckboxOrDefault(entity.coloredCheckbox),
            displayShoppingsProducts = DisplayProducts.valueOfOrDefault(entity.displayShoppingsProducts),
            purchasesSeparator = toPurchasesSeparatorOrDefault(entity.purchasesSeparator),
            editProductAfterCompleted = toEditProductAfterCompletedOrDefault(entity.editProductAfterCompleted),
            lockProductElement = LockProductElement.valueOfOrDefault(entity.lockProductElement),
            completedWithCheckbox = toCompletedWithCheckboxOrDefault(entity.completedWithCheckbox),
            enterToSaveProduct = toEnterToSaveProductOrDefault(entity.enterToSaveProduct),
            displayDefaultAutocompletes = toDisplayDefaultAutocompletesOrDefault(entity.displayDefaultAutocompletes),
            maxAutocompletesNames = toMaxAutocompleteNamesOrDefault(entity.maxAutocompletesNames),
            maxAutocompletesQuantities = toMaxAutocompleteQuantitiesOrDefault(entity.maxAutocompletesQuantities),
            maxAutocompletesMoneys = toMaxAutocompleteMoneysOrDefault(entity.maxAutocompletesMoneys),
            maxAutocompletesOthers = toMaxAutocompleteOthersOrDefault(entity.maxAutocompletesOthers),
            saveProductToAutocompletes = toSaveProductToAutocompletesOrDefault(entity.saveProductToAutocompletes),
            displayMoney = toDisplayMoneyOrDefault(entity.displayMoney),
            currency = toCurrencyOrDefault(entity),
            taxRate = toTaxRateOrDefault(entity),
            moneyDecimalFormat = toMoneyDecimalFormat(entity),
            quantityDecimalFormat = toQuantityDecimalFormat(entity),
            automaticallyEmptyTrash = toAutomaticallyEmptyTrashOrDefault(entity.automaticallyEmptyTrash),
            displayListOfAutocompletes = toDisplayListOfAutocompletesOrDefault(entity.displayListOfAutocompletes),
            afterSaveProduct = AfterSaveProduct.valueOfOrDefault(entity.afterSaveProduct),
            afterAddShopping = AfterAddShopping.valueOfOrDefault(entity.afterAddShopping),
            afterProductCompleted = AfterProductCompleted.valueOfOrDefault(entity.afterProductCompleted, entity.editProductAfterCompleted),
            afterShoppingCompleted = AfterShoppingCompleted.valueOfOrDefault(entity.afterShoppingCompleted),
            displayEmptyShoppings = toDisplayEmptyShoppingsOrDefault(entity.displayEmptyShoppings),
            swipeProductLeft = SwipeProduct.valueOfOrDefault(entity.swipeProductLeft),
            swipeProductRight = SwipeProduct.valueOfOrDefault(entity.swipeProductRight),
            swipeShoppingLeft = SwipeShopping.valueOfOrDefault(entity.swipeShoppingLeft),
            swipeShoppingRight = SwipeShopping.valueOfOrDefault(entity.swipeShoppingRight)
        )
    }

    private fun toUserCodeVersion(entity: AppBuildConfigEntity): Int {
        return entity.userCodeVersion ?: if (entity.appFirstTime == "NOTHING") {
            AppBuildConfig.CODE_VERSION_18
        } else {
            if (entity.codeVersion14 == true) {
                AppBuildConfig.CODE_VERSION_14
            } else {
                AppBuildConfig.UNKNOWN_CODE_VERSION
            }
        }
    }

    private fun toScreenDpOrDefault(value: Int?): Int {
        return value ?: DeviceConfig.UNKNOWN_SIZE_DP
    }

    private fun toNightThemeOrDefault(
        appNightTheme: Boolean?,
        widgetNightTheme: Boolean?
    ): NightTheme {
        return NightTheme.valueOfOrDefault(appNightTheme, widgetNightTheme)
    }

    private fun toMultiColumnsOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.MULTI_COLUMNS
    }

    private fun toStrikethroughCompletedProductsOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.STRIKETHROUGH_COMPLETED_PRODUCTS
    }

    private fun toShoppingsSortAscending(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.SORT.ascending
    }

    private fun toShoppingsSortFormatted(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.SORT_FORMATTED
    }

    private fun toDisplayLongTotalOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_LONG_TOTAL
    }

    private fun toDisplayOtherFieldsOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_OTHER_FIELDS
    }

    private fun toColoredCheckboxOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.COLORED_CHECKBOX
    }

    private fun toPurchasesSeparatorOrDefault(value: String?): String {
        return value ?: UserPreferencesDefaults.PURCHASES_SEPARATOR
    }

    private fun toEditProductAfterCompletedOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.EDIT_PRODUCT_AFTER_COMPLETED
    }

    private fun toCompletedWithCheckboxOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.COMPLETED_WITH_CHECKBOX
    }

    private fun toEnterToSaveProductOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.ENTER_TO_SAVE_PRODUCTS
    }

    private fun toDisplayDefaultAutocompletesOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_DEFAULT_AUTOCOMPLETES
    }

    private fun toMaxAutocompleteNamesOrDefault(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_NAMES
    }

    private fun toMaxAutocompleteQuantitiesOrDefault(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_QUANTITIES
    }

    private fun toMaxAutocompleteMoneysOrDefault(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_MONEYS
    }

    private fun toMaxAutocompleteOthersOrDefault(value: Int?): Int {
        return value ?: UserPreferencesDefaults.MAX_AUTOCOMPLETES_OTHERS
    }

    private fun toSaveProductToAutocompletesOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.SAVE_PRODUCT_TO_AUTOCOMPLETES
    }

    private fun toDisplayMoneyOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_MONEY
    }

    private fun toAutomaticallyEmptyTrashOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.AUTOMATICALLY_EMPTY_TRASH
    }

    private fun toDisplayListOfAutocompletesOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.LIST_OF_AUTOCOMPLETES
    }

    private fun toDisplayEmptyShoppingsOrDefault(value: Boolean?): Boolean {
        return value ?: UserPreferencesDefaults.DISPLAY_EMPTY_SHOPPINGS
    }

    private fun toCurrencyOrDefault(entity: UserPreferencesEntity): Currency {
        val symbol = entity.currency
        val displayToLeft = entity.displayCurrencyToLeft
        return if (symbol == null || displayToLeft == null) {
            UserPreferencesDefaults.getCurrency()
        } else {
            Currency(symbol, displayToLeft)
        }
    }

    private fun toTaxRateOrDefault(entity: UserPreferencesEntity): Money {
        val value = entity.taxRate
        val asPercent = entity.taxRateAsPercent
        return if (value == null || asPercent == null) {
            UserPreferencesDefaults.getTaxRate()
        } else {
            val currency = toCurrencyOrDefault(entity)
            Money(
                value = value,
                currency = currency,
                asPercent = asPercent,
                decimalFormat = toMoneyDecimalFormat(entity)
            )
        }
    }

    private fun toMoneyDecimalFormat(entity: UserPreferencesEntity): DecimalFormat {
        return UserPreferencesDefaults.getMoneyDecimalFormat().apply {
            entity.minMoneyFractionDigits?.let { minimumFractionDigits = it }
            entity.maxMoneyFractionDigits?.let { maximumFractionDigits = it }
        }
    }

    private fun toQuantityDecimalFormat(entity: UserPreferencesEntity): DecimalFormat {
        return UserPreferencesDefaults.getQuantityDecimalFormat().apply {
            entity.minQuantityFractionDigits?.let { minimumFractionDigits = it }
            entity.maxQuantityFractionDigits?.let { maximumFractionDigits = it }
        }
    }
}