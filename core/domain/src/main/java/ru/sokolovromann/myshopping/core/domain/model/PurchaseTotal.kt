package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

sealed class PurchaseTotal {

    data class Calculated(val total: CartTotal) : PurchaseTotal()

    data class Edited(val total: CartTotal) : PurchaseTotal()

    fun getMoney(): BigDecimal = when (this) {
        is Calculated -> total.money
        is Edited -> total.money
    }
}