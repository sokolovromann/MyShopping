package ru.sokolovromann.myshopping.manager

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.Api15AutocompletesConfigEntity
import ru.sokolovromann.myshopping.data39.old.Api15Repository
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject
import kotlin.collections.distinctBy

class Api15Manager @Inject constructor(private val api15Repository: Api15Repository) {

    suspend fun getAutocompletes(): Map<String, List<Api15AutocompleteEntity>> = withIoContext {
        val displayDefault = getAutocompletesConfig().displayDefaultAutocompletes
        return@withIoContext api15Repository.getAutocompletes(displayDefault)
            .groupBy { it.name.lowercase() }
            .mapKeys { (key, _) -> key.replaceFirstChar { it.uppercase() } }
            .mapValues { (_, value) ->
                val sorted = value.sortedBy { it.lastModified }
                mutableListOf<Api15AutocompleteEntity>().apply {
                    addAll(sorted.filter("Manufacturer"))
                    addAll(sorted.filter("Brands"))
                    addAll(sorted.filter("Sizes"))
                    addAll(sorted.filter("Colors"))
                    addAll(sorted.filter("Quantities"))
                    addAll(sorted.filter("UnitPrices"))
                    addAll(sorted.filter("Discounts"))
                    addAll(sorted.filter("TaxRates"))
                    addAll(sorted.filter("Costs"))
                }
            }
    }

    suspend fun getAutocompletesConfig(): Api15AutocompletesConfigEntity = withIoContext {
        return@withIoContext api15Repository.getAutocompletesConfig()
    }

    suspend fun countAutocompleteNames(name: String): Int = withIoContext {
        val displayDefault = getAutocompletesConfig().displayDefaultAutocompletes
        return@withIoContext api15Repository.getAutocompletes(displayDefault)
            .count { it.name.equals(name, true) }
    }

    suspend fun countAutocompleteDetails(name: String, detailName: String): Int = withIoContext {
        val displayDefault = getAutocompletesConfig().displayDefaultAutocompletes
        return@withIoContext api15Repository.getAutocompletes(displayDefault)
            .filter { it.name.equals(name, true) }
            .filter(detailName)
            .count()
    }

    private fun Collection<Api15AutocompleteEntity>.filter(field: String): List<Api15AutocompleteEntity> {
        return when (field) {
            "Manufacturer" -> {
                distinctBy { it.manufacturer.lowercase() }
                    .filter { it.manufacturer.isNotEmpty() }
            }
            "Brands" -> {
                distinctBy { it.brand.lowercase() }
                    .filter { it.brand.isNotEmpty() }
            }
            "Sizes" -> {
                distinctBy { it.size.lowercase() }
                    .filter { it.size.isNotEmpty() }
            }
            "Colors" -> {
                distinctBy { it.color.lowercase() }
                    .filter { it.color.isNotEmpty() }
            }
            "Quantities" -> {
                distinctBy { "${it.quantity} ${it.quantitySymbol.lowercase()}" }
                    .filter { it.quantity > 0f }
            }
            "UnitPrices" -> {
                distinctBy { it.price }
                    .filter { it.price > 0f }
            }
            "Discounts" -> {
                distinctBy { "${it.discount} ${it.discountAsPercent}" }
                    .filter { it.discount > 0f }
            }
            "TaxRates" -> {
                distinctBy { it.taxRate }
                    .filter { it.taxRate > 0f }
            }
            "Costs" -> {
                distinctBy { it.total }
                    .filter { it.total > 0f }
            }
            else -> {
                this.toList()
            }
        }
    }
}