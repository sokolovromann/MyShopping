package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class GeneralSettingsItems(
    val theme: SettingsItem<Theme> = SettingsItem(UserPreferencesDefaults.General.theme),
    val fontSize: SettingsItem<FontSize> = SettingsItem(UserPreferencesDefaults.General.fontSize),
    val dateTime: SettingsItem<DateTimeFormattingMode> = SettingsItem(UserPreferencesDefaults.General.dateTimeFormattingMode),
    val money: SettingsItem<MoneyFormattingMode> = SettingsItem(UserPreferencesDefaults.General.moneyFormattingMode),
    val currency: SettingsItem<Currency> = SettingsItem(UserPreferencesDefaults.General.currency),
    val keyboardDisplayDelay: SettingsItem<KeyboardDisplayDelay> = SettingsItem(UserPreferencesDefaults.General.keyboardDisplayDelay)
)