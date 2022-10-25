package ru.sokolovromann.myshopping.data.repository.model

data class ShoppingListNotifications(
    private val shoppingLists: List<ShoppingList>
) {

    fun reminders(): List<Pair<String, Long>> {
        return shoppingLists.map {
            Pair(it.uid, it.reminder ?: 0L)
        }
    }
}