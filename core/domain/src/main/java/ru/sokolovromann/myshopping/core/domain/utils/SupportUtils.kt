package ru.sokolovromann.myshopping.core.domain.utils

import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricValue
import ru.sokolovromann.myshopping.core.domain.model.FilteredFabricsByType
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.model.Support

object SupportUtils {

    fun createSupport(suggestionWithFabrics: SuggestionWithFabrics): Support {
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
                .take(),
            suggestion.used
        )
    }

    private fun Collection<Fabric>.filteredByType(): FilteredFabricsByType {
        val quantities = mutableListOf<Fabric>()
        val unitPrices = mutableListOf<Fabric>()
        val discounts = mutableListOf<Fabric>()
        val taxes = mutableListOf<Fabric>()
        val costs = mutableListOf<Fabric>()
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
                is FabricValue.CostType -> costs.add(it)
                is FabricValue.ManufacturerType -> manufacturers.add(it)
                is FabricValue.BrandType -> brands.add(it)
                is FabricValue.SizeType -> sizes.add(it)
                is FabricValue.ColorType -> colors.add(it)
                FabricValue.NoData -> {}
            }
        }
        return FilteredFabricsByType(
            quantities, unitPrices, discounts, taxes, costs, manufacturers, brands, sizes, colors
        )
    }

    private fun FilteredFabricsByType.take() =
        FilteredFabricsByType(
            quantities.take(3),
            unitPrices.take(3),
            discounts.take(3),
            taxes.take(3),
            costs.take(3),
            manufacturers.take(3),
            brands.take(3),
            sizes.take(3),
            colors.take(3)
        )
}