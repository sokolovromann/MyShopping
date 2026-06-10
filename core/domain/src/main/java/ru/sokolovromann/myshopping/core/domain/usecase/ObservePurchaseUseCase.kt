package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.model.Purchase
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository
import ru.sokolovromann.myshopping.core.domain.utils.PurchaseUtils

class ObservePurchaseUseCase @Inject constructor(
    private val cartsRepository: CartsRepository,
    private val observeProductsPreferencesUseCase: ObserveProductsPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(uid: UID): Flow<Purchase?> = combine(
        flow = cartsRepository.observeCartWithProducts(uid),
        flow2 = observeProductsPreferencesUseCase(),
        transform = { cartWithProducts, productsPreferences ->
            cartWithProducts?.let {
                PurchaseUtils.createPurchase(
                    it,
                    productsPreferences.calculateTotal,
                    productsPreferences.sort,
                    productsPreferences.groupByStatus
                )
            }
        }
    ).flowOn(ioDispatcher)
}