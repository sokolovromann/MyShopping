package ru.sokolovromann.myshopping.data39.old

import android.content.Context
import kotlinx.coroutines.flow.first
import ru.sokolovromann.myshopping.data39.LocalEnvironment
import ru.sokolovromann.myshopping.data39.LocalResources
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class Api15Repository @Inject constructor(
    private val context: Context,
    private val api15ShoppingListsDao: Api15ShoppingListsDao,
    private val api15ProductsDao: Api15ProductsDao,
    private val api15AutocompletesDao: Api15AutocompletesDao,
    private val localResources: LocalResources
) {

    suspend fun getShoppings(): Collection<Api15ShoppingEntity> = withIoContext {
        return@withIoContext api15ShoppingListsDao.getAllShoppings().first()
    }

    suspend fun getProducts(): Collection<Api15ProductEntity> = withIoContext {
        return@withIoContext api15ProductsDao.getAllProducts().first()
    }

    suspend fun getAutocompletes(): Collection<Api15AutocompleteEntity> = withIoContext {
        return@withIoContext api15AutocompletesDao.getAllAutocompletes().first().toMutableList()
            .apply {
                val resAutocompletes = localResources
                    .getStrings(LocalEnvironment.DEFAULT_AUTOCOMPLETES_RES_ID)
                    .map { Api15AutocompleteEntity(name = it, personal = false) }
                addAll(resAutocompletes)
            }
    }
}