package ru.sokolovromann.myshopping.data.repository.model

@Deprecated("Use /model/CodeVersion14")
data class CodeVersion14(
    val shoppingLists: List<ShoppingList> = listOf(),
    val autocompletes: List<Autocomplete> = listOf(),
    val preferences: CodeVersion14Preferences = CodeVersion14Preferences()
) {
    companion object {
        const val CODE_VERSION = 14
    }
}