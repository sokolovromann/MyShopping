package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.CalculateProductsTotal
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartPriority
import ru.sokolovromann.myshopping.core.domain.model.CartTotal
import ru.sokolovromann.myshopping.core.domain.model.CartWithProducts
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.FilterProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.GroupCartsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.SortCarts
import ru.sokolovromann.myshopping.core.domain.model.FilteredPurchasesByPriority
import ru.sokolovromann.myshopping.core.domain.model.Purchase
import ru.sokolovromann.myshopping.core.domain.model.PurchaseStatus
import ru.sokolovromann.myshopping.core.domain.model.Purchases
import ru.sokolovromann.myshopping.core.domain.model.PurchasesStatus
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository
import ru.sokolovromann.myshopping.core.domain.utils.PurchaseUtils
import java.math.BigDecimal

class ObservePurchasesUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    private val observeCartsPreferencesUseCase: ObserveCartsPreferencesUseCase,
    private val observeProductsPreferencesUseCase: ObserveProductsPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(directory: CartDirectory): Flow<Purchases> = combine(
        flow = cartsRepository.observeCartsWithProducts(directory),
        flow2 = observeCartsPreferencesUseCase(),
        flow3 = observeProductsPreferencesUseCase(),
        transform = { cartsWithProducts, cartsPreferences, productsPreferences ->
            Purchases(
                filteredPurchasesByPriority = cartsWithProducts
                    .mapped(cartsPreferences, productsPreferences)
                    .sorted(cartsPreferences.sort)
                    .grouped(cartsPreferences.groupByStatus)
                    .filteredByPriority(),
                status = cartsWithProducts.getStatus(),
                calculatedTotal = cartsWithProducts.getTotal(cartsPreferences.calculateProductsTotal)
            )
        }
    ).flowOn(ioDispatcher)

    private fun Collection<CartWithProducts>.getStatus(): PurchasesStatus =
        if (isEmpty()) PurchasesStatus.NotFound else PurchasesStatus.Found

    private fun Collection<CartWithProducts>.getTotal(calculateTotal: CalculateProductsTotal): CartTotal? {
        return if (calculateTotal != CalculateProductsTotal.DoNotCalculate) {
            val money: BigDecimal = BigDecimal.ZERO
            forEach { cartWithProducts ->
                PurchaseUtils.getTotal(cartWithProducts, calculateTotal)?.let {
                    money.plus(it.getMoney())
                }
            }
            val filterProductsByStatus = when (calculateTotal) {
                is CalculateProductsTotal.AllProducts -> FilterProductsByStatus.All
                is CalculateProductsTotal.CompletedProducts -> FilterProductsByStatus.Completed
                is CalculateProductsTotal.ActiveProducts -> FilterProductsByStatus.Active
            }
            CartTotal(money, filterProductsByStatus)
        } else null
    }

    private fun Collection<CartWithProducts>.mapped(
        cartsPreferences: CartsPreferences,
        productsPreferences: ProductsPreferences
    ) = map { cartWithProducts ->
        PurchaseUtils.createPurchase(
            cartWithProducts,
            cartsPreferences.calculateProductsTotal,
            productsPreferences.sort,
            productsPreferences.groupByStatus
        )
    }

    private fun Collection<Purchase>.sorted(sort: SortCarts): Collection<Purchase> {
        return when (sort) {
            is SortCarts.ByCreated -> {
                if (sort.byAscending) {
                    sortedBy { it.created.value }
                } else {
                    sortedByDescending { it.created.value }
                }
            }
            is SortCarts.ByLastModified -> {
                if (sort.byAscending) {
                    sortedBy { it.lastModified.value }
                } else {
                    sortedByDescending { it.lastModified.value }
                }
            }
            is SortCarts.ByName -> {
                if (sort.byAscending) {
                    sortedBy { it.name.lowercase() }
                } else {
                    sortedByDescending { it.name.lowercase() }
                }
            }
            is SortCarts.ByReminder -> {
                if (sort.byAscending) {
                    sortedBy { it.reminder?.time?.value ?: 0L }
                } else {
                    sortedByDescending { it.reminder?.time?.value ?: 0L }
                }
            }
            is SortCarts.ByTotal -> {
                if (sort.byAscending) {
                    sortedBy { it.total?.getMoney()?.toFloat() ?: 0f }
                } else {
                    sortedByDescending { it.total?.getMoney()?.toFloat() ?: 0f }
                }
            }
            SortCarts.DoNotSort -> {
                sortedBy { it.position.value }
            }
        }
    }

    private fun Collection<Purchase>.grouped(group: GroupCartsByStatus): Collection<Purchase> {
        val partition = partition { it.status == PurchaseStatus.Completed }
        return when (group) {
            is GroupCartsByStatus.CompletedFirst -> {
                partition.first.toMutableList().apply {
                    if (group.displayEmpty) partition.second else {
                        partition.second.filterNot { it.status == PurchaseStatus.Empty }
                    }
                }
            }
            is GroupCartsByStatus.ActiveFirst -> {
                partition.second.toMutableList().apply {
                    if (group.displayEmpty) partition.first else {
                        partition.first.filterNot { it.status == PurchaseStatus.Empty }
                    }
                }
            }
            is GroupCartsByStatus.HideCompleted -> {
                if (group.displayEmpty) partition.second else {
                    partition.second.filterNot { it.status == PurchaseStatus.Empty }
                }
            }
            is GroupCartsByStatus.DoNotGroup -> {
                if (group.displayEmpty) this else filterNot { it.status == PurchaseStatus.Empty }
            }
        }
    }

    private fun Collection<Purchase>.filteredByPriority(): FilteredPurchasesByPriority {
        val partition = partition { it.priority == CartPriority.High }
        return FilteredPurchasesByPriority(partition.first, partition.second)
    }
}