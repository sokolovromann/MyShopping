package ru.sokolovromann.myshopping.feature.dictionary.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.extension.toTextFieldValue
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.dictionary.R
import ru.sokolovromann.myshopping.feature.dictionary.domain.model.Support
import ru.sokolovromann.myshopping.feature.dictionary.ui.model.AddEditSuggestionState
import ru.sokolovromann.myshopping.feature.dictionary.ui.model.UiFabric

class AddEditSuggestionStateMapper @Inject constructor(
    private val fabricsMapper: FabricsMapper
) {

    fun toAddEditSuggestionState(support: Support) =
        AddEditSuggestionState(
            showKeyboard = true,
            name = support.suggestionWithFabrics?.suggestion?.name
                .orEmpty()
                .toTextFieldValue(),
            isNameError = false,
            fabrics = createFabrics(support)
        )

    fun toSuggestion(name: String): Suggestion {
        val timeInMillis = TimeInMillis.getCurrent()
        return Suggestion(
            uid = UID.createRandom(),
            directory = SuggestionDirectory.NoDirectory,
            created = timeInMillis,
            lastModified = timeInMillis,
            name = name,
            used = 0
        )
    }

    fun copySuggestion(suggestion: Suggestion, name: String): Suggestion {
        val timeInMillis = TimeInMillis.getCurrent()
        return suggestion.copy(
            lastModified = timeInMillis,
            name = name,
            used = suggestion.used.plus(1)
        )
    }

    private fun createFabrics(
        support: Support
    ): Map<UiText, List<UiFabric>> {
        val fabrics = support.suggestionWithFabrics?.fabrics ?: return emptyMap()
        val preferences = support.generalPreferences

        val fabricsStrings = mutableMapOf<UiText, List<UiFabric>>()
        if (fabrics.quantities.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_quantities)] =
                fabrics.quantities.map {
                    UiFabric(it.uid, fabricsMapper.quantityToString(it))
                }
        }
        if (fabrics.unitPrices.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_unit_price)] =
                fabrics.unitPrices.map {
                    UiFabric(it.uid, fabricsMapper.unitPricesToString(it, preferences))
                }
        }
        if (fabrics.discounts.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_discounts)] =
                fabrics.discounts.map {
                    UiFabric(it.uid, fabricsMapper.discountToString(it, preferences))
                }
        }
        if (fabrics.taxes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_taxes)] =
                fabrics.taxes.map {
                    UiFabric(it.uid, fabricsMapper.taxRateToString(it, preferences))
                }
        }
        if (fabrics.costs.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_costs)] =
                fabrics.costs.map {
                    UiFabric(it.uid, fabricsMapper.costToString(it, preferences))
                }
        }
        if (fabrics.manufacturers.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_manufacturers)] =
                fabrics.manufacturers.map {
                    UiFabric(it.uid, fabricsMapper.manufacturerToString(it))
                }
        }
        if (fabrics.brands.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_brands)] =
                fabrics.brands.map {
                    UiFabric(it.uid, fabricsMapper.brandToString(it))
                }
        }
        if (fabrics.sizes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_sizes)] =
                fabrics.sizes.map {
                    UiFabric(it.uid, fabricsMapper.sizeToString(it))
                }
        }
        if (fabrics.colors.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_colors)] =
                fabrics.colors.map {
                    UiFabric(it.uid, fabricsMapper.colorToString(it))
                }
        }
        return fabricsStrings
    }
}