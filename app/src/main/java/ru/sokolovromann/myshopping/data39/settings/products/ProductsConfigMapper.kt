package ru.sokolovromann.myshopping.data39.settings.products

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject
import kotlin.text.orEmpty

class ProductsConfigMapper @Inject constructor() : Mapper<Preferences, ProductsConfig>() {

    override fun mapEntityTo(entity: Preferences): ProductsConfig {
        return ProductsConfig(
            viewMode = mapViewModeTo(entity),
            sort = mapSortTo(entity),
            group = mapGroupTo(entity),
            addingMode = mapAddingModeTo(entity),
            calculateTotal = mapCalculateTotalTo(entity),
            strikethroughCompleted = mapStrikethroughCompletedTo(entity),
            afterCompleting = mapAfterCompletingTo(entity),
            afterTappingByCheckbox = mapAfterTappingByCheckboxTo(entity),
            checkboxesColor = mapCheckboxesColorTo(entity),
            afterTappingByItem = mapAfterTappingByItemTo(entity),
            swipeLeft = mapSwipeLeftTo(entity),
            swipeRight = mapSwipeRightTo(entity)
        )
    }

    override fun mapEntityFrom(model: ProductsConfig): Preferences {
        return mutablePreferencesOf().apply {
            val viewMode = mapViewModeFrom(model.viewMode)
            plusAssign(viewMode)

            val sort = mapSortFrom(model.sort)
            plusAssign(sort)

            val group = mapGroupFrom(model.group)
            plusAssign(group)

            val addingMode = mapAddingModeFrom(model.addingMode)
            plusAssign(addingMode)

            val calculateTotal = mapCalculateTotalFrom(model.calculateTotal)
            plusAssign(calculateTotal)

            val strikethroughCompleted = mapStrikethroughCompletedFrom(model.strikethroughCompleted)
            plusAssign(strikethroughCompleted)

            val afterCompleting = mapAfterCompletingFrom(model.afterCompleting)
            plusAssign(afterCompleting)

            val afterTappingByCheckbox = mapAfterTappingByCheckboxFrom(model.afterTappingByCheckbox)
            plusAssign(afterTappingByCheckbox)

            val checkboxesColor = mapCheckboxesColorFrom(model.checkboxesColor)
            plusAssign(checkboxesColor)

            val afterTappingByItem = mapAfterTappingByItemFrom(model.afterTappingByItem)
            plusAssign(afterTappingByItem)

            val swipeLeft = mapSwipeLeftFrom(model.swipeLeft)
            plusAssign(swipeLeft)

            val swipeRight = mapSwipeRightFrom(model.swipeRight)
            plusAssign(swipeRight)
        }
    }

    fun mapViewModeTo(entity: Preferences): ProductsViewMode {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.VIEW_MODE],
            ProductsConfigDefaults.VIEW_MODE
        )
    }

    fun mapViewModeFrom(model: ProductsViewMode): Preferences {
        return preferencesOf(
            ProductsConfigScheme.VIEW_MODE to model.name
        )
    }

    fun mapSortTo(entity: Preferences): SortProducts {
        val default = ProductsConfigDefaults.SORT
        val name: SortProductsName = EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.SORT],
            default.name
        )
        val params: SortProductsParams? = EnumExtensions.valueOfOrNull<SortProductsParams>(
            entity[ProductsConfigScheme.SORT_PARAMS]
        )
        return SortProducts(name, params)
    }

    fun mapSortFrom(model: SortProducts): Preferences {
        return preferencesOf(
            ProductsConfigScheme.SORT to model.name.name,
            ProductsConfigScheme.SORT_PARAMS to model.params?.name.orEmpty()
        )
    }

    fun mapGroupTo(entity: Preferences): GroupProducts {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.GROUP],
            ProductsConfigDefaults.GROUP
        )
    }

    fun mapGroupFrom(model: GroupProducts): Preferences {
        return preferencesOf(
            ProductsConfigScheme.GROUP to model.name
        )
    }

    fun mapAddingModeTo(entity: Preferences): AddingProductMode {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.ADDING_MODE],
            ProductsConfigDefaults.ADDING_MODE
        )
    }

    fun mapAddingModeFrom(model: AddingProductMode): Preferences {
        return preferencesOf(
            ProductsConfigScheme.ADDING_MODE to model.name
        )
    }

    fun mapCalculateTotalTo(entity: Preferences): CalculateProductsTotal {
        val default = ProductsConfigDefaults.CALCULATE_TOTAL
        val name: CalculateProductsTotalName = EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.CALCULATE_TOTAL],
            default.name
        )
        val params: CalculateProductsTotalParams? = EnumExtensions.valueOfOrNull<CalculateProductsTotalParams>(
            entity[ProductsConfigScheme.CALCULATE_TOTAL_PARAMS]
        )
        return CalculateProductsTotal(name, params)
    }

    fun mapCalculateTotalFrom(model: CalculateProductsTotal): Preferences {
        return preferencesOf(
            ProductsConfigScheme.CALCULATE_TOTAL to model.name.name,
            ProductsConfigScheme.CALCULATE_TOTAL_PARAMS to model.params?.name.orEmpty()
        )
    }

    fun mapStrikethroughCompletedTo(entity: Preferences): StrikethroughCompletedProducts {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.STRIKETHROUGH_COMPLETED],
            ProductsConfigDefaults.STRIKETHROUGH_COMPLETED
        )
    }

    fun mapStrikethroughCompletedFrom(model: StrikethroughCompletedProducts): Preferences {
        return preferencesOf(
            ProductsConfigScheme.STRIKETHROUGH_COMPLETED to model.name
        )
    }

    fun mapAfterCompletingTo(entity: Preferences): AfterCompletingProduct {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.AFTER_COMPETING],
            ProductsConfigDefaults.AFTER_COMPLETING
        )
    }

    fun mapAfterCompletingFrom(model: AfterCompletingProduct): Preferences {
        return preferencesOf(
            ProductsConfigScheme.AFTER_COMPETING to model.name
        )
    }

    fun mapAfterTappingByCheckboxTo(entity: Preferences): AfterTappingByProductCheckbox {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.AFTER_TAPPING_BY_CHECKBOX],
            ProductsConfigDefaults.AFTER_TAPPING_BY_CHECKBOX
        )
    }

    fun mapAfterTappingByCheckboxFrom(model: AfterTappingByProductCheckbox): Preferences {
        return preferencesOf(
            ProductsConfigScheme.AFTER_TAPPING_BY_CHECKBOX to model.name
        )
    }

    fun mapCheckboxesColorTo(entity: Preferences): ProductsCheckboxesColor {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.CHECKBOXES_COLOR],
            ProductsConfigDefaults.CHECKBOXES_COLOR
        )
    }

    fun mapCheckboxesColorFrom(model: ProductsCheckboxesColor): Preferences {
        return preferencesOf(
            ProductsConfigScheme.CHECKBOXES_COLOR to model.name
        )
    }

    fun mapAfterTappingByItemTo(entity: Preferences): AfterTappingByProductItem {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.AFTER_TAPPING_BY_ITEM],
            ProductsConfigDefaults.AFTER_TAPPING_BY_ITEM
        )
    }

    fun mapAfterTappingByItemFrom(model: AfterTappingByProductItem): Preferences {
        return preferencesOf(
            ProductsConfigScheme.AFTER_TAPPING_BY_ITEM to model.name,
        )
    }

    fun mapSwipeLeftTo(entity: Preferences): SwipeProduct {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.SWIPE_LEFT],
            ProductsConfigDefaults.SWIPE_LEFT
        )
    }

    fun mapSwipeLeftFrom(model: SwipeProduct): Preferences {
        return preferencesOf(
            ProductsConfigScheme.SWIPE_LEFT to model.name
        )
    }

    fun mapSwipeRightTo(entity: Preferences): SwipeProduct {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsConfigScheme.SWIPE_RIGHT],
            ProductsConfigDefaults.SWIPE_RIGHT
        )
    }

    fun mapSwipeRightFrom(model: SwipeProduct): Preferences {
        return preferencesOf(
            ProductsConfigScheme.SWIPE_RIGHT to model.name
        )
    }
}