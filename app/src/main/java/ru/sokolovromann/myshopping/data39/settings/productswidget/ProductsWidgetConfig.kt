package ru.sokolovromann.myshopping.data39.settings.productswidget

import ru.sokolovromann.myshopping.data39.settings.general.FontSize
import ru.sokolovromann.myshopping.data39.settings.general.Theme
import ru.sokolovromann.myshopping.data39.settings.products.GroupProducts
import ru.sokolovromann.myshopping.data39.settings.products.SortProducts

data class ProductsWidgetConfig(
    val theme: Theme,
    val fontSize: FontSize,
    val sort: SortProducts,
    val group: GroupProducts
)