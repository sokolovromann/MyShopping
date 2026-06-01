package ru.sokolovromann.myshopping.core.domain.model

data class AddEditProductPreferences(
    val lockField: LockProductField,
    val afterTappingByEnter: AfterTappingByProductEnter,
    val afterAdding: AfterAddingProduct,
    val afterEditing: AfterEditingProduct,
    val tax: Tax?
)