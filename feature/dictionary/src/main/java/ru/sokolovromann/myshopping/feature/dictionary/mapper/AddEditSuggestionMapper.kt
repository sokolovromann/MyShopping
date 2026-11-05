package ru.sokolovromann.myshopping.feature.dictionary.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionDirectory
import ru.sokolovromann.myshopping.core.domain.model.Support
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.core.ui.utils.toTextFieldValue
import ru.sokolovromann.myshopping.feature.dictionary.R
import ru.sokolovromann.myshopping.feature.dictionary.model.AddEditSuggestionState
import ru.sokolovromann.myshopping.feature.dictionary.model.FabricItem

class AddEditSuggestionMapper @Inject constructor(
    private val fabricsMapper: FabricsMapper
) {

    fun getDefaultAddEditSuggestionState() = AddEditSuggestionState()

    fun createAddEditSuggestionState(support: Support, preferences: GeneralPreferences) =
        AddEditSuggestionState(
            showKeyboard = true,
            name = support.name.toTextFieldValue(),
            isNameError = false,
            fabrics = createFabrics(support, preferences)
        )

    fun createSuggestion(name: String): Suggestion {
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

    fun changeSuggestion(support: Support, name: String): Suggestion {
        val timeInMillis = TimeInMillis.getCurrent()
        return Suggestion(
            uid = support.uid,
            directory = support.directory,
            created = support.created,
            lastModified = timeInMillis,
            name = name,
            used = support.used.plus(1)
        )
    }

    private fun createFabrics(
        support: Support,
        preferences: GeneralPreferences
    ): Map<UiText, List<FabricItem>> {
        val fabricsStrings = mutableMapOf<UiText, List<FabricItem>>()
        val fabricsByType = support.filteredFabricsByType
        if (fabricsByType.quantities.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_quantities)] =
                fabricsByType.quantities.map {
                    FabricItem(it.uid, fabricsMapper.quantityToString(it))
                }
        }
        if (fabricsByType.unitPrices.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_unit_price)] =
                fabricsByType.unitPrices.map {
                    FabricItem(it.uid, fabricsMapper.unitPricesToString(it, preferences))
                }
        }
        if (fabricsByType.discounts.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_discounts)] =
                fabricsByType.discounts.map {
                    FabricItem(it.uid, fabricsMapper.discountToString(it, preferences))
                }
        }
        if (fabricsByType.taxes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_taxes)] =
                fabricsByType.taxes.map {
                    FabricItem(it.uid, fabricsMapper.taxRateToString(it, preferences))
                }
        }
        if (fabricsByType.costs.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_costs)] =
                fabricsByType.costs.map {
                    FabricItem(it.uid, fabricsMapper.costToString(it, preferences))
                }
        }
        if (fabricsByType.manufacturers.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_manufacturers)] =
                fabricsByType.manufacturers.map {
                    FabricItem(it.uid, fabricsMapper.manufacturerToString(it))
                }
        }
        if (fabricsByType.brands.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_brands)] =
                fabricsByType.brands.map {
                    FabricItem(it.uid, fabricsMapper.brandToString(it))
                }
        }
        if (fabricsByType.sizes.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_sizes)] =
                fabricsByType.sizes.map {
                    FabricItem(it.uid, fabricsMapper.sizeToString(it))
                }
        }
        if (fabricsByType.colors.isNotEmpty()) {
            fabricsStrings[UiText.FromResources(R.string.dictionary_item_colors)] =
                fabricsByType.colors.map {
                    FabricItem(it.uid, fabricsMapper.colorToString(it))
                }
        }
        return fabricsStrings
    }
}