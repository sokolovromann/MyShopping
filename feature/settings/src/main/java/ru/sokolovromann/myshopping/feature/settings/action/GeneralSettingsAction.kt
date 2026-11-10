package ru.sokolovromann.myshopping.feature.settings.action

import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme

sealed class GeneralSettingsAction {

    data class OnThemeSelected(val theme: Theme) : GeneralSettingsAction()

    data class OnFontSizeSelected(val fontSize: FontSize) : GeneralSettingsAction()

    data class OnMoneySelected(val formattingMode: MoneyFormattingMode) : GeneralSettingsAction()

    data class OnCurrencySaved(val currency: Currency) : GeneralSettingsAction()
}