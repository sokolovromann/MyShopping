package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.Money

data class CodeVersion14Preferences(
    val firstOpened: Boolean = false,
    val currency: Currency = Currency(),
    val taxRate: Money = Money(),
    val fontSize: FontSize = FontSize.DefaultValue,
    val multiColumns: Boolean = false,
    val displayMoney: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val editProductAfterCompleted: Boolean = false,
    val saveProductToAutocompletes: Boolean = false
)