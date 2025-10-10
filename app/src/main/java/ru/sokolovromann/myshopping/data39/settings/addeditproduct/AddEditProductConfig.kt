package ru.sokolovromann.myshopping.data39.settings.addeditproduct

data class AddEditProductConfig(
    val displayFields: DisplayAddEditProductFields,
    val lockField: LockAddEditProductField,
    val keyboardDisplayDelay: ProductKeyboardDisplayDelay,
    val afterTappingByEnter: AfterTappingByProductEnter,
    val afterAdding: AfterAddingProduct,
    val afterEditing: AfterEditingProduct
)