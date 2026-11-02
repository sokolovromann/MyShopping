package ru.sokolovromann.myshopping.feature.dictionary.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.Dictionary
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.dictionary.R
import ru.sokolovromann.myshopping.feature.dictionary.model.SuggestionItem
import ru.sokolovromann.myshopping.feature.dictionary.model.SuggestionsState

class SuggestionsMapper @Inject constructor(
    private val fabricsMapper: FabricsMapper
) {

    fun createSuggestionState(
        dictionary: Dictionary,
        suggestionsPreferences: SuggestionsPreferences,
        generalPreferences: GeneralPreferences
    ) = SuggestionsState(
        isLoading = false,
        items = createItems(dictionary, generalPreferences),
        itemsCells = createCells(suggestionsPreferences.view),
        itemsSelected = emptySet()
    )

    fun toUids(items: List<SuggestionItem>): Set<UID> = items.map { it.uid }.toSet()

    private fun createItems(
        dictionary: Dictionary,
        generalPreferences: GeneralPreferences
    ) = dictionary.supports.map { support ->
        val fabricsStrings = mutableMapOf<UiText, String>()
        val fabricsByType = support.filteredFabricsByType
        val prefix = ": "
        if (fabricsByType.quantities.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_quantities)] =
                fabricsByType.quantities.joinToString(prefix = prefix) {
                    fabricsMapper.quantityToString(it)
                }
        }
        if (fabricsByType.unitPrices.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_unit_price)] =
                fabricsByType.unitPrices.joinToString(prefix = prefix) {
                    fabricsMapper.unitPricesToString(it, generalPreferences)
                }
        }
        if (fabricsByType.discounts.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_discounts)] =
                fabricsByType.discounts.joinToString(prefix = prefix) {
                    fabricsMapper.discountToString(it, generalPreferences)
                }
        }
        if (fabricsByType.taxes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_taxes)] =
                fabricsByType.taxes.joinToString(prefix = prefix) {
                    fabricsMapper.taxRateToString(it, generalPreferences)
                }
        }
        if (fabricsByType.costs.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_costs)] =
                fabricsByType.costs.joinToString(prefix = prefix) {
                    fabricsMapper.costToString(it, generalPreferences)
                }
        }
        if (fabricsByType.manufacturers.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_manufacturers)] =
                fabricsByType.manufacturers.joinToString(prefix = prefix) {
                    fabricsMapper.manufacturerToString(it)
                }
        }
        if (fabricsByType.brands.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_brands)] =
                fabricsByType.brands.joinToString(prefix = prefix) {
                    fabricsMapper.brandToString(it)
                }
        }
        if (fabricsByType.sizes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_sizes)] =
                fabricsByType.sizes.joinToString(prefix = prefix) {
                    fabricsMapper.sizeToString(it)
                }
        }
        if (fabricsByType.colors.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_colors)] =
                fabricsByType.colors.joinToString(prefix = prefix) {
                    fabricsMapper.colorToString(it)
                }
        }
        SuggestionItem(
            uid = support.uid,
            name = support.name.toUiText(),
            fabrics = fabricsStrings
        )
    }

    private fun createCells(view: SuggestionsView) = when (view) {
        is SuggestionsView.Grid -> 2
        is SuggestionsView.List -> 1
    }
}