package ru.sokolovromann.myshopping.core.domain.utils

import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.CartTotal
import ru.sokolovromann.myshopping.core.domain.model.CartWithProducts
import ru.sokolovromann.myshopping.core.domain.model.FilterProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.ProductPriority
import ru.sokolovromann.myshopping.core.domain.model.ProductStatus
import ru.sokolovromann.myshopping.core.domain.model.SortProducts
import ru.sokolovromann.myshopping.core.domain.model.FilteredProductsByPriority
import ru.sokolovromann.myshopping.core.domain.model.Purchase
import ru.sokolovromann.myshopping.core.domain.model.PurchaseStatus
import ru.sokolovromann.myshopping.core.domain.model.PurchaseTotal
import java.math.BigDecimal

internal object PurchaseUtils {

    fun createPurchase(
        cartWithProducts: CartWithProducts,
        calculateProductsTotal: CalculateProductsTotal,
        sortProducts: SortProducts,
        groupProductsByStatus: GroupProductsByStatus
    ): Purchase {
        val cart = cartWithProducts.cart
        val products = cartWithProducts.products
        return Purchase(
            uid = cart.uid,
            directory = cart.directory,
            position = cart.position,
            created = cart.created,
            lastModified = cart.lastModified,
            status = products.getStatus(),
            priority = cart.priority,
            name = cart.name,
            reminder = cart.reminder,
            discount = cart.discount,
            total = getTotal(cartWithProducts, calculateProductsTotal),
            budget = cart.budget,
            note = cart.note,
            sortProducts = cart.sortProducts,
            groupProductsByStatus = cart.groupProductsByStatus,
            filteredProductsByPriority = products
                .sorted(cart.sortProducts ?: sortProducts)
                .groupedByStatus(cart.groupProductsByStatus ?: groupProductsByStatus)
                .filteredByPriority()
        )
    }

    fun getTotal(
        cartWithProducts: CartWithProducts,
        calculateTotal: CalculateProductsTotal
    ): PurchaseTotal? {
        val editedTotal = cartWithProducts.cart.total
        val products = cartWithProducts.products
        return if (calculateTotal != CalculateProductsTotal.DoNotCalculate) {
            if (editedTotal != null) {
                PurchaseTotal.Edited(editedTotal)
            } else {
                val calculatedTotal = when (calculateTotal) {
                    is CalculateProductsTotal.AllProducts -> {
                        val money = products.sumOf { it.cost ?: BigDecimal.ZERO }
                        CartTotal(money, FilterProductsByStatus.All)
                    }
                    is CalculateProductsTotal.CompletedProducts -> {
                        val money = products.filter { it.status == ProductStatus.Completed }
                            .sumOf { it.cost ?: BigDecimal.ZERO }
                        CartTotal(money, FilterProductsByStatus.Completed)
                    }
                    is CalculateProductsTotal.ActiveProducts -> {
                        val money = products.filter { it.status == ProductStatus.Active }
                            .sumOf { it.cost ?: BigDecimal.ZERO }
                        CartTotal(money, FilterProductsByStatus.Active)
                    }
                }
                PurchaseTotal.Calculated(calculatedTotal)
            }
        } else null
    }

    private fun Collection<Product>.getStatus(): PurchaseStatus {
        if (isEmpty()) return PurchaseStatus.Empty

        val active = find { it.status == ProductStatus.Active }
        return if (active == null) PurchaseStatus.Completed else PurchaseStatus.Active
    }

    private fun Collection<Product>.sorted(sort: SortProducts): Collection<Product> {
        return when (sort) {
            is SortProducts.ByCreated -> {
                if (sort.byAscending) {
                    sortedBy { it.created.value }
                } else {
                    sortedByDescending { it.created.value }
                }
            }
            is SortProducts.ByLastModified -> {
                if (sort.byAscending) {
                    sortedBy { it.lastModified.value }
                } else {
                    sortedByDescending { it.lastModified.value }
                }
            }
            is SortProducts.ByName -> {
                if (sort.byAscending) {
                    sortedBy { it.name.lowercase() }
                } else {
                    sortedByDescending { it.name.lowercase() }
                }
            }
            is SortProducts.ByCost -> {
                if (sort.byAscending) {
                    sortedBy { it.cost?.toFloat() ?: 0f }
                } else {
                    sortedByDescending { it.cost?.toFloat() ?: 0f }
                }
            }
            SortProducts.DoNotSort -> {
                sortedBy { it.position.value }
            }
        }
    }

    private fun Collection<Product>.groupedByStatus(group: GroupProductsByStatus): Collection<Product> {
        val partition = partition { it.status == ProductStatus.Completed }
        return when (group) {
            GroupProductsByStatus.CompletedFirst -> {
                partition.first.toMutableList().apply {
                    partition.second
                }
            }
            GroupProductsByStatus.ActiveFirst -> {
                partition.second.toMutableList().apply {
                    partition.first
                }
            }
            GroupProductsByStatus.HideCompleted -> {
                partition.second
            }
            GroupProductsByStatus.DoNotGroup -> {
                this
            }
        }
    }

    private fun Collection<Product>.filteredByPriority(): FilteredProductsByPriority {
        val partition = partition { it.priority == ProductPriority.High }
        return FilteredProductsByPriority(partition.first, partition.second)
    }
}