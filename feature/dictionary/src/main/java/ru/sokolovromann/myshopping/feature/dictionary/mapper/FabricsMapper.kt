package ru.sokolovromann.myshopping.feature.dictionary.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricValue
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.utils.MoneyUtils
import ru.sokolovromann.myshopping.core.domain.utils.QuantityUtils

class FabricsMapper @Inject constructor() {

    fun quantityToString(fabric: Fabric) = QuantityUtils.format(
        (fabric.value as FabricValue.QuantityType).data
    )

    fun unitPricesToString(fabric: Fabric, preferences: GeneralPreferences) =
        MoneyUtils.format(
            bigDecimal = (fabric.value as FabricValue.UnitPriceType).data,
            currency = preferences.currency,
            formattingMode = preferences.moneyFormattingMode
        )

    fun discountToString(fabric: Fabric, preferences: GeneralPreferences) =
        MoneyUtils.format(
            discount = (fabric.value as FabricValue.DiscountType).data,
            currency = preferences.currency,
            formattingMode = preferences.moneyFormattingMode
        )

    fun taxRateToString(fabric: Fabric, preferences: GeneralPreferences) =
        MoneyUtils.format(
            bigDecimal = (fabric.value as FabricValue.TaxType).data.value,
            currency = preferences.currency,
            formattingMode = preferences.moneyFormattingMode
        )

    fun costToString(fabric: Fabric, preferences: GeneralPreferences) =
        MoneyUtils.format(
            bigDecimal = (fabric.value as FabricValue.CostType).data,
            currency = preferences.currency,
            formattingMode = preferences.moneyFormattingMode
        )

    fun manufacturerToString(fabric: Fabric) = (fabric.value as FabricValue.ManufacturerType).data

    fun brandToString(fabric: Fabric) = (fabric.value as FabricValue.BrandType).data

    fun sizeToString(fabric: Fabric) = (fabric.value as FabricValue.SizeType).data

    fun colorToString(fabric: Fabric) = (fabric.value as FabricValue.ColorType).data
}