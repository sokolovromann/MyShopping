package ru.sokolovromann.myshopping.utils.math

enum class DiscountType {

    Percent,

    Money;

    override fun toString(): String {
        return name
    }
}