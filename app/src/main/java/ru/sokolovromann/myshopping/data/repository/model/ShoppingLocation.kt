package ru.sokolovromann.myshopping.data.repository.model

enum class ShoppingLocation {

    PURCHASES, ARCHIVE, TRASH;

    companion object {

        fun create(archived: Boolean, deleted: Boolean): ShoppingLocation {
            return if (deleted) {
                TRASH
            } else {
                if (archived) ARCHIVE else PURCHASES
            }
        }
    }

    override fun toString(): String {
        return name
    }
}