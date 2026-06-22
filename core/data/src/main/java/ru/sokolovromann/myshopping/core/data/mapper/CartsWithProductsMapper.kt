package ru.sokolovromann.myshopping.core.data.mapper

import ru.sokolovromann.myshopping.core.data.model.CartWithProductsEntity
import ru.sokolovromann.myshopping.core.domain.model.CartWithProducts

class CartsWithProductsMapper(
    private val cartsMapper: CartsMapper,
    private val productsMapper: ProductsMapper
) : RoomDatabaseMapper<CartWithProductsEntity, CartWithProducts>() {

    override fun toEntity(model: CartWithProducts) = CartWithProductsEntity(
        cartsMapper.toEntity(model.cart),
        productsMapper.toEntities(model.products)
    )

    override fun toModel(entity: CartWithProductsEntity) = CartWithProducts(
        cartsMapper.toModel(entity.cart),
        productsMapper.toModels(entity.products)
    )
}