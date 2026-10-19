package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.domain.model.Currency
import ru.sokolovromann.myshopping.core.domain.model.DateTimeFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.KeyboardDisplayDelay
import ru.sokolovromann.myshopping.core.domain.model.MoneyFormattingMode
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.ui.component.DialogContent
import ru.sokolovromann.myshopping.core.ui.component.DialogTitle
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuTitle
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.GeneralSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

@Composable
fun GeneralSettingsCard(
    onAction: (GeneralSettingsAction) -> Unit,
    items: GeneralSettingsItems
) {
    SettingsCard(stringResource(R.string.settings_text_general)) {
        ThemeItem(items.theme) {
            val action = GeneralSettingsAction.OnThemeSelected(it)
            onAction(action)
        }
        FontSizeItem(items.fontSize) {
            val action = GeneralSettingsAction.OnFontSizeSelected(it)
            onAction(action)
        }
        DateTimeItem(items.dateTime) {
            val action = GeneralSettingsAction.OnDateTimeSelected(it)
            onAction(action)
        }
        MoneyItem(items.money) {
            val action = GeneralSettingsAction.OnMoneySelected(it)
            onAction(action)
        }
        CurrencyItem(items.currency) {
            val action = GeneralSettingsAction.OnCurrencySaved(it)
            onAction(action)
        }
        KeyboardDisplayDelayItem(items.keyboardDisplayDelay) {
            val action = GeneralSettingsAction.OnKeyboardDisplayDelaySelected(it)
            onAction(action)
        }
    }
}

// THEME

@Composable
fun ThemeItem(
    data: SettingsItem<Theme>,
    onSelected: (Theme) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_theme)) },
        body = {
            Text(data.text.asCompose())
            ThemeMenu(
                expanded = expanded,
                onSelected = { newTheme ->
                    expanded = false
                    newTheme?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun ThemeMenu(
    expanded: Boolean,
    onSelected: (Theme?) -> Unit,
    selected: Theme
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_theme))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_theme_default),
            selected = selected == Theme.Default,
            onClick = { onSelected(Theme.Default) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_theme_light),
            selected = selected == Theme.Light,
            onClick = { onSelected(Theme.Light) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_theme_dark),
            selected = selected == Theme.Dark,
            onClick = { onSelected(Theme.Dark) }
        )
    }
}

// FONT SIZE

@Composable
fun FontSizeItem(
    data: SettingsItem<FontSize>,
    onSelected: (FontSize) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_font_size)) },
        body = {
            Text(data.text.asCompose())
            FontSizeMenu(
                expanded = expanded,
                onSelected = { newFontSize ->
                    expanded = false
                    newFontSize?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun FontSizeMenu(
    expanded: Boolean,
    onSelected: (FontSize?) -> Unit,
    selected: FontSize
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_font_size))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_font_size_small),
            selected = selected == FontSize.Small,
            onClick = { onSelected(FontSize.Small) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_font_size_medium),
            selected = selected == FontSize.Medium,
            onClick = { onSelected(FontSize.Medium) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_font_size_large),
            selected = selected == FontSize.Large,
            onClick = { onSelected(FontSize.Large) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_font_size_extra_large),
            selected = selected == FontSize.ExtraLarge,
            onClick = { onSelected(FontSize.ExtraLarge) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_font_size_huge),
            selected = selected == FontSize.Huge,
            onClick = { onSelected(FontSize.Huge) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_font_size_extra_huge),
            selected = selected == FontSize.ExtraHuge,
            onClick = { onSelected(FontSize.ExtraHuge) }
        )
    }
}

// DATE TIME

@Composable
private fun DateTimeItem(
    data: SettingsItem<DateTimeFormattingMode>,
    onSelected: (DateTimeFormattingMode) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_date_time_formatting_mode)) },
        body = {
            Text(data.text.asCompose())
            DateTimeMenu(
                expanded = expanded,
                onSelected = { newFormattingMode ->
                    expanded = false
                    newFormattingMode?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun DateTimeMenu(
    expanded: Boolean,
    onSelected: (DateTimeFormattingMode?) -> Unit,
    selected: DateTimeFormattingMode
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_date_time_formatting_mode))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_date_time_formatting_mode_ddmmmyyyy_12),
            selected = selected == DateTimeFormattingMode.DDMMMYYYY(is24hour = false),
            onClick = { onSelected(DateTimeFormattingMode.DDMMMYYYY(is24hour = false)) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_date_time_formatting_mode_ddmmmyyyy_24),
            selected = selected == DateTimeFormattingMode.DDMMMYYYY(is24hour = true),
            onClick = { onSelected(DateTimeFormattingMode.DDMMMYYYY(is24hour = true)) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_date_time_formatting_mode_mmmddyyyy_12),
            selected = selected == DateTimeFormattingMode.MMMDDYYYY(is24hour = false),
            onClick = { onSelected(DateTimeFormattingMode.MMMDDYYYY(is24hour = false)) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_date_time_formatting_mode_mmmddyyyy_24),
            selected = selected == DateTimeFormattingMode.MMMDDYYYY(is24hour = true),
            onClick = { onSelected(DateTimeFormattingMode.MMMDDYYYY(is24hour = true)) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_date_time_formatting_mode_yyyymmmdd_12),
            selected = selected == DateTimeFormattingMode.YYYYMMMDD(is24hour = false),
            onClick = { onSelected(DateTimeFormattingMode.YYYYMMMDD(is24hour = false)) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_date_time_formatting_mode_yyyymmmdd_24),
            selected = selected == DateTimeFormattingMode.YYYYMMMDD(is24hour = true),
            onClick = { onSelected(DateTimeFormattingMode.YYYYMMMDD(is24hour = true)) }
        )
    }
}

// MONEY

@Composable
private fun MoneyItem(
    data: SettingsItem<MoneyFormattingMode>,
    onSelected: (MoneyFormattingMode) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_money_formatting_mode)) },
        body = {
            Text(data.text.asCompose())
            MoneyMenu(
                expanded = expanded,
                onSelected = { newFormattingMode ->
                    expanded = false
                    newFormattingMode?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun MoneyMenu(
    expanded: Boolean,
    onSelected: (MoneyFormattingMode?) -> Unit,
    selected: MoneyFormattingMode
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_money_formatting_mode))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_money_formatting_mode_simple),
            selected = selected == MoneyFormattingMode.Simple,
            onClick = { onSelected(MoneyFormattingMode.Simple) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_money_formatting_mode_advanced),
            selected = selected == MoneyFormattingMode.Advanced,
            onClick = { onSelected(MoneyFormattingMode.Advanced) }
        )
    }
}

// CURRENCY

@Composable
private fun CurrencyItem(
    data: SettingsItem<Currency>,
    onSaved: (Currency) -> Unit
) {
    var displayDialog by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_currency)) },
        body = {
            Text(data.text.asCompose())
            CurrencyDialog(
                display = displayDialog,
                onSaved = { newCurrency ->
                    displayDialog = false
                    newCurrency?.let { onSaved(it) }
                },
                data = data.selected
            )
        },
        onClick = { displayDialog = true }
    )
}

@Composable
private fun CurrencyDialog(
    display: Boolean,
    onSaved: (Currency?) -> Unit,
    data: Currency
) {
    if (!display) return

    var sign by rememberSaveable { mutableStateOf(data.getSign()) }
    var isLeft by rememberSaveable { mutableStateOf(data is Currency.Left) }

    AlertDialog(
        onDismissRequest = { onSaved(null) },
        confirmButton = {
            TextButton(
                onClick = {
                    val newCurrency = if (isLeft) Currency.Left(sign) else Currency.Right(sign)
                    onSaved(newCurrency)
                },
                content = { Text(stringResource(R.string.settings_action_currency_save)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = { onSaved(null) },
                content = { Text(stringResource(R.string.settings_action_currency_cancel)) }
            )
        },
        title = { DialogTitle(stringResource(R.string.settings_text_currency)) },
        text = {
            DialogContent {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = sign,
                        onValueChange = { sign = it },
                        label = { Text(stringResource(R.string.settings_label_currency_symbol)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    DisplayCurrencyButton(
                        onSelected = { isLeft = it },
                        isLeft = isLeft
                    )
                }
            }
        }
    )
}

@Composable
private fun DisplayCurrencyButton(
    onSelected: (Boolean) -> Unit,
    isLeft: Boolean
) {
    val text = if (isLeft) {
        stringResource(R.string.settings_text_currency_left)
    } else {
        stringResource(R.string.settings_text_currency_right)
    }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Button(
        onClick = { expanded = true },
        content = {
            Text(text)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuSelectableItem(
                    text = stringResource(R.string.settings_text_currency_left),
                    selected = isLeft,
                    onClick = {
                        expanded = false
                        onSelected(true)
                    }
                )
                DropdownMenuSelectableItem(
                    text = stringResource(R.string.settings_text_currency_right),
                    selected = !isLeft,
                    onClick = {
                        expanded = false
                        onSelected(false)
                    }
                )
            }
        }
    )
}

// KEYBOARD DISPLAY DELAY

@Composable
private fun KeyboardDisplayDelayItem(
    data: SettingsItem<KeyboardDisplayDelay>,
    onSelected: (KeyboardDisplayDelay) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_keyboard_display_delay)) },
        body = {
            Text(data.text.asCompose())
            KeyboardDisplayDelayMenu(
                expanded = expanded,
                onSelected = { newDelay ->
                    expanded = false
                    newDelay?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun KeyboardDisplayDelayMenu(
    expanded: Boolean,
    onSelected: (KeyboardDisplayDelay?) -> Unit,
    selected: KeyboardDisplayDelay
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_keyboard_display_delay))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_keyboard_display_delay_50ms),
            selected = selected == KeyboardDisplayDelay.Ms50,
            onClick = { onSelected(KeyboardDisplayDelay.Ms50) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_keyboard_display_delay_off),
            selected = selected == KeyboardDisplayDelay.Off,
            onClick = { onSelected(KeyboardDisplayDelay.Off) }
        )
    }
}