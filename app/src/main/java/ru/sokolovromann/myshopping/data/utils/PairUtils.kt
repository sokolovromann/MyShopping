package ru.sokolovromann.myshopping.data.utils

fun <E> Pair<List<E>, List<E>>.toSingleList(): List<E> {
    return first.toMutableList().apply { addAll(second) }
}