package ru.sokolovromann.myshopping.data.model

data class AutocompleteWithConfig(
    val autocomplete: Autocomplete = Autocomplete(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return autocomplete.id == IdDefaults.NO_ID
    }
}