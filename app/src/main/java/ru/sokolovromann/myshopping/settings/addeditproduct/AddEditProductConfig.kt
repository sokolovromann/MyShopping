package ru.sokolovromann.myshopping.settings.addeditproduct

data class AddEditProductConfig(
    val displayFields: DisplayAddEditProductFields,
    val lockField: LockAddEditProductField,
    val keyboardDisplayDelay: ProductKeyboardDisplayDelay,
    val afterTappingByEnter: AfterTappingByProductEnter,
    val afterAdding: AfterAddingProduct,
    val afterEditing: AfterEditingProduct
)