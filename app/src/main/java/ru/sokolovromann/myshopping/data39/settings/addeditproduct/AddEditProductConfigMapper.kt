package ru.sokolovromann.myshopping.data39.settings.addeditproduct

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class AddEditProductConfigMapper @Inject constructor() : Mapper<Preferences, AddEditProductConfig>() {

    override fun mapEntityTo(entity: Preferences): AddEditProductConfig {
        return AddEditProductConfig(
            displayFields = mapDisplayFieldsTo(entity),
            lockField = mapLockFieldTo(entity),
            keyboardDisplayDelay = mapKeyboardDisplayDelayTo(entity),
            afterTappingByEnter = mapAfterTappingByEnterTo(entity),
            afterAdding = mapAfterAddingTo(entity),
            afterEditing = mapAfterEditingTo(entity)
        )
    }

    override fun mapEntityFrom(model: AddEditProductConfig): Preferences {
        return mutablePreferencesOf().apply {
            val displayFields = mapDisplayFieldsFrom(model.displayFields)
            plusAssign(displayFields)

            val lockField = mapLockFieldFrom(model.lockField)
            plusAssign(lockField)

            val keyboardDisplayDelay = mapKeyboardDisplayDelayFrom(model.keyboardDisplayDelay)
            plusAssign(keyboardDisplayDelay)

            val afterTappingByEnter = mapAfterTappingByEnterFrom(model.afterTappingByEnter)
            plusAssign(afterTappingByEnter)

            val afterAdding = mapAfterAddingFrom(model.afterAdding)
            plusAssign(afterAdding)

            val afterEditing = mapAfterEditingFrom(model.afterEditing)
            plusAssign(afterEditing)
        }
    }

    fun mapDisplayFieldsTo(entity: Preferences): DisplayAddEditProductFields {
        val default = AddEditProductConfigDefaults.DISPLAY_FIELDS
        return DisplayAddEditProductFields(
            entity[AddEditProductConfigScheme.DISPLAY_NAME] ?: default.name,
            entity[AddEditProductConfigScheme.DISPLAY_IMAGE] ?: default.image,
            entity[AddEditProductConfigScheme.DISPLAY_MANUFACTURER] ?: default.manufacturer,
            entity[AddEditProductConfigScheme.DISPLAY_BRAND] ?: default.brand,
            entity[AddEditProductConfigScheme.DISPLAY_SIZE] ?: default.size,
            entity[AddEditProductConfigScheme.DISPLAY_COLOR] ?: default.color,
            entity[AddEditProductConfigScheme.DISPLAY_QUANTITY] ?: default.quantity,
            entity[AddEditProductConfigScheme.DISPLAY_PLUS_MINUS_ONE_QUANTITY] ?: default.plusMinusOneQuantity,
            entity[AddEditProductConfigScheme.DISPLAY_PRICE] ?: default.price,
            entity[AddEditProductConfigScheme.DISPLAY_DISCOUNT] ?: default.discount,
            entity[AddEditProductConfigScheme.DISPLAY_TAX_RATE] ?: default.taxRate,
            entity[AddEditProductConfigScheme.DISPLAY_COST] ?: default.cost,
            entity[AddEditProductConfigScheme.DISPLAY_NOTE] ?: default.note,
            entity[AddEditProductConfigScheme.DISPLAY_ID] ?: default.id,
            entity[AddEditProductConfigScheme.DISPLAY_CREATED] ?: default.created,
            entity[AddEditProductConfigScheme.DISPLAY_LAST_MODIFIED] ?: default.lastModified
        )
    }

    fun mapDisplayFieldsFrom(model: DisplayAddEditProductFields): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.DISPLAY_NAME to model.name,
            AddEditProductConfigScheme.DISPLAY_IMAGE to model.image,
            AddEditProductConfigScheme.DISPLAY_MANUFACTURER to model.manufacturer,
            AddEditProductConfigScheme.DISPLAY_BRAND to model.brand,
            AddEditProductConfigScheme.DISPLAY_SIZE to model.size,
            AddEditProductConfigScheme.DISPLAY_COLOR to model.color,
            AddEditProductConfigScheme.DISPLAY_QUANTITY to model.quantity,
            AddEditProductConfigScheme.DISPLAY_PLUS_MINUS_ONE_QUANTITY to model.plusMinusOneQuantity,
            AddEditProductConfigScheme.DISPLAY_PRICE to model.price,
            AddEditProductConfigScheme.DISPLAY_DISCOUNT to model.discount,
            AddEditProductConfigScheme.DISPLAY_TAX_RATE to model.taxRate,
            AddEditProductConfigScheme.DISPLAY_COST to model.cost,
            AddEditProductConfigScheme.DISPLAY_NOTE to model.note,
            AddEditProductConfigScheme.DISPLAY_ID to model.id,
            AddEditProductConfigScheme.DISPLAY_CREATED to model.created,
            AddEditProductConfigScheme.DISPLAY_LAST_MODIFIED to model.lastModified,
        )
    }

    fun mapLockFieldTo(entity: Preferences): LockAddEditProductField {
        return EnumExtensions.valueOfOrDefault(
            entity[AddEditProductConfigScheme.LOCK_FIELD],
            AddEditProductConfigDefaults.LOCK_FIELD
        )
    }

    fun mapLockFieldFrom(model: LockAddEditProductField): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.LOCK_FIELD to model.name,
        )
    }

    fun mapKeyboardDisplayDelayTo(entity: Preferences): ProductKeyboardDisplayDelay {
        return EnumExtensions.valueOfOrDefault(
            entity[AddEditProductConfigScheme.KEYBOARD_DISPLAY_DELAY],
            AddEditProductConfigDefaults.KEYBOARD_DISPLAY_DELAY
        )
    }

    fun mapKeyboardDisplayDelayFrom(model: ProductKeyboardDisplayDelay): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.KEYBOARD_DISPLAY_DELAY to model.name
        )
    }

    fun mapAfterTappingByEnterTo(entity: Preferences): AfterTappingByProductEnter {
        return EnumExtensions.valueOfOrDefault(
            entity[AddEditProductConfigScheme.AFTER_TAPPING_BY_ENTER],
            AddEditProductConfigDefaults.AFTER_TAPPING_BY_ENTER
        )
    }

    fun mapAfterTappingByEnterFrom(model: AfterTappingByProductEnter): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.AFTER_TAPPING_BY_ENTER to model.name
        )
    }

    fun mapAfterAddingTo(entity: Preferences): AfterAddingProduct {
        return EnumExtensions.valueOfOrDefault(
            entity[AddEditProductConfigScheme.AFTER_ADDING],
            AddEditProductConfigDefaults.AFTER_ADDING
        )
    }

    fun mapAfterAddingFrom(model:  AfterAddingProduct): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.AFTER_ADDING to model.name
        )
    }

    fun mapAfterEditingTo(entity: Preferences): AfterEditingProduct {
        return EnumExtensions.valueOfOrDefault(
            entity[AddEditProductConfigScheme.AFTER_EDITING],
            AddEditProductConfigDefaults.AFTER_EDITING
        )
    }

    fun mapAfterEditingFrom(model: AfterEditingProduct): Preferences {
        return preferencesOf(
            AddEditProductConfigScheme.AFTER_EDITING to model.name
        )
    }
}