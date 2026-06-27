package ru.sokolovromann.myshopping.core.data.mapper

import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.model.ProductEntity
import ru.sokolovromann.myshopping.core.domain.model.DiscountMeasurementUnit
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.ProductDirectory
import ru.sokolovromann.myshopping.core.domain.model.ProductDiscount
import ru.sokolovromann.myshopping.core.domain.model.ProductPriority
import ru.sokolovromann.myshopping.core.domain.model.ProductQuantity
import ru.sokolovromann.myshopping.core.domain.model.ProductStatus
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class ProductsMapper @Inject constructor() : RoomDatabaseMapper<ProductEntity, Product>() {

    override fun toEntity(model: Product) = ProductEntity(
        model.uid.value,
        model.directory.value.value,
        model.position.value.toString(),
        model.created.value.toString(),
        model.lastModified.value.toString(),
        model.status.toString(),
        model.priority.toString(),
        model.name,
        model.quantity?.number?.toPlainString().orEmpty(),
        model.quantity?.measurementUnit.orEmpty(),
        model.unitPrice?.toPlainString().orEmpty(),
        model.fullPrice?.toPlainString().orEmpty(),
        model.discount?.money?.toPlainString().orEmpty(),
        model.discount?.measurementUnit?.toString().orEmpty(),
        model.tax?.value?.toPlainString().orEmpty(),
        model.cost?.toPlainString().orEmpty(),
        model.note,
        model.manufacturer,
        model.brand,
        model.size,
        model.color,
        String()
    )

    override fun toModel(entity: ProductEntity) = Product(
        UID(entity.uid),
        ProductDirectory(UID(entity.directory)),
        toPositionOrMin(entity.position),
        toTimeInMillisOrCurrent(entity.created),
        toTimeInMillisOrCurrent(entity.lastModified),
        EnumUtils.valueOfOrDefault(entity.status, ProductStatus.Active),
        EnumUtils.valueOfOrDefault(entity.priority, ProductPriority.Medium),
        entity.name,
        entity.quantity.toBigDecimalOrNull()?.let {
            ProductQuantity(it, entity.quantityMeasurementUnit)
        },
        entity.unitPrice.toBigDecimalOrNull(),
        entity.fullPrice.toBigDecimalOrNull(),
        entity.discount.toBigDecimalOrNull()?.let {
            ProductDiscount(
                it,
                EnumUtils.valueOfOrDefault(entity.discountMeasurementUnit, DiscountMeasurementUnit.Percent)
            )
        },
        entity.tax.toBigDecimalOrNull()?.let { Tax(it) },
        entity.cost.toBigDecimalOrNull(),
        entity.note,
        entity.manufacturer,
        entity.brand,
        entity.size,
        entity.color
    )
}