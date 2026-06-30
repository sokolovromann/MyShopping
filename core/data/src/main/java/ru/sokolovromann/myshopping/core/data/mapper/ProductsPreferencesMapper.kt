package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
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
            preferences[LocalDataStoreScheme.Products.VIEW],
            ProductsView.List
        ),
        toSort(
            preferences[LocalDataStoreScheme.Products.SORT],
            preferences[LocalDataStoreScheme.Products.SORT_BY_ASCENDING]
        ),
        toGroupByStatus(preferences[LocalDataStoreScheme.Products.GROUP_BY_STATUS]),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Products.ADDING_MODE],
            ProductsAddingMode.Simple
        ),
        toCalculateProductsTotal(
            preferences[LocalDataStoreScheme.Products.CALCULATE_PRODUCTS_TOTAL],
            preferences[LocalDataStoreScheme.Products.PRODUCTS_TOTAL_CALCULATING_MODE]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Products.STRIKETHROUGH_COMPLETED],
            StrikethroughCompletedProducts.Off
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Products.AFTER_COMPLETING],
            AfterCompletingProduct.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Products.AFTER_TAPPING_BY_CHECKBOX],
            AfterTappingByProductCheckbox.ChangeProductStatus
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Products.CHECKBOX_COLOR],
            CheckboxColor.RedOrGreen
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Products.AFTER_TAPPING_BY_ITEM],
            AfterTappingByProductItem.DoNothing
        ),
        SwipeProduct.Left(
            EnumUtils.valueOfOrDefault(
                preferences[LocalDataStoreScheme.Products.SWIPE_LEFT],
                SwipeProductActionName.Off
            )
        ),
        SwipeProduct.Right(
            EnumUtils.valueOfOrDefault(
                preferences[LocalDataStoreScheme.Products.SWIPE_RIGHT],
                SwipeProductActionName.Off
            )
        ),
    )

    override fun toPreferences(model: ProductsPreferences) = preferencesOf(
        LocalDataStoreScheme.Products.VIEW
                to model.view.javaClass.simpleName,
        LocalDataStoreScheme.Products.SORT
                to model.sort.javaClass.simpleName,
        LocalDataStoreScheme.Products.SORT_BY_ASCENDING
                to model.sort.isByAscending().toString(),
        LocalDataStoreScheme.Products.GROUP_BY_STATUS
                to model.groupByStatus.javaClass.simpleName,
        LocalDataStoreScheme.Products.ADDING_MODE
                to model.addingMode.toString(),
        LocalDataStoreScheme.Products.CALCULATE_PRODUCTS_TOTAL
                to model.calculateTotal.javaClass.simpleName,
        LocalDataStoreScheme.Products.PRODUCTS_TOTAL_CALCULATING_MODE
                to model.calculateTotal.getCalculatingMode().toString(),
        LocalDataStoreScheme.Products.STRIKETHROUGH_COMPLETED
                to model.strikethroughCompleted.toString(),
        LocalDataStoreScheme.Products.AFTER_COMPLETING
                to model.afterCompleting.toString(),
        LocalDataStoreScheme.Products.AFTER_TAPPING_BY_CHECKBOX
                to model.afterTappingByCheckbox.toString(),
        LocalDataStoreScheme.Products.CHECKBOX_COLOR
                to model.checkboxColor.toString(),
        LocalDataStoreScheme.Products.AFTER_TAPPING_BY_ITEM
                to model.afterTappingByItem.toString(),
        LocalDataStoreScheme.Products.SWIPE_LEFT
                to model.swipeLeft.actionName.toString(),
        LocalDataStoreScheme.Products.SWIPE_RIGHT
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