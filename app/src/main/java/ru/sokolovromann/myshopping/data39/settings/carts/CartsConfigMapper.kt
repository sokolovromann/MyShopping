package ru.sokolovromann.myshopping.data39.settings.carts

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject
import kotlin.text.orEmpty

class CartsConfigMapper @Inject constructor() : Mapper<Preferences, CartsConfig>() {

    override fun mapEntityTo(entity: Preferences): CartsConfig {
        return CartsConfig(
            viewMode = mapViewModeTo(entity),
            sort = mapSortTo(entity),
            group = mapGroupTo(entity),
            calculateTotal = mapCalculateTotalTo(entity),
            afterAdding = mapAfterAddingTo(entity),
            afterCompleting = mapAfterCompletingTo(entity),
            afterArchiving = mapAfterArchivingTo(entity),
            afterTappingByCheckbox = mapAfterTappingByCheckboxTo(entity),
            checkboxesColor = mapCheckboxesColorTo(entity),
            swipeLeft = mapSwipeLeftTo(entity),
            swipeRight = mapSwipeRightTo(entity),
            emptyCarts = mapEmptyCartsTo(entity),
            deletionFromTrash = mapDeletionFromTrashTo(entity)
        )
    }

    override fun mapEntityFrom(model: CartsConfig): Preferences {
        return mutablePreferencesOf().apply {
            val viewMode = mapViewModeFrom(model.viewMode)
            plusAssign(viewMode)

            val sort = mapSortFrom(model.sort)
            plusAssign(sort)

            val group = mapGroupFrom(model.group)
            plusAssign(group)

            val calculateTotal = mapCalculateTotalFrom(model.calculateTotal)
            plusAssign(calculateTotal)

            val afterAdding = mapAfterAddingFrom(model.afterAdding)
            plusAssign(afterAdding)

            val afterCompleting = mapAfterCompletingFrom(model.afterCompleting)
            plusAssign(afterCompleting)

            val afterArchiving = mapAfterArchivingFrom(model.afterArchiving)
            plusAssign(afterArchiving)

            val afterTappingByCheckbox = mapAfterTappingByCheckboxFrom(model.afterTappingByCheckbox)
            plusAssign(afterTappingByCheckbox)

            val checkboxesColor = mapCheckboxesColorFrom(model.checkboxesColor)
            plusAssign(checkboxesColor)

            val swipeLeft = mapSwipeLeftFrom(model.swipeLeft)
            plusAssign(swipeLeft)

            val swipeRight = mapSwipeRightFrom(model.swipeRight)
            plusAssign(swipeRight)

            val emptyCarts = mapEmptyCartsFrom(model.emptyCarts)
            plusAssign(emptyCarts)

            val deletionFromTrash = mapDeletionFromTrashFrom(model.deletionFromTrash)
            plusAssign(deletionFromTrash)
        }
    }

    fun mapViewModeTo(entity: Preferences): CartsViewMode {
        val default = CartsConfigDefaults.VIEW_MODE
        val name: CartsViewModeName = EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.VIEW_MODE],
            default.name
        )
        val params: CartsViewModeParams = EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.VIEW_MODE_PARAMS],
            default.params
        )
        return CartsViewMode(name, params)
    }

    fun mapViewModeFrom(model: CartsViewMode): Preferences {
        return preferencesOf(
            CartsConfigScheme.VIEW_MODE to model.name.name,
            CartsConfigScheme.VIEW_MODE_PARAMS to model.params.name,
        )
    }

    fun mapSortTo(entity: Preferences): SortCarts {
        val default = CartsConfigDefaults.SORT
        val name: SortCartsName = EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.SORT],
            default.name
        )
        val params: SortCartsParams? = EnumExtensions.valueOfOrNull<SortCartsParams>(
            entity[CartsConfigScheme.SORT_PARAMS]
        )
        return SortCarts(name, params)
    }

    fun mapSortFrom(model: SortCarts): Preferences {
        return preferencesOf(
            CartsConfigScheme.SORT to model.name.name,
            CartsConfigScheme.SORT_PARAMS to model.params?.name.orEmpty()
        )
    }

    fun mapGroupTo(entity: Preferences): GroupCarts {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.GROUP],
            CartsConfigDefaults.GROUP
        )
    }

    fun mapGroupFrom(model: GroupCarts): Preferences {
        return preferencesOf(
            CartsConfigScheme.GROUP to model.name
        )
    }

    fun mapCalculateTotalTo(entity: Preferences): CalculateCartsTotal {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.CALCULATE_TOTAL],
            CartsConfigDefaults.CALCULATE_TOTAL
        )
    }

    fun mapCalculateTotalFrom(model: CalculateCartsTotal): Preferences {
        return preferencesOf(
            CartsConfigScheme.CALCULATE_TOTAL to model.name
        )
    }

    fun mapAfterAddingTo(entity: Preferences): AfterAddingCart {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.AFTER_ADDING],
            CartsConfigDefaults.AFTER_ADDING
        )
    }

    fun mapAfterAddingFrom(model: AfterAddingCart): Preferences {
        return preferencesOf(
            CartsConfigScheme.AFTER_ADDING to model.name
        )
    }

    fun mapAfterCompletingTo(entity: Preferences): AfterCompletingCart {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.AFTER_COMPLETING],
            CartsConfigDefaults.AFTER_COMPLETING
        )
    }

    fun mapAfterCompletingFrom(model: AfterCompletingCart): Preferences {
        return preferencesOf(
            CartsConfigScheme.AFTER_COMPLETING to model.name
        )
    }

    fun mapAfterArchivingTo(entity: Preferences): AfterArchivingCart {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.AFTER_ARCHIVING],
            CartsConfigDefaults.AFTER_ARCHIVING
        )
    }

    fun mapAfterArchivingFrom(model: AfterArchivingCart): Preferences {
        return preferencesOf(
            CartsConfigScheme.AFTER_ARCHIVING to model.name
        )
    }

    fun mapAfterTappingByCheckboxTo(entity: Preferences): AfterTappingByCartCheckbox {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.AFTER_TAPPING_BY_CHECKBOX],
            CartsConfigDefaults.AFTER_TAPPING_BY_CHECKBOX
        )
    }

    fun mapAfterTappingByCheckboxFrom(model: AfterTappingByCartCheckbox): Preferences {
        return preferencesOf(
            CartsConfigScheme.AFTER_TAPPING_BY_CHECKBOX to model.name
        )
    }

    fun mapCheckboxesColorTo(entity: Preferences): CartsCheckboxesColor {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.CHECKBOXES_COLOR],
            CartsConfigDefaults.CHECKBOXES_COLOR
        )
    }

    fun mapCheckboxesColorFrom(model: CartsCheckboxesColor): Preferences {
        return preferencesOf(
            CartsConfigScheme.CHECKBOXES_COLOR to model.name
        )
    }

    fun mapSwipeLeftTo(entity: Preferences): SwipeCart {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.SWIPE_LEFT],
            CartsConfigDefaults.SWIPE_LEFT
        )
    }

    fun mapSwipeLeftFrom(model: SwipeCart): Preferences {
        return preferencesOf(
            CartsConfigScheme.SWIPE_LEFT to model.name
        )
    }

    fun mapSwipeRightTo(entity: Preferences): SwipeCart {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.SWIPE_RIGHT],
            CartsConfigDefaults.SWIPE_RIGHT
        )
    }

    fun mapSwipeRightFrom(model: SwipeCart): Preferences {
        return preferencesOf(
            CartsConfigScheme.SWIPE_RIGHT to model.name
        )
    }

    fun mapEmptyCartsTo(entity: Preferences): EmptyCarts {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.EMPTY_CARTS],
            CartsConfigDefaults.EMPTY_CARTS
        )
    }

    fun mapEmptyCartsFrom(model: EmptyCarts): Preferences {
        return preferencesOf(
            CartsConfigScheme.EMPTY_CARTS to model.name
        )
    }

    fun mapDeletionFromTrashTo(entity: Preferences): DeletionCartsFromTrash {
        return EnumExtensions.valueOfOrDefault(
            entity[CartsConfigScheme.DELETION_FROM_TRASH],
            CartsConfigDefaults.DELETION_FROM_TRASH
        )
    }

    fun mapDeletionFromTrashFrom(model: DeletionCartsFromTrash): Preferences {
        return preferencesOf(
            CartsConfigScheme.DELETION_FROM_TRASH to model.name
        )
    }
}