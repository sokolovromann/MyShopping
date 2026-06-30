package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsProductsDisplayMode
import ru.sokolovromann.myshopping.core.domain.model.CartsView
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.GroupCartsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsTotalCalculatingMode
import ru.sokolovromann.myshopping.core.domain.model.SortCarts
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart
import ru.sokolovromann.myshopping.core.domain.model.SwipeCartActionName
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class CartsPreferencesMapper @Inject constructor() : DataStoreMapper<CartsPreferences>() {

    override fun toModel(preferences: Preferences) = CartsPreferences(
        toView(
            preferences[LocalDataStoreScheme.Carts.VIEW],
            preferences[LocalDataStoreScheme.Carts.PRODUCTS_DISPLAY_MODE]
        ),
        toSort(
            preferences[LocalDataStoreScheme.Carts.SORT],
            preferences[LocalDataStoreScheme.Carts.SORT_BY_ASCENDING]
        ),
        toGroupCartsByStatus(
            preferences[LocalDataStoreScheme.Carts.GROUP_BY_STATUS],
            preferences[LocalDataStoreScheme.Carts.DISPLAY_EMPTY]
        ),
        toCalculateProductsTotal(
            preferences[LocalDataStoreScheme.Carts.CALCULATE_PRODUCTS_TOTAL],
            preferences[LocalDataStoreScheme.Carts.PRODUCTS_TOTAL_CALCULATING_MODE]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Carts.AFTER_ADDING],
            AfterAddingCart.OpenProductsScreen
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Carts.AFTER_COMPLETING],
            AfterCompletingCart.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Carts.AFTER_ARCHIVING],
            AfterArchivingCart.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Carts.AFTER_TAPPING_BY_CHECKBOX],
            AfterTappingByCartCheckbox.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Carts.CHECKBOX_COLOR],
            CheckboxColor.RedOrGreen
        ),
        SwipeCart.Left(
            EnumUtils.valueOfOrDefault(
                preferences[LocalDataStoreScheme.Carts.SWIPE_LEFT],
                SwipeCartActionName.Off
            )
        ),
        SwipeCart.Right(
            EnumUtils.valueOfOrDefault(
                preferences[LocalDataStoreScheme.Carts.SWIPE_RIGHT],
                SwipeCartActionName.Off
            )
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Carts.DELETION_FROM_TRASH],
            DeletionCartFromTrash.DoNotDelete
        )
    )

    override fun toPreferences(model: CartsPreferences) = preferencesOf(
        LocalDataStoreScheme.Carts.VIEW
                to model.view.javaClass.simpleName,
        LocalDataStoreScheme.Carts.PRODUCTS_DISPLAY_MODE
                to model.view.getProductsDisplayMode().toString(),
        LocalDataStoreScheme.Carts.SORT
                to model.sort.javaClass.simpleName,
        LocalDataStoreScheme.Carts.SORT_BY_ASCENDING
                to model.sort.isByAscending().toString(),
        LocalDataStoreScheme.Carts.GROUP_BY_STATUS
                to model.groupByStatus.javaClass.simpleName,
        LocalDataStoreScheme.Carts.DISPLAY_EMPTY
                to model.groupByStatus.isDisplayEmpty().toString(),
        LocalDataStoreScheme.Carts.CALCULATE_PRODUCTS_TOTAL
                to model.calculateProductsTotal.javaClass.simpleName,
        LocalDataStoreScheme.Carts.PRODUCTS_TOTAL_CALCULATING_MODE
                to model.calculateProductsTotal.getCalculatingMode().toString(),
        LocalDataStoreScheme.Carts.AFTER_ADDING
                to model.afterAdding.toString(),
        LocalDataStoreScheme.Carts.AFTER_COMPLETING
                to model.afterCompleting.toString(),
        LocalDataStoreScheme.Carts.AFTER_ARCHIVING
                to model.afterArchiving.toString(),
        LocalDataStoreScheme.Carts.AFTER_TAPPING_BY_CHECKBOX
                to model.afterTappingByCheckbox.toString(),
        LocalDataStoreScheme.Carts.CHECKBOX_COLOR
                to model.checkboxColor.toString(),
        LocalDataStoreScheme.Carts.SWIPE_LEFT
                to model.swipeLeft.actionName.toString(),
        LocalDataStoreScheme.Carts.SWIPE_RIGHT
                to model.swipeRight.actionName.toString(),
        LocalDataStoreScheme.Carts.DELETION_FROM_TRASH
                to model.deletionFromTrash.toString()
    )

    private fun toView(view: String?, displayMode: String?): CartsView {
        val productsDisplayMode = EnumUtils.valueOfOrDefault(
            displayMode,
            CartsProductsDisplayMode.ProductsHorizontally
        )
        return when (view) {
            "List" -> CartsView.List(productsDisplayMode)
            "Grid" -> CartsView.Grid(productsDisplayMode)
            else -> CartsView.List(productsDisplayMode)
        }
    }

    private fun toSort(sort: String?, sortByAscending: String?): SortCarts {
        val byAscending = sortByAscending.toBoolean()
        return when (sort) {
            "ByCreated" -> SortCarts.ByCreated(byAscending)
            "ByLastModified" -> SortCarts.ByLastModified(byAscending)
            "ByName" -> SortCarts.ByName(byAscending)
            "ByTotal" -> SortCarts.ByTotal(byAscending)
            "ByReminder" -> SortCarts.ByReminder(byAscending)
            "DoNotSort" -> SortCarts.DoNotSort
            else -> SortCarts.DoNotSort
        }
    }

    private fun toGroupCartsByStatus(group: String?, displayEmpty: String?): GroupCartsByStatus {
        val display = displayEmpty.toBoolean()
        return when (group) {
            "CompletedFirst" -> GroupCartsByStatus.CompletedFirst(display)
            "ActiveFirst" -> GroupCartsByStatus.ActiveFirst(display)
            "HideCompleted" -> GroupCartsByStatus.HideCompleted(display)
            "DoNotGroup" -> GroupCartsByStatus.DoNotGroup(display)
            else -> GroupCartsByStatus.ActiveFirst(display)
        }
    }

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