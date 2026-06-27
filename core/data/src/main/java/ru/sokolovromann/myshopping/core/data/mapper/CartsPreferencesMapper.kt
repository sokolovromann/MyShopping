package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.CartsPreferencesScheme
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
            preferences[CartsPreferencesScheme.VIEW_KEY],
            preferences[CartsPreferencesScheme.PRODUCTS_DISPLAY_MODE_KEY]
        ),
        toSort(
            preferences[CartsPreferencesScheme.SORT_KEY],
            preferences[CartsPreferencesScheme.SORT_BY_ASCENDING_KEY]
        ),
        toGroupCartsByStatus(
            preferences[CartsPreferencesScheme.GROUP_BY_STATUS_KEY],
            preferences[CartsPreferencesScheme.DISPLAY_EMPTY_KEY]
        ),
        toCalculateProductsTotal(
            preferences[CartsPreferencesScheme.CALCULATE_PRODUCTS_TOTAL_KEY],
            preferences[CartsPreferencesScheme.PRODUCTS_TOTAL_CALCULATING_MODE_KEY]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[CartsPreferencesScheme.AFTER_ADDING_KEY],
            AfterAddingCart.OpenProductsScreen
        ),
        EnumUtils.valueOfOrDefault(
            preferences[CartsPreferencesScheme.AFTER_COMPLETING_KEY],
            AfterCompletingCart.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[CartsPreferencesScheme.AFTER_ARCHIVING_KEY],
            AfterArchivingCart.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[CartsPreferencesScheme.AFTER_TAPPING_BY_CHECKBOX_KEY],
            AfterTappingByCartCheckbox.DoNothing
        ),
        EnumUtils.valueOfOrDefault(
            preferences[CartsPreferencesScheme.CHECKBOX_COLOR_KEY],
            CheckboxColor.RedOrGreen
        ),
        SwipeCart.Left(
            EnumUtils.valueOfOrDefault(
                preferences[CartsPreferencesScheme.SWIPE_LEFT_KEY],
                SwipeCartActionName.Off
            )
        ),
        SwipeCart.Right(
            EnumUtils.valueOfOrDefault(
                preferences[CartsPreferencesScheme.SWIPE_RIGHT_KEY],
                SwipeCartActionName.Off
            )
        ),
        EnumUtils.valueOfOrDefault(
            preferences[CartsPreferencesScheme.DELETION_FROM_TRASH_KEY],
            DeletionCartFromTrash.DoNotDelete
        )
    )

    override fun toPreferences(model: CartsPreferences) = preferencesOf(
        CartsPreferencesScheme.VIEW_KEY
                to model.view.javaClass.simpleName,
        CartsPreferencesScheme.PRODUCTS_DISPLAY_MODE_KEY
                to model.view.getProductsDisplayMode().toString(),
        CartsPreferencesScheme.SORT_KEY
                to model.sort.javaClass.simpleName,
        CartsPreferencesScheme.SORT_BY_ASCENDING_KEY
                to model.sort.isByAscending().toString(),
        CartsPreferencesScheme.GROUP_BY_STATUS_KEY
                to model.groupByStatus.javaClass.simpleName,
        CartsPreferencesScheme.DISPLAY_EMPTY_KEY
                to model.groupByStatus.isDisplayEmpty().toString(),
        CartsPreferencesScheme.CALCULATE_PRODUCTS_TOTAL_KEY
                to model.calculateProductsTotal.javaClass.simpleName,
        CartsPreferencesScheme.PRODUCTS_TOTAL_CALCULATING_MODE_KEY
                to model.calculateProductsTotal.getCalculatingMode().toString(),
        CartsPreferencesScheme.AFTER_ADDING_KEY
                to model.afterAdding.toString(),
        CartsPreferencesScheme.AFTER_COMPLETING_KEY
                to model.afterCompleting.toString(),
        CartsPreferencesScheme.AFTER_ARCHIVING_KEY
                to model.afterArchiving.toString(),
        CartsPreferencesScheme.AFTER_TAPPING_BY_CHECKBOX_KEY
                to model.afterTappingByCheckbox.toString(),
        CartsPreferencesScheme.CHECKBOX_COLOR_KEY
                to model.checkboxColor.toString(),
        CartsPreferencesScheme.SWIPE_LEFT_KEY
                to model.swipeLeft.actionName.toString(),
        CartsPreferencesScheme.SWIPE_RIGHT_KEY
                to model.swipeRight.actionName.toString(),
        CartsPreferencesScheme.DELETION_FROM_TRASH_KEY
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