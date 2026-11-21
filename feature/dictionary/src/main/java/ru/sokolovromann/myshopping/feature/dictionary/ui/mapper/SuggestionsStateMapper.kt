package ru.sokolovromann.myshopping.feature.dictionary.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.dictionary.R
import ru.sokolovromann.myshopping.feature.dictionary.domain.model.Dictionary
import ru.sokolovromann.myshopping.feature.dictionary.ui.model.SuggestionsState
import ru.sokolovromann.myshopping.feature.dictionary.ui.model.UiSuggestion

class SuggestionsStateMapper @Inject constructor(
    private val fabricsMapper: FabricsMapper
) {

    fun toSuggestionsState(dictionary: Dictionary) = SuggestionsState(
        isProgress = false,
        suggestions = createSuggestion(dictionary),
        cells = createCells(dictionary.suggestionsPreferences.view),
        selected = emptySet(),
        sort = dictionary.suggestionsPreferences.sort,
        view = dictionary.suggestionsPreferences.view
    )

    fun toUids(items: List<UiSuggestion>): Set<UID> = items.map { it.uid }.toSet()

    private fun createSuggestion(
        dictionary: Dictionary
    ) = dictionary.suggestionsWithFabrics.map { (suggestion, fabrics) ->
        val fabricsStrings = mutableMapOf<UiText, String>()
        val prefix = ": "
        if (fabrics.quantities.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_quantities)] =
                fabrics.quantities.joinToString(prefix = prefix) {
                    fabricsMapper.quantityToString(it)
                }
        }
        if (fabrics.unitPrices.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_unit_price)] =
                fabrics.unitPrices.joinToString(prefix = prefix) {
                    fabricsMapper.unitPricesToString(it, dictionary.generalPreferences)
                }
        }
        if (fabrics.discounts.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_discounts)] =
                fabrics.discounts.joinToString(prefix = prefix) {
                    fabricsMapper.discountToString(it, dictionary.generalPreferences)
                }
        }
        if (fabrics.taxes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_taxes)] =
                fabrics.taxes.joinToString(prefix = prefix) {
                    fabricsMapper.taxRateToString(it, dictionary.generalPreferences)
                }
        }
        if (fabrics.costs.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_costs)] =
                fabrics.costs.joinToString(prefix = prefix) {
                    fabricsMapper.costToString(it, dictionary.generalPreferences)
                }
        }
        if (fabrics.manufacturers.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_manufacturers)] =
                fabrics.manufacturers.joinToString(prefix = prefix) {
                    fabricsMapper.manufacturerToString(it)
                }
        }
        if (fabrics.brands.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_brands)] =
                fabrics.brands.joinToString(prefix = prefix) {
                    fabricsMapper.brandToString(it)
                }
        }
        if (fabrics.sizes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_sizes)] =
                fabrics.sizes.joinToString(prefix = prefix) {
                    fabricsMapper.sizeToString(it)
                }
        }
        if (fabrics.colors.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_colors)] =
                fabrics.colors.joinToString(prefix = prefix) {
                    fabricsMapper.colorToString(it)
                }
        }
        UiSuggestion(
            uid = suggestion.uid,
            name = suggestion.name.toUiText(),
            fabrics = fabricsStrings
        )
    }

    private fun createCells(view: SuggestionsView) = when (view) {
        is SuggestionsView.Grid -> 2
        is SuggestionsView.List -> 1
    }
}