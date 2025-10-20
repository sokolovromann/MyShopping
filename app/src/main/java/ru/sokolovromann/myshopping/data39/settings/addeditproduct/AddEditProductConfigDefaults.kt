package ru.sokolovromann.myshopping.data39.settings.addeditproduct

object AddEditProductConfigDefaults {
    val DISPLAY_FIELDS: DisplayAddEditProductFields = DisplayAddEditProductFields(
        name = true,
        image = false,
        manufacturer = false,
        brand = false,
        size = false,
        color = false,
        quantity = true,
        plusMinusOneQuantity = true,
        unitPrice = true,
        discount = false,
        taxRate = false,
        cost = true,
        note = true,
        id = false,
        created = false,
        lastModified = false
    )
    val LOCK_FIELD: LockAddEditProductField = LockAddEditProductField.Total
    val KEYBOARD_DISPLAY_DELAY: ProductKeyboardDisplayDelay = ProductKeyboardDisplayDelay.Ms50
    val AFTER_TAPPING_BY_ENTER: AfterTappingByProductEnter = AfterTappingByProductEnter.GoToNextField
    val AFTER_ADDING: AfterAddingProduct = AfterAddingProduct.CloseScreen
    val AFTER_EDITING: AfterEditingProduct = AfterEditingProduct.CloseScreen
}