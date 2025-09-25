package ru.sokolovromann.myshopping.settings.addeditproduct

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.io.TwoSidedMapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class AddEditProductConfigMapper @Inject constructor() : TwoSidedMapper<Preferences, AddEditProductConfig>() {

    override fun mapTo(a: Preferences): AddEditProductConfig {
        val defaultDisplayFields = AddEditProductConfigDefaults.DISPLAY_FIELDS
        val displayFields = DisplayAddEditProductFields(
            name = a[AddEditProductConfigScheme.DISPLAY_NAME] ?: defaultDisplayFields.name,
            image = a[AddEditProductConfigScheme.DISPLAY_IMAGE] ?: defaultDisplayFields.image,
            manufacturer = a[AddEditProductConfigScheme.DISPLAY_MANUFACTURER] ?: defaultDisplayFields.manufacturer,
            brand = a[AddEditProductConfigScheme.DISPLAY_BRAND] ?: defaultDisplayFields.brand,
            size = a[AddEditProductConfigScheme.DISPLAY_SIZE] ?: defaultDisplayFields.size,
            color = a[AddEditProductConfigScheme.DISPLAY_COLOR] ?: defaultDisplayFields.color,
            quantity = a[AddEditProductConfigScheme.DISPLAY_QUANTITY] ?: defaultDisplayFields.quantity,
            minusAndPlusOneQuantity = a[AddEditProductConfigScheme.DISPLAY_MINUS_AND_PLUS_ONE_QUANTITY] ?: defaultDisplayFields.minusAndPlusOneQuantity,
            price = a[AddEditProductConfigScheme.DISPLAY_PRICE] ?: defaultDisplayFields.price,
            discount = a[AddEditProductConfigScheme.DISPLAY_DISCOUNT] ?: defaultDisplayFields.discount,
            taxRate = a[AddEditProductConfigScheme.DISPLAY_TAX_RATE] ?: defaultDisplayFields.taxRate,
            cost = a[AddEditProductConfigScheme.DISPLAY_COST] ?: defaultDisplayFields.cost,
            note = a[AddEditProductConfigScheme.DISPLAY_NOTE] ?: defaultDisplayFields.note,
            id = a[AddEditProductConfigScheme.DISPLAY_ID] ?: defaultDisplayFields.id,
            created = a[AddEditProductConfigScheme.DISPLAY_CREATED] ?: defaultDisplayFields.created,
            lastModified = a[AddEditProductConfigScheme.DISPLAY_LAST_MODIFIED] ?: defaultDisplayFields.lastModified
        )
        val lockField: LockAddEditProductField = EnumExtensions.valueOfOrDefault(
            name = a[AddEditProductConfigScheme.LOCK_FIELD],
            defaultValue = AddEditProductConfigDefaults.LOCK_FIELD
        )
        val keyboardDisplayDelay: ProductKeyboardDisplayDelay = EnumExtensions.valueOfOrDefault(
            name = a[AddEditProductConfigScheme.KEYBOARD_DISPLAY_DELAY],
            defaultValue = AddEditProductConfigDefaults.KEYBOARD_DISPLAY_DELAY
        )
        val afterTappingByEnter: AfterTappingByProductEnter = EnumExtensions.valueOfOrDefault(
            name = a[AddEditProductConfigScheme.AFTER_TAPPING_BY_ENTER],
            defaultValue = AddEditProductConfigDefaults.AFTER_TAPPING_BY_ENTER
        )
        val afterAdding: AfterAddingProduct = EnumExtensions.valueOfOrDefault(
            name = a[AddEditProductConfigScheme.AFTER_ADDING],
            defaultValue = AddEditProductConfigDefaults.AFTER_ADDING
        )
        val afterEditing: AfterEditingProduct = EnumExtensions.valueOfOrDefault(
            name = a[AddEditProductConfigScheme.AFTER_EDITING],
            defaultValue = AddEditProductConfigDefaults.AFTER_EDITING
        )
        return AddEditProductConfig(
            displayFields = displayFields,
            lockField = lockField,
            keyboardDisplayDelay = keyboardDisplayDelay,
            afterTappingByEnter = afterTappingByEnter,
            afterAdding = afterAdding,
            afterEditing = afterEditing
        )
    }

    override fun mapFrom(b: AddEditProductConfig): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.DISPLAY_NAME to b.displayFields.name,
            AddEditProductConfigScheme.DISPLAY_IMAGE to b.displayFields.image,
            AddEditProductConfigScheme.DISPLAY_MANUFACTURER to b.displayFields.manufacturer,
            AddEditProductConfigScheme.DISPLAY_BRAND to b.displayFields.brand,
            AddEditProductConfigScheme.DISPLAY_SIZE to b.displayFields.size,
            AddEditProductConfigScheme.DISPLAY_COLOR to b.displayFields.color,
            AddEditProductConfigScheme.DISPLAY_QUANTITY to b.displayFields.quantity,
            AddEditProductConfigScheme.DISPLAY_MINUS_AND_PLUS_ONE_QUANTITY to b.displayFields.minusAndPlusOneQuantity,
            AddEditProductConfigScheme.DISPLAY_PRICE to b.displayFields.price,
            AddEditProductConfigScheme.DISPLAY_DISCOUNT to b.displayFields.discount,
            AddEditProductConfigScheme.DISPLAY_TAX_RATE to b.displayFields.taxRate,
            AddEditProductConfigScheme.DISPLAY_COST to b.displayFields.cost,
            AddEditProductConfigScheme.DISPLAY_NOTE to b.displayFields.note,
            AddEditProductConfigScheme.DISPLAY_ID to b.displayFields.id,
            AddEditProductConfigScheme.DISPLAY_CREATED to b.displayFields.created,
            AddEditProductConfigScheme.DISPLAY_LAST_MODIFIED to b.displayFields.lastModified,
            AddEditProductConfigScheme.LOCK_FIELD to b.lockField.name,
            AddEditProductConfigScheme.KEYBOARD_DISPLAY_DELAY to b.keyboardDisplayDelay.name,
            AddEditProductConfigScheme.AFTER_TAPPING_BY_ENTER to b.afterTappingByEnter.name,
            AddEditProductConfigScheme.AFTER_ADDING to b.afterAdding.name,
            AddEditProductConfigScheme.AFTER_EDITING to b.afterEditing.name
        )
    }
}