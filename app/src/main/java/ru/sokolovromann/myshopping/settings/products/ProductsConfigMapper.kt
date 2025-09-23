package ru.sokolovromann.myshopping.settings.products

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.io.TwoSidedMapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject
import kotlin.text.orEmpty

class ProductsConfigMapper @Inject constructor() : TwoSidedMapper<Preferences, ProductsConfig>() {

    override fun mapTo(a: Preferences): ProductsConfig {
        val viewMode: ProductsViewMode = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.VIEW_MODE],
            defaultValue = ProductsConfigDefaults.VIEW_MODE
        )
        val sortParams: SortProducts.Params? = EnumExtensions.valueOfOrNull<SortProducts.Params>(
            name = a[ProductsConfigScheme.SORT_PARAMS]
        )
        val sort: SortProducts = if (sortParams == null) {
            SortProducts.DoNotSort
        } else {
            SortProducts.classOfOrDefault(
                name = a[ProductsConfigScheme.SORT],
                params = sortParams,
                defaultValue = ProductsConfigDefaults.SORT
            )
        }
        val group: GroupProducts = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.GROUP],
            defaultValue = ProductsConfigDefaults.GROUP
        )
        val addingMode: AddingProductMode = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.ADDING_MODE],
            defaultValue = ProductsConfigDefaults.ADDING_MODE
        )

        val calculateTotalParams: CalculateProductsTotal.Params? = EnumExtensions.valueOfOrNull<CalculateProductsTotal.Params>(
            name = a[ProductsConfigScheme.CALCULATE_TOTAL_PARAMS]
        )
        val calculateTotal: CalculateProductsTotal = if (calculateTotalParams == null) {
            CalculateProductsTotal.DoNotCalculate
        } else {
            CalculateProductsTotal.classOfOrDefault(
                name = a[ProductsConfigScheme.CALCULATE_TOTAL],
                params = calculateTotalParams,
                defaultValue = ProductsConfigDefaults.CALCULATE_TOTAL
            )
        }
        val strikethroughCompleted: StrikethroughCompletedProducts = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.STRIKETHROUGH_COMPLETED],
            defaultValue = ProductsConfigDefaults.STRIKETHROUGH_COMPLETED
        )
        val afterCompleting: AfterCompletingProduct = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.AFTER_COMPETING],
            defaultValue = ProductsConfigDefaults.AFTER_COMPLETING
        )
        val afterTappingByCheckbox: AfterTappingByProductCheckbox = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.AFTER_TAPPING_BY_CHECKBOX],
            defaultValue = ProductsConfigDefaults.AFTER_TAPPING_BY_CHECKBOX
        )
        val checkboxesColor: ProductsCheckboxesColor = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.CHECKBOXES_COLOR],
            defaultValue = ProductsConfigDefaults.CHECKBOXES_COLOR
        )
        val afterTappingByItem: AfterTappingByProductItem = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.AFTER_TAPPING_BY_ITEM],
            defaultValue = ProductsConfigDefaults.AFTER_TAPPING_BY_ITEM
        )
        val swipeLeft: SwipeProduct = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.SWIPE_LEFT],
            defaultValue = ProductsConfigDefaults.SWIPE_LEFT
        )
        val swipeRight: SwipeProduct = EnumExtensions.valueOfOrDefault(
            name = a[ProductsConfigScheme.SWIPE_RIGHT],
            defaultValue = ProductsConfigDefaults.SWIPE_RIGHT
        )
        return ProductsConfig(
            viewMode = viewMode,
            sort = sort,
            group = group,
            addingMode = addingMode,
            calculateTotal = calculateTotal,
            strikethroughCompleted = strikethroughCompleted,
            afterCompleting = afterCompleting,
            afterTappingByCheckbox = afterTappingByCheckbox,
            checkboxesColor = checkboxesColor,
            afterTappingByItem = afterTappingByItem,
            swipeLeft = swipeLeft,
            swipeRight = swipeRight
        )
    }

    override fun mapFrom(b: ProductsConfig): Preferences {
        return preferencesOf(
            ProductsConfigScheme.VIEW_MODE to b.viewMode.name,
            ProductsConfigScheme.SORT to b.sort.getName(),
            ProductsConfigScheme.SORT_PARAMS to b.sort.params?.name.orEmpty(),
            ProductsConfigScheme.GROUP to b.group.name,
            ProductsConfigScheme.ADDING_MODE to b.addingMode.name,
            ProductsConfigScheme.CALCULATE_TOTAL to b.calculateTotal.getName(),
            ProductsConfigScheme.CALCULATE_TOTAL_PARAMS to b.calculateTotal.params?.name.orEmpty(),
            ProductsConfigScheme.STRIKETHROUGH_COMPLETED to b.strikethroughCompleted.name,
            ProductsConfigScheme.AFTER_COMPETING to b.afterCompleting.name,
            ProductsConfigScheme.AFTER_TAPPING_BY_CHECKBOX to b.afterTappingByCheckbox.name,
            ProductsConfigScheme.CHECKBOXES_COLOR to b.checkboxesColor.name,
            ProductsConfigScheme.AFTER_TAPPING_BY_ITEM to b.afterTappingByItem.name,
            ProductsConfigScheme.SWIPE_LEFT to b.swipeLeft.name,
            ProductsConfigScheme.SWIPE_RIGHT to b.swipeRight.name,
        )
    }
}