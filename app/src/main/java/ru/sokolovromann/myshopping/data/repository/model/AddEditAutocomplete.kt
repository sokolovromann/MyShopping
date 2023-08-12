package ru.sokolovromann.myshopping.data.repository.model

data class AddEditAutocomplete(
    val autocomplete: Autocomplete? = null,
    val appConfig: AppConfig = AppConfig()
)