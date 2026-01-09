package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.data39.LocalRoomMapper
import javax.inject.Inject

class SuggestionDetailsMapper @Inject constructor() : LocalRoomMapper<SuggestionDetailRoomEntity, SuggestionDetail>() {

    override fun toEntity(model: SuggestionDetail): SuggestionDetailRoomEntity {
        val value: String
        val valueParams: String
        when (model) {
            is SuggestionDetail.Image -> {
                value = model.value.data
                valueParams = ""
            }
            is SuggestionDetail.Manufacturer -> {
                value = model.value.data
                valueParams = ""
            }
            is SuggestionDetail.Brand -> {
                value = model.value.data
                valueParams = ""
            }
            is SuggestionDetail.Size -> {
                value = model.value.data
                valueParams = ""
            }
            is SuggestionDetail.Color -> {
                value = model.value.data
                valueParams = ""
            }
            is SuggestionDetail.Quantity -> {
                val valueWithParams = fromDecimalWithParams(model.value.data)
                value = valueWithParams.first
                valueParams = valueWithParams.second
            }
            is SuggestionDetail.UnitPrice -> {
                value = fromDecimal(model.value.data)
                valueParams = ""
            }
            is SuggestionDetail.Discount -> {
                val valueWithParams = fromDecimalWithParams(model.value.data)
                value = valueWithParams.first
                valueParams = valueWithParams.second
            }
            is SuggestionDetail.TaxRate -> {
                value = fromDecimal(model.value.data)
                valueParams = ""
            }
            is SuggestionDetail.Cost -> {
                value = fromDecimal(model.value.data)
                valueParams = ""
            }
            is SuggestionDetail.Text -> {
                value = model.value.data
                valueParams = ""
            }
        }
        return SuggestionDetailRoomEntity(
            uid = fromUid(model.getUid()),
            directory = fromUid(model.getDirectory()),
            created = fromDateTime(model.getCreated()),
            type = model.getClassName(),
            value = value,
            valueParams = valueParams
        )
    }

    override fun fromEntity(entity: SuggestionDetailRoomEntity): SuggestionDetail {
        val uid = toUid(entity.uid)
        val directory = toUid(entity.directory)
        val created = toDateTime(entity.created)
        return when (entity.type) {
            "Image" -> {
                val value = SuggestionDetailValue(uid, directory, created, entity.value)
                SuggestionDetail.Image(value)
            }
            "Manufacturer" -> {
                val value = SuggestionDetailValue(uid, directory, created, entity.value)
                SuggestionDetail.Manufacturer(value)
            }
            "Brand" -> {
                val value = SuggestionDetailValue(uid, directory, created, entity.value)
                SuggestionDetail.Brand(value)
            }
            "Size" -> {
                val value = SuggestionDetailValue(uid, directory, created, entity.value)
                SuggestionDetail.Size(value)
            }
            "Color" -> {
                val value = SuggestionDetailValue(uid, directory, created, entity.value)
                SuggestionDetail.Color(value)
            }
            "Quantity" -> {
                val params = toDecimalWithParams(entity.value, entity.valueParams)
                val value = SuggestionDetailValue(uid, directory, created, params)
                SuggestionDetail.Quantity(value)
            }
            "UnitPrice" -> {
                val params = toDecimal(entity.value)
                val value = SuggestionDetailValue(uid, directory, created, params)
                SuggestionDetail.UnitPrice(value)
            }
            "Discount" -> {
                val params = toDecimalWithParams(
                    entity.value,
                    toEnum(entity.valueParams, SuggestionsDefaults.DISCOUNT_TYPE)
                )
                val value = SuggestionDetailValue(uid, directory, created, params)
                SuggestionDetail.Discount(value)
            }
            "TaxRate" -> {
                val params = toDecimal(entity.value)
                val value = SuggestionDetailValue(uid, directory, created, params)
                SuggestionDetail.TaxRate(value)
            }
            "Cost" -> {
                val params = toDecimal(entity.value)
                val value = SuggestionDetailValue(uid, directory, created, params)
                SuggestionDetail.Cost(value)
            }
            else -> {
                val value = SuggestionDetailValue(uid, directory, created, entity.value)
                SuggestionDetail.Text(value)
            }
        }
    }

    fun toDetails(entities: Collection<SuggestionDetailRoomEntity>): SuggestionDetails {
        val images: MutableList<SuggestionDetail.Image> = mutableListOf()
        val manufacturers: MutableList<SuggestionDetail.Manufacturer> = mutableListOf()
        val brands: MutableList<SuggestionDetail.Brand> = mutableListOf()
        val sizes: MutableList<SuggestionDetail.Size> = mutableListOf()
        val colors: MutableList<SuggestionDetail.Color> = mutableListOf()
        val quantities: MutableList<SuggestionDetail.Quantity> = mutableListOf()
        val unitPrices: MutableList<SuggestionDetail.UnitPrice> = mutableListOf()
        val discounts: MutableList<SuggestionDetail.Discount> = mutableListOf()
        val taxRates: MutableList<SuggestionDetail.TaxRate> = mutableListOf()
        val costs: MutableList<SuggestionDetail.Cost> = mutableListOf()
        fromEntities(entities).forEach {
            when (it) {
                is SuggestionDetail.Image -> { images.add(it) }
                is SuggestionDetail.Manufacturer -> { manufacturers.add(it) }
                is SuggestionDetail.Brand -> { brands.add(it) }
                is SuggestionDetail.Size -> { sizes.add(it) }
                is SuggestionDetail.Color -> { colors.add(it) }
                is SuggestionDetail.Quantity -> { quantities.add(it) }
                is SuggestionDetail.UnitPrice -> { unitPrices.add(it) }
                is SuggestionDetail.Discount -> { discounts.add(it) }
                is SuggestionDetail.TaxRate -> { taxRates.add(it) }
                is SuggestionDetail.Cost -> { costs.add(it) }
                is SuggestionDetail.Text -> {}
            }
        }
        return SuggestionDetails(
            images = images,
            manufacturers = manufacturers,
            brands = brands,
            sizes = sizes,
            colors = colors,
            quantities = quantities,
            unitPrices = unitPrices,
            discounts = discounts,
            taxRates = taxRates,
            costs = costs
        )
    }
}