package ru.sokolovromann.myshopping.manager

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.Api15Repository
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class Api15Manager @Inject constructor(private val api15Repository: Api15Repository) {

    suspend fun getAutocompletes(): Map<String, List<Api15AutocompleteEntity>> = withIoContext {
        return@withIoContext api15Repository.getAutocompletes()
            .groupBy { it.name.lowercase() }
            .mapKeys { (key, _) -> key.replaceFirstChar { it.uppercase() } }
            .mapValues { (_, value) ->
                val sorted = value.sortedBy { it.lastModified }
                val details = mutableListOf<Api15AutocompleteEntity>()

                val manufacturer = sorted
                    .distinctBy { it.manufacturer.lowercase() }
                    .filter { it.manufacturer.isNotEmpty() }
                details.addAll(manufacturer)

                val brands = sorted
                    .distinctBy { it.brand.lowercase() }
                    .filter { it.brand.isNotEmpty() }
                details.addAll(brands)

                val sizes = sorted
                    .distinctBy { it.size.lowercase() }
                    .filter { it.size.isNotEmpty() }
                details.addAll(sizes)

                val colors = sorted
                    .distinctBy { it.color.lowercase() }
                    .filter { it.color.isNotEmpty() }
                details.addAll(colors)

                val quantities = sorted
                    .distinctBy { "${it.quantity} ${it.quantitySymbol.lowercase()}" }
                    .filter { it.quantity > 0f }
                details.addAll(quantities)

                val unitPrices = sorted
                    .distinctBy { it.price }
                    .filter { it.price > 0f }
                details.addAll(unitPrices)

                val discounts = sorted
                    .distinctBy { "${it.discount} ${it.discountAsPercent}" }
                    .filter { it.discount > 0f }
                details.addAll(discounts)

                val taxRates = sorted
                    .distinctBy { it.taxRate }
                    .filter { it.taxRate > 0f }
                details.addAll(taxRates)

                val costs = sorted
                    .distinctBy { it.total }
                    .filter { it.total > 0f }
                details.addAll(costs)

                details.toList()
            }
    }

    suspend fun countAutocompleteNames(name: String): Int = withIoContext {
        return@withIoContext api15Repository.getAutocompletes().count {
            it.name.equals(name, true)
        }
    }
}