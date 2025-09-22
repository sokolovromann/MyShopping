package ru.sokolovromann.myshopping.settings.carts

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.io.TwoSidedMapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject
import kotlin.text.orEmpty

class CartsConfigMapper @Inject constructor() : TwoSidedMapper<Preferences, CartsConfig>() {

    override fun mapTo(a: Preferences): CartsConfig {
        val viewModeParams: CartsViewMode.Params = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.VIEW_MODE_PARAMS],
            defaultValue = CartsConfigDefaults.VIEW_MODE.params
        )
        val viewMode: CartsViewMode = CartsViewMode.classOfOrDefault(
            name = a[CartsConfigScheme.VIEW_MODE],
            params = viewModeParams,
            defaultValue = CartsConfigDefaults.VIEW_MODE
        )
        val sortParams: SortCarts.Params? = EnumExtensions.valueOfOrNull<SortCarts.Params>(
            name = a[CartsConfigScheme.SORT_PARAMS]
        )
        val sort: SortCarts = if (sortParams == null) {
            SortCarts.DoNotSort
        } else {
            SortCarts.classOfOrDefault(
                name = a[CartsConfigScheme.SORT],
                params = sortParams,
                defaultValue = CartsConfigDefaults.SORT
            )
        }
        val group: GroupCarts = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.GROUP],
            defaultValue = CartsConfigDefaults.GROUP
        )
        val calculateTotal: CalculateCartsTotal = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.CALCULATE_TOTAL],
            defaultValue = CartsConfigDefaults.CALCULATE_TOTAL
        )
        val afterAdding: AfterAddingCart = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.AFTER_ADDING],
            defaultValue = CartsConfigDefaults.AFTER_ADDING
        )
        val afterCompleting: AfterCompletingCart = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.AFTER_COMPLETING],
            defaultValue = CartsConfigDefaults.AFTER_COMPLETING
        )
        val afterArchiving: AfterArchivingCart = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.AFTER_ARCHIVING],
            defaultValue = CartsConfigDefaults.AFTER_ARCHIVING
        )
        val afterTappingByCheckbox: AfterTappingByCartCheckbox = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.AFTER_TAPPING_BY_CHECKBOX],
            defaultValue = CartsConfigDefaults.AFTER_TAPPING_BY_CHECKBOX
        )
        val checkboxesColor: CartsCheckboxesColor = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.CHECKBOXES_COLOR],
            defaultValue = CartsConfigDefaults.CHECKBOXES_COLOR
        )
        val swipeLeft: SwipeCart = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.SWIPE_LEFT],
            defaultValue = CartsConfigDefaults.SWIPE_LEFT
        )
        val swipeRight: SwipeCart = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.SWIPE_RIGHT],
            defaultValue = CartsConfigDefaults.SWIPE_RIGHT
        )
        val emptyCarts: EmptyCarts = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.EMPTY_CARTS],
            defaultValue = CartsConfigDefaults.EMPTY_CARTS
        )
        val deletionFromTrash: DeletionCartsFromTrash = EnumExtensions.valueOfOrDefault(
            name = a[CartsConfigScheme.DELETION_FROM_TRASH],
            defaultValue = CartsConfigDefaults.DELETION_FROM_TRASH
        )
        return CartsConfig(
            viewMode = viewMode,
            sort = sort,
            group = group,
            calculateTotal = calculateTotal,
            afterAdding = afterAdding,
            afterCompleting = afterCompleting,
            afterArchiving = afterArchiving,
            afterTappingByCheckbox = afterTappingByCheckbox,
            checkboxesColor = checkboxesColor,
            swipeLeft = swipeLeft,
            swipeRight = swipeRight,
            emptyCarts = emptyCarts,
            deletionFromTrash = deletionFromTrash
        )
    }

    override fun mapFrom(b: CartsConfig): Preferences {
        return preferencesOf(
            CartsConfigScheme.VIEW_MODE to b.viewMode.getName(),
            CartsConfigScheme.VIEW_MODE_PARAMS to b.viewMode.params.name,
            CartsConfigScheme.SORT to b.sort.getName(),
            CartsConfigScheme.SORT_PARAMS to b.sort.params?.name.orEmpty(),
            CartsConfigScheme.GROUP to b.afterAdding.name,
            CartsConfigScheme.CALCULATE_TOTAL to b.calculateTotal.name,
            CartsConfigScheme.AFTER_ADDING to b.afterAdding.name,
            CartsConfigScheme.AFTER_COMPLETING to b.afterCompleting.name,
            CartsConfigScheme.AFTER_ARCHIVING to b.afterArchiving.name,
            CartsConfigScheme.AFTER_TAPPING_BY_CHECKBOX to b.afterTappingByCheckbox.name,
            CartsConfigScheme.CHECKBOXES_COLOR to b.checkboxesColor.name,
            CartsConfigScheme.SWIPE_LEFT to b.swipeLeft.name,
            CartsConfigScheme.SWIPE_RIGHT to b.swipeRight.name,
            CartsConfigScheme.EMPTY_CARTS to b.emptyCarts.name,
            CartsConfigScheme.DELETION_FROM_TRASH to b.deletionFromTrash.name
        )
    }
}