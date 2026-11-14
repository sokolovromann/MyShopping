package ru.sokolovromann.myshopping.feature.carts.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.CartTotal
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsView
import ru.sokolovromann.myshopping.core.domain.model.FilterProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.Purchase
import ru.sokolovromann.myshopping.core.domain.model.PurchaseTotal
import ru.sokolovromann.myshopping.core.domain.model.Purchases
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.utils.MoneyUtils
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.carts.R
import ru.sokolovromann.myshopping.feature.carts.model.CartItem
import ru.sokolovromann.myshopping.feature.carts.model.ProductItem
import ru.sokolovromann.myshopping.feature.carts.model.PurchasesState

class PurchasesMapper @Inject constructor() {

    fun createPurchasesState(
        purchases: Purchases,
        cartsPreferences: CartsPreferences,
        generalPreferences: GeneralPreferences
    ) = PurchasesState(
        isLoading = false,
        status = purchases.status,
        pinnedItems = createItems(purchases.filteredPurchasesByPriority.high, generalPreferences),
        otherItems = createItems(purchases.filteredPurchasesByPriority.medium, generalPreferences),
        itemsCells = createCells(cartsPreferences.view),
        itemsSelected = emptySet(),
        total = createPurchasesTotal(purchases.calculatedTotal, generalPreferences)
    )

    fun toUids(items: List<CartItem>): Set<UID> = items.map { it.uid }.toSet()

    private fun createItems(
        purchases: Collection<Purchase>,
        generalPreferences: GeneralPreferences
    ) = purchases.map { purchase ->
        val total: UiText? = when (val purchaseTotal = purchase.total) {
            is PurchaseTotal.Calculated -> createPurchasesTotal(purchaseTotal.total, generalPreferences)
            is PurchaseTotal.Edited -> createPurchasesTotal(purchaseTotal.total, generalPreferences)
            null -> null
        }
        CartItem(
            uid = purchase.uid,
            status = purchase.status,
            name = purchase.name.ifEmpty { null },
            pinnedProducts = createProducts(purchase.filteredProductsByPriority.high),
            otherProducts = createProducts(purchase.filteredProductsByPriority.medium),
            reminder = purchase.reminder?.time?.getDisplay(),
            total = total
        )
    }

    private fun createProducts(products: Collection<Product>) =
        products.map { ProductItem(it.status, it.name) }

    private fun createPurchasesTotal(
        cartTotal: CartTotal?,
        generalPreferences: GeneralPreferences
    ): UiText? =
        when (cartTotal?.filterByStatus) {
            FilterProductsByStatus.All -> UiText.FromResourcesWithArgs(
                R.string.carts_text_total_all_args,
                MoneyUtils.format(
                    cartTotal.money,
                    generalPreferences.currency,
                    generalPreferences.moneyFormattingMode
                )
            )
            FilterProductsByStatus.Completed -> UiText.FromResourcesWithArgs(
                R.string.carts_text_total_completed_args,
                MoneyUtils.format(
                    cartTotal.money,
                    generalPreferences.currency,
                    generalPreferences.moneyFormattingMode
                )
            )
            FilterProductsByStatus.Active -> UiText.FromResourcesWithArgs(
                R.string.carts_text_total_active_args,
                MoneyUtils.format(
                    cartTotal.money,
                    generalPreferences.currency,
                    generalPreferences.moneyFormattingMode
                )
            )
            null -> null
        }

    private fun createCells(view: CartsView) = when (view) {
        is CartsView.Grid -> 2
        is CartsView.List -> 1
    }
}