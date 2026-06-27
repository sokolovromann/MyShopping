package ru.sokolovromann.myshopping.core.data.mapper

import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.model.CartEntity
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.model.CartBudget
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartDiscount
import ru.sokolovromann.myshopping.core.domain.model.CartPriority
import ru.sokolovromann.myshopping.core.domain.model.CartReminder
import ru.sokolovromann.myshopping.core.domain.model.CartTotal
import ru.sokolovromann.myshopping.core.domain.model.DiscountMeasurementUnit
import ru.sokolovromann.myshopping.core.domain.model.FilterProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.RepeatCartReminder
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class CartsMapper @Inject constructor(
    private val productsPreferencesMapper: ProductsPreferencesMapper
) : RoomDatabaseMapper<CartEntity, Cart>() {

    override fun toEntity(model: Cart) = CartEntity(
        model.uid.value,
        model.directory.toString(),
        model.position.value.toString(),
        model.created.value.toString(),
        model.lastModified.value.toString(),
        model.priority.toString(),
        model.name,
        model.reminder?.time?.value?.toString().orEmpty(),
        model.reminder?.repeat?.toString().orEmpty(),
        model.discount?.money?.toPlainString().orEmpty(),
        model.discount?.measurementUnit?.toString().orEmpty(),
        model.discount?.filterByStatus?.toString().orEmpty(),
        model.total?.money?.toPlainString().orEmpty(),
        model.total?.filterByStatus?.toString().orEmpty(),
        model.budget?.money?.toPlainString().orEmpty(),
        model.budget?.filterByStatus?.toString().orEmpty(),
        model.note,
        String(),
        model.sortProducts?.javaClass?.simpleName.orEmpty(),
        model.sortProducts?.isByAscending()?.toString().orEmpty(),
        model.groupProductsByStatus?.toString().orEmpty()
    )

    override fun toModel(entity: CartEntity) = Cart(
        UID(entity.uid),
        EnumUtils.valueOfOrDefault(entity.directory, CartDirectory.Current),
        toPositionOrMin(entity.position),
        toTimeInMillisOrCurrent(entity.created),
        toTimeInMillisOrCurrent(entity.lastModified),
        EnumUtils.valueOfOrDefault(entity.priority, CartPriority.Medium),
        entity.name,
        entity.reminder.toLongOrNull()?.let {
            CartReminder(
                TimeInMillis(it),
                EnumUtils.valueOfOrDefault(entity.repeatReminder, RepeatCartReminder.NoRepeat)
            )
        },
        entity.discount.toBigDecimalOrNull()?.let {
            CartDiscount(
                it,
                EnumUtils.valueOfOrDefault(entity.discountMeasurementUnit, DiscountMeasurementUnit.Percent),
                EnumUtils.valueOfOrDefault(entity.filterDiscountByProductStatus, FilterProductsByStatus.All)
            )
        },
        entity.total.toBigDecimalOrNull()?.let {
            CartTotal(
                it,
                EnumUtils.valueOfOrDefault(entity.filterTotalByProductStatus, FilterProductsByStatus.All)
            )
        },
        entity.budget.toBigDecimalOrNull()?.let {
            CartBudget(
                it,
                EnumUtils.valueOfOrDefault(entity.filterBudgetByProductStatus, FilterProductsByStatus.All)
            )
        },
        entity.note,
        productsPreferencesMapper.toSort(
            entity.sortProduct,
            entity.sortProductByAscending
        ),
        productsPreferencesMapper.toGroupByStatus(entity.groupProductByStatus)
    )
}