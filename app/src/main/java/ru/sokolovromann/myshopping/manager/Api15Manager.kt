package ru.sokolovromann.myshopping.manager

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.Api15AutocompletesConfigEntity
import ru.sokolovromann.myshopping.data39.old.Api15Repository
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.math.DiscountType
import javax.inject.Inject

class Api15Manager @Inject constructor(private val api15Repository: Api15Repository) {

    suspend fun getAutocompletes(): Collection<Api15AutocompleteEntity> = withIoContext {
        val displayDefault = getAutocompletesConfig().displayDefaultAutocompletes
        return@withIoContext api15Repository.getAutocompletes(displayDefault)
            .sortedBy { it.lastModified }
    }

    suspend fun getAutocompletesConfig(): Api15AutocompletesConfigEntity = withIoContext {
        return@withIoContext api15Repository.getAutocompletesConfig()
    }

    suspend fun addAutocompletes(
        suggestion: Suggestion,
        details: Collection<SuggestionDetail>
    ): Unit = withIoContext {
        val autocompletes = mutableListOf<Api15AutocompleteEntity>()

        details.forEach { detail ->
            when (detail) {
                is SuggestionDetail.Manufacturer -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            manufacturer = detail.value.data
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.Brand -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            brand = detail.value.data
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.Size -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            size = detail.value.data
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.Color -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            color = detail.value.data
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.Quantity -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            quantity = detail.value.data.decimal.toFloatOrZero(),
                            quantitySymbol = detail.value.data.params
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.UnitPrice -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            price = detail.value.data.toFloatOrZero()
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.Discount -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            discount = detail.value.data.decimal.toFloatOrZero(),
                            discountAsPercent = detail.value.data.params == DiscountType.Percent
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.TaxRate -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            taxRate = detail.value.data.toFloatOrZero(),
                            taxRateAsPercent = true
                        )
                        autocompletes.add(entity)
                    }
                }
                is SuggestionDetail.Cost -> {
                    (1..detail.value.used).forEach { _ ->
                        val entity = Api15AutocompleteEntity(
                            uid = UID.createRandom().value,
                            name = suggestion.name,
                            total = detail.value.data.toFloatOrZero()
                        )
                        autocompletes.add(entity)
                    }
                }
                else -> {}
            }
        }
        api15Repository.addAutocompletes(autocompletes)
    }

    suspend fun addAutocomplete(suggestion: Suggestion): Unit = withIoContext {
        val autocomplete = Api15AutocompleteEntity(
            uid = UID.createRandom().value,
            name = suggestion.name
        )
        api15Repository.addAutocomplete(autocomplete)
    }
}