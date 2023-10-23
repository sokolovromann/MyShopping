package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.LockProductElement
import java.text.DecimalFormat

data class UserPreferences(
    val nightTheme: Boolean = UserPreferencesDefaults.NIGHT_THEME,
    val fontSize: FontSize = UserPreferencesDefaults.FONT_SIZE,
    val shoppingsMultiColumns: Boolean = UserPreferencesDefaults.MULTI_COLUMNS,
    val productsMultiColumns: Boolean = UserPreferencesDefaults.MULTI_COLUMNS,
    val displayCompleted: DisplayCompleted = UserPreferencesDefaults.DISPLAY_COMPLETED,
    val displayTotal: DisplayTotal = UserPreferencesDefaults.DISPLAY_TOTAL,
    val displayOtherFields: Boolean = UserPreferencesDefaults.DISPLAY_OTHER_FIELDS,
    val coloredCheckbox: Boolean = UserPreferencesDefaults.COLORED_CHECKBOX,
    val displayShoppingsProducts: DisplayProducts = UserPreferencesDefaults.DISPLAY_SHOPPINGS_PRODUCTS,
    val purchasesSeparator: String = UserPreferencesDefaults.PURCHASES_SEPARATOR,
    val editProductAfterCompleted: Boolean = UserPreferencesDefaults.EDIT_PRODUCT_AFTER_COMPLETED,
    val lockProductElement: LockProductElement = UserPreferencesDefaults.LOCK_PRODUCT_ELEMENT,
    val completedWithCheckbox: Boolean = UserPreferencesDefaults.COMPLETED_WITH_CHECKBOX,
    val enterToSaveProduct: Boolean = UserPreferencesDefaults.ENTER_TO_SAVE_PRODUCTS,
    val displayDefaultAutocompletes: Boolean = UserPreferencesDefaults.DISPLAY_DEFAULT_AUTOCOMPLETES,
    val maxAutocompletesNames: Int = UserPreferencesDefaults.MAX_AUTOCOMPLETES_NAMES,
    val maxAutocompletesQuantities: Int = UserPreferencesDefaults.MAX_AUTOCOMPLETES_QUANTITIES,
    val maxAutocompletesMoneys: Int = UserPreferencesDefaults.MAX_AUTOCOMPLETES_MONEYS,
    val maxAutocompletesOthers: Int = UserPreferencesDefaults.MAX_AUTOCOMPLETES_OTHERS,
    val saveProductToAutocompletes: Boolean = UserPreferencesDefaults.SAVE_PRODUCT_TO_AUTOCOMPLETES,
    val displayMoney: Boolean = UserPreferencesDefaults.DISPLAY_MONEY,
    val currency: Currency = UserPreferencesDefaults.getCurrency(),
    val taxRate: Money = UserPreferencesDefaults.getTaxRate(),
    val moneyDecimalFormat: DecimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat(),
    val quantityDecimalFormat: DecimalFormat = UserPreferencesDefaults.getQuantityDecimalFormat()
)