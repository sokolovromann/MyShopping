package ru.sokolovromann.myshopping.core.domain.utils

import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricValue
import ru.sokolovromann.myshopping.core.domain.model.FilteredFabricsByType
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.model.Support

object SupportUtils {

    fun createSupport(
        suggestionWithFabrics: SuggestionWithFabrics,
        displaySuggestionDetails: DisplaySuggestionDetails
    ): Support {
        val suggestion = suggestionWithFabrics.suggestion
        return Support(
            suggestion.uid,
            suggestion.directory,
            suggestion.created,
            suggestion.lastModified,
            suggestion.name,
            suggestionWithFabrics.fabrics
                .sortedByDescending { it.lastModified.value }
                .filteredByType()
                .take(displaySuggestionDetails),
            suggestion.used
        )
    }

    private fun Collection<Fabric>.filteredByType(): FilteredFabricsByType {
        val quantities = mutableListOf<Fabric>()
        val unitPrices = mutableListOf<Fabric>()
        val discounts = mutableListOf<Fabric>()
        val taxes = mutableListOf<Fabric>()
        val cost = mutableListOf<Fabric>()
        val manufacturers = mutableListOf<Fabric>()
        val brands = mutableListOf<Fabric>()
        val sizes = mutableListOf<Fabric>()
        val colors = mutableListOf<Fabric>()
        forEach {
            when (it.value) {
                is FabricValue.QuantityType -> quantities.add(it)
                is FabricValue.UnitPriceType -> unitPrices.add(it)
                is FabricValue.DiscountType -> discounts.add(it)
                is FabricValue.TaxType -> taxes.add(it)
                is FabricValue.CostType -> cost.add(it)
                is FabricValue.ManufacturerType -> manufacturers.add(it)
                is FabricValue.BrandType -> brands.add(it)
                is FabricValue.ColorType -> sizes.add(it)
                is FabricValue.SizeType -> colors.add(it)
                FabricValue.NoData -> {}
            }
        }
        return FilteredFabricsByType(
            quantities, unitPrices, discounts, taxes, cost, manufacturers, brands, sizes, colors
        )
    }

    private fun FilteredFabricsByType.take(
        displaySuggestionDetails: DisplaySuggestionDetails
    ): FilteredFabricsByType {
        fun <T> Iterable<T>.take(): List<T> = when (displaySuggestionDetails) {
            DisplaySuggestionDetails.Low -> this.take(3)
            DisplaySuggestionDetails.Medium -> this.take(5)
            DisplaySuggestionDetails.Many -> this.take(10)
            DisplaySuggestionDetails.All -> this
            DisplaySuggestionDetails.DoNotDisplay -> emptyList()
        }.toList()
        return FilteredFabricsByType(
            quantities.take(),
            unitPrices.take(),
            discounts.take(),
            taxes.take(),
            cost.take(),
            manufacturers.take(),
            brands.take(),
            sizes.take(),
            colors.take()
        )
    }
}