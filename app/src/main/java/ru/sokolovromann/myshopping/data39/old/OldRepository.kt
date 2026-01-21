package ru.sokolovromann.myshopping.data39.old

import kotlinx.coroutines.flow.first
import ru.sokolovromann.myshopping.data39.LocalEnvironment
import ru.sokolovromann.myshopping.data39.LocalResources
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class OldRepository @Inject constructor(
    private val oldShoppingListsDao: OldShoppingListsDao,
    private val oldProductsDao: OldProductsDao,
    private val oldAutocompletesDao: OldAutocompletesDao,
    private val localResources: LocalResources
) {

    suspend fun getShoppings(): Collection<OldShoppingEntity> = withIoContext {
        return@withIoContext oldShoppingListsDao.getAllShoppings().first()
    }

    suspend fun getProducts(): Collection<OldProductEntity> = withIoContext {
        return@withIoContext oldProductsDao.getAllProducts().first()
    }

    suspend fun getAutocompletes(): Collection<OldAutocompleteEntity> = withIoContext {
        return@withIoContext oldAutocompletesDao.getAllAutocompletes().first().toMutableList()
            .apply {
                val resAutocompletes = localResources
                    .getStrings(LocalEnvironment.DEFAULT_AUTOCOMPLETES_RES_ID)
                    .map { OldAutocompleteEntity(name = it, personal = false) }
                addAll(resAutocompletes)
            }
    }
}