package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiGeneralPreferences(
    val theme: UiPreference<Theme> = UiPreference(UserPreferencesDefaults.General.theme),
    val fontSize: UiPreference<FontSize> = UiPreference(UserPreferencesDefaults.General.fontSize),
    val money: UiPreference<MoneyFormattingMode> = UiPreference(UserPreferencesDefaults.General.moneyFormattingMode),
    val currency: UiPreference<Currency> = UiPreference(UserPreferencesDefaults.General.currency)
)