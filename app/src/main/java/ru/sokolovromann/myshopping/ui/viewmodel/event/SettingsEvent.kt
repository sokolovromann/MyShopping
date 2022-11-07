package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class SettingsEvent {

    object EditCurrencySymbol : SettingsEvent()

    object EditTaxRate : SettingsEvent()

    object SelectFontSize : SettingsEvent()

    object SelectProductsDisplayAutocomplete : SettingsEvent()

    object DisplayProductsAllAutocomplete : SettingsEvent()

    object DisplayProductsNameAutocomplete : SettingsEvent()

    object TinyFontSizeSelected : SettingsEvent()

    object SmallFontSizeSelected : SettingsEvent()

    object MediumFontSizeSelected : SettingsEvent()

    object LargeFontSizeSelected : SettingsEvent()

    object HugeFontSizeSelected : SettingsEvent()

    object InvertNightTheme : SettingsEvent()

    object InvertDisplayMoney : SettingsEvent()

    object InvertDisplayCurrencyToLeft : SettingsEvent()

    object InvertFirstLetterUppercase : SettingsEvent()

    object InvertShoppingListsMultiColumns : SettingsEvent()

    object InvertProductsMultiColumns : SettingsEvent()

    object InvertProductsEditCompleted : SettingsEvent()

    object InvertProductsAddLastProducts : SettingsEvent()

    object SendEmailToDeveloper : SettingsEvent()

    object ShowBackScreen : SettingsEvent()

    object ShowNavigationDrawer : SettingsEvent()

    object ShowAppGithub : SettingsEvent()

    object HideFontSize : SettingsEvent()

    object HideNavigationDrawer : SettingsEvent()

    object HideProductsAutocomplete : SettingsEvent()

    object HideProductsDisplayAutocomplete : SettingsEvent()
}