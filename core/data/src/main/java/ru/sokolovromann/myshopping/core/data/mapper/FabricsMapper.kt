package ru.sokolovromann.myshopping.core.data.mapper

import ru.sokolovromann.myshopping.core.data.model.FabricEntity
import ru.sokolovromann.myshopping.core.domain.model.DiscountMeasurementUnit
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.FabricValue
import ru.sokolovromann.myshopping.core.domain.model.ProductDiscount
import ru.sokolovromann.myshopping.core.domain.model.ProductQuantity
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

class FabricsMapper : RoomDatabaseMapper<FabricEntity, Fabric>() {

    override fun toEntity(model: Fabric) = FabricEntity(
        model.uid.value,
        model.directory.value.value,
        model.created.value.toString(),
        model.lastModified.value.toString(),
        toTypeString(model.value),
        toValueString(model.value),
        toValueParamsString(model.value),
        model.used.toString()
    )

    override fun toModel(entity: FabricEntity) = Fabric(
        UID(entity.uid),
        FabricDirectory(UID(entity.directory)),
        toTimeInMillisOrCurrent(entity.created),
        toTimeInMillisOrCurrent(entity.lastModified),
        toFabricValue(entity.type, entity.value, entity.valueParams),
        entity.used.toIntOrNull() ?: 0
    )

    private fun toTypeString(value: FabricValue) = when (value) {
        is FabricValue.QuantityType -> "Quantity"
        is FabricValue.UnitPriceType -> "UnitPrice"
        is FabricValue.DiscountType -> "Discount"
        is FabricValue.TaxType -> "Tax"
        is FabricValue.CostType -> "Cost"
        is FabricValue.ManufacturerType -> "Manufacturer"
        is FabricValue.BrandType -> "Brand"
        is FabricValue.SizeType -> "Size"
        is FabricValue.ColorType -> "Color"
        FabricValue.NoData -> ""
    }

    private fun toValueString(value: FabricValue) = when (value) {
        is FabricValue.QuantityType -> value.data.number.toPlainString()
        is FabricValue.UnitPriceType -> value.data.toPlainString()
        is FabricValue.DiscountType -> value.data.money.toPlainString()
        is FabricValue.TaxType -> value.data.value.toPlainString()
        is FabricValue.CostType -> value.data.toPlainString()
        is FabricValue.ManufacturerType -> value.data
        is FabricValue.BrandType -> value.data
        is FabricValue.SizeType -> value.data
        is FabricValue.ColorType -> value.data
        FabricValue.NoData -> ""
    }

    private fun toValueParamsString(value: FabricValue) = when(value) {
        is FabricValue.QuantityType -> value.data.measurementUnit
        is FabricValue.DiscountType -> value.data.measurementUnit.toString()
        else -> ""
    }

    private fun toFabricValue(type: String, value: String, params: String) = when (type) {
        "Quantity" -> {
            value.toBigDecimalOrNull()?.let {
                val productsQuantity = ProductQuantity(it, params)
                FabricValue.QuantityType(productsQuantity)
            } ?: FabricValue.NoData
        }
        "UnitPrice" -> {
            value.toBigDecimalOrNull()?.let {
                FabricValue.UnitPriceType(it)
            } ?: FabricValue.NoData
        }
        "Discount" -> {
            value.toBigDecimalOrNull()?.let {
                val productDiscount = ProductDiscount(
                    it,
                    EnumUtils.valueOfOrDefault(params, DiscountMeasurementUnit.Percent)
                )
                FabricValue.DiscountType(productDiscount)
            } ?: FabricValue.NoData
        }
        "Tax" -> {
            value.toBigDecimalOrNull()?.let {
                FabricValue.TaxType(Tax(it))
            } ?: FabricValue.NoData
        }
        "Cost" -> {
            value.toBigDecimalOrNull()?.let {
                FabricValue.CostType(it)
            } ?: FabricValue.NoData
        }
        "Manufacturer" -> {
            if (value.isNotEmpty()) {
                FabricValue.ManufacturerType(value)
            } else FabricValue.NoData
        }
        "Brand" -> {
            if (value.isNotEmpty()) {
                FabricValue.BrandType(value)
            } else FabricValue.NoData
        }
        "Size" -> {
            if (value.isNotEmpty()) {
                FabricValue.SizeType(value)
            } else FabricValue.NoData
        }
        "Color" -> {
            if (value.isNotEmpty()) {
                FabricValue.ColorType(value)
            } else FabricValue.NoData
        }
        else -> FabricValue.NoData
    }
}