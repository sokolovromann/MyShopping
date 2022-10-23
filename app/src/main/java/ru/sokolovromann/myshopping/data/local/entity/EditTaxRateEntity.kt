package ru.sokolovromann.myshopping.data.local.entity

data class EditTaxRateEntity(
    val taxRate: Float = 0f,
    val taxRateAsPercent: Boolean = false
)