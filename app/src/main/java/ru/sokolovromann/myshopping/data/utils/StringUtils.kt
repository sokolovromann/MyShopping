package ru.sokolovromann.myshopping.data.utils

fun String.uppercaseFirst(): String {
    return if (this.isEmpty()) {
        this
    } else {
        this.replaceFirst(
            oldValue = this.first().toString(),
            newValue = this.first().uppercase()
        )
    }
}

fun String.asSearchQuery(): String {
    return this.lowercase()
}