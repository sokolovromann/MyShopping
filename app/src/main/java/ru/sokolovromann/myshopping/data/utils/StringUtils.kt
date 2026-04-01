package ru.sokolovromann.myshopping.data.utils

fun String.asSearchQuery(): String {
    return this.lowercase()
}