package ru.sokolovromann.myshopping.data39.old

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data39.LocalEnvironment
import ru.sokolovromann.myshopping.data39.LocalResources
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class Api15Repository @Inject constructor(
    private val api15ShoppingListsDao: Api15ShoppingListsDao,
    private val api15ProductsDao: Api15ProductsDao,
    private val api15AutocompletesDao: Api15AutocompletesDao,
    private val localResources: LocalResources,
    private val appContent: AppContent
) {

    suspend fun getShoppings(): Collection<Api15ShoppingEntity> = withIoContext {
        return@withIoContext api15ShoppingListsDao.getAllShoppings().first()
    }

    suspend fun getProducts(): Collection<Api15ProductEntity> = withIoContext {
        return@withIoContext api15ProductsDao.getAllProducts().first()
    }

    suspend fun getAutocompletes(displayDefault: Boolean?): Collection<Api15AutocompleteEntity> = withIoContext {
        val autocompletes = api15AutocompletesDao.getAllAutocompletes().first().toMutableList()
        return@withIoContext if (displayDefault == true) {
            autocompletes.apply {
                val resAutocompletes = localResources
                    .getStrings(LocalEnvironment.DEFAULT_AUTOCOMPLETES_RES_ID)
                    .map { Api15AutocompleteEntity(name = it, personal = false) }
                addAll(resAutocompletes)
            }
        } else { autocompletes }
    }

    suspend fun getAutocompletesConfig(): Api15AutocompletesConfigEntity = withIoContext {
        val preferences = appContent.getPreferences().data.first()
        return@withIoContext Api15AutocompletesConfigEntity(
            preferences[booleanPreferencesKey("display_default_autocompletes")],
            preferences[intPreferencesKey("max_autocomplete_names")],
            preferences[intPreferencesKey("max_autocomplete_quantities")],
            preferences[intPreferencesKey("max_autocomplete_moneys")],
            preferences[intPreferencesKey("max_autocomplete_others")],
            preferences[booleanPreferencesKey("save_product_to_autocompletes")]
        )
    }
}