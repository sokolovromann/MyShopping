package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.ProductsPreferencesScheme
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsTotalCalculatingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsView
import ru.sokolovromann.myshopping.core.domain.model.SortProducts
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct
import ru.sokolovromann.myshopping.core.domain.model.SwipeProductActionName
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class ProductsPreferencesMapper @Inject constructor() : DataStoreMapper<ProductsPreferences>() {

    override fun toModel(preferences: Preferences) = ProductsPreferences(
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.VIEW_KEY],
            ProductsView.List
        ),
        toSort(
            preferences[ProductsPreferencesScheme.SORT_KEY],
            preferences[ProductsPreferencesScheme.SORT_BY_ASCENDING_KEY]
        ),
        toGroupByStatus(preferences[ProductsPreferencesScheme.GROUP_BY_STATUS_KEY]),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.ADDING_MODE_KEY],
            ProductsAddingMode.Simple
        ),
        toCalculateProductsTotal(
            preferences[ProductsPreferencesScheme.CALCULATE_PRODUCTS_TOTAL_KEY],
            preferences[ProductsPreferencesScheme.PRODUCTS_TOTAL_CALCULATING_MODE_KEY]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.STRIKETHROUGH_COMPLETED_KEY],
            StrikethroughCompletedProducts.Off
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.AFTER_COMPLETING_KEY],
            AfterCompletingProduct.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.AFTER_TAPPING_BY_CHECKBOX_KEY],
            AfterTappingByProductCheckbox.ChangeProductStatus
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.CHECKBOX_COLOR_KEY],
            CheckboxColor.RedOrGreen
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsPreferencesScheme.AFTER_TAPPING_BY_ITEM_KEY],
            AfterTappingByProductItem.DoNothing
        ),
        SwipeProduct.Left(
            EnumUtils.valueOfOrDefault(
                preferences[ProductsPreferencesScheme.SWIPE_LEFT_KEY],
                SwipeProductActionName.Off
            )
        ),
        SwipeProduct.Right(
            EnumUtils.valueOfOrDefault(
                preferences[ProductsPreferencesScheme.SWIPE_RIGHT_KEY],
                SwipeProductActionName.Off
            )
        ),
    )

    override fun toPreferences(model: ProductsPreferences) = preferencesOf(
        ProductsPreferencesScheme.VIEW_KEY
                to model.view.javaClass.simpleName,
        ProductsPreferencesScheme.SORT_KEY
                to model.sort.javaClass.simpleName,
        ProductsPreferencesScheme.SORT_BY_ASCENDING_KEY
                to model.sort.isByAscending().toString(),
        ProductsPreferencesScheme.GROUP_BY_STATUS_KEY
                to model.groupByStatus.javaClass.simpleName,
        ProductsPreferencesScheme.ADDING_MODE_KEY
                to model.addingMode.toString(),
        ProductsPreferencesScheme.CALCULATE_PRODUCTS_TOTAL_KEY
                to model.calculateTotal.javaClass.simpleName,
        ProductsPreferencesScheme.PRODUCTS_TOTAL_CALCULATING_MODE_KEY
                to model.calculateTotal.getCalculatingMode().toString(),
        ProductsPreferencesScheme.STRIKETHROUGH_COMPLETED_KEY
                to model.strikethroughCompleted.toString(),
        ProductsPreferencesScheme.AFTER_COMPLETING_KEY
                to model.afterCompleting.toString(),
        ProductsPreferencesScheme.AFTER_TAPPING_BY_CHECKBOX_KEY
                to model.afterTappingByCheckbox.toString(),
        ProductsPreferencesScheme.CHECKBOX_COLOR_KEY
                to model.checkboxColor.toString(),
        ProductsPreferencesScheme.AFTER_TAPPING_BY_ITEM_KEY
                to model.afterTappingByItem.toString(),
        ProductsPreferencesScheme.SWIPE_LEFT_KEY
                to model.swipeLeft.actionName.toString(),
        ProductsPreferencesScheme.SWIPE_RIGHT_KEY
                to model.swipeRight.actionName.toString(),
    )

    fun toSort(sort: String?, sortByAscending: String?): SortProducts {
        val byAscending = sortByAscending.toBoolean()
        return when (sort) {
            "ByCreated" -> SortProducts.ByCreated(byAscending)
            "ByLastModified" -> SortProducts.ByLastModified(byAscending)
            "ByName" -> SortProducts.ByName(byAscending)
            "ByCost" -> SortProducts.ByCost(byAscending)
            "DoNotSort" -> SortProducts.DoNotSort
            else -> SortProducts.DoNotSort
        }
    }

    fun toGroupByStatus(group: String?) =
        EnumUtils.valueOfOrDefault(group, GroupProductsByStatus.ActiveFirst)

    private fun toCalculateProductsTotal(
        calculateTotal: String?,
        calculatingMode: String?
    ): CalculateProductsTotal {
        val productsTotalCalculatingMode = EnumUtils.valueOfOrDefault(
            calculatingMode,
            ProductsTotalCalculatingMode.Short
        )
        return when (calculateTotal) {
            "AllProducts" -> CalculateProductsTotal.AllProducts(productsTotalCalculatingMode)
            "CompletedProducts" -> CalculateProductsTotal.CompletedProducts(productsTotalCalculatingMode)
            "ActiveProducts" -> CalculateProductsTotal.ActiveProducts(productsTotalCalculatingMode)
            "DoNotCalculate" -> CalculateProductsTotal.DoNotCalculate
            else -> CalculateProductsTotal.AllProducts(productsTotalCalculatingMode)
        }
    }
}