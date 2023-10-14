package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Id

data class AutocompleteWithConfig(
    val autocomplete: Autocomplete = Autocomplete(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return autocomplete.id == Id.NO_ID
    }
}