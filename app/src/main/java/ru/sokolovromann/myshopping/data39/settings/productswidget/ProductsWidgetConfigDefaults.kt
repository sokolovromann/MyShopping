package ru.sokolovromann.myshopping.data39.settings.productswidget

import ru.sokolovromann.myshopping.data39.settings.general.FontSize
import ru.sokolovromann.myshopping.data39.settings.general.Theme
import ru.sokolovromann.myshopping.data39.settings.products.GroupProducts
import ru.sokolovromann.myshopping.data39.settings.products.SortProducts
import ru.sokolovromann.myshopping.data39.settings.products.SortProductsName

object ProductsWidgetConfigDefaults {
    val THEME: Theme = Theme.Light
    val FONT_SIZE: FontSize = FontSize.Medium
    val SORT: SortProducts = SortProducts(
        name = SortProductsName.DoNotSort,
        params = null
    )
    val GROUP: GroupProducts = GroupProducts.ActiveFirst
}