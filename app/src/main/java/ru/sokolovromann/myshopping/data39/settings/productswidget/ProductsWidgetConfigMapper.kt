package ru.sokolovromann.myshopping.data39.settings.productswidget

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import ru.sokolovromann.myshopping.data39.settings.general.FontSize
import ru.sokolovromann.myshopping.data39.settings.general.Theme
import ru.sokolovromann.myshopping.data39.settings.products.GroupProducts
import ru.sokolovromann.myshopping.data39.settings.products.SortProducts
import ru.sokolovromann.myshopping.data39.settings.products.SortProductsName
import ru.sokolovromann.myshopping.data39.settings.products.SortProductsParams
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class ProductsWidgetConfigMapper @Inject constructor() : Mapper<Preferences, ProductsWidgetConfig>() {

    override fun mapEntityTo(entity: Preferences): ProductsWidgetConfig {
        return ProductsWidgetConfig(
            theme = mapThemeTo(entity),
            fontSize = mapFontSizeTo(entity),
            sort = mapSortTo(entity),
            group = mapGroupTo(entity)
        )
    }

    override fun mapEntityFrom(model: ProductsWidgetConfig): Preferences {
        return mutablePreferencesOf().apply {
            val theme = mapThemeFrom(model.theme)
            plusAssign(theme)

            val fontSize = mapFontSizeFrom(model.fontSize)
            plusAssign(fontSize)

            val sort = mapSortFrom(model.sort)
            plusAssign(sort)

            val group = mapGroupFrom(model.group)
            plusAssign(group)
        }
    }

    fun mapThemeTo(entity: Preferences): Theme {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsWidgetConfigScheme.THEME],
            ProductsWidgetConfigDefaults.THEME
        )
    }

    fun mapThemeFrom(model: Theme): Preferences {
        return preferencesOf(
            ProductsWidgetConfigScheme.THEME to model.name,
        )
    }

    fun mapFontSizeTo(entity: Preferences): FontSize {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsWidgetConfigScheme.FONT_SIZE],
            ProductsWidgetConfigDefaults.FONT_SIZE
        )
    }

    fun mapFontSizeFrom(model: FontSize): Preferences {
        return preferencesOf(
            ProductsWidgetConfigScheme.FONT_SIZE to model.name
        )
    }

    fun mapSortTo(entity: Preferences): SortProducts {
        val default = ProductsWidgetConfigDefaults.SORT
        val name: SortProductsName = EnumExtensions.valueOfOrDefault(
            entity[ProductsWidgetConfigScheme.SORT],
            default.name
        )
        val params: SortProductsParams? = EnumExtensions.valueOfOrNull<SortProductsParams>(
            entity[ProductsWidgetConfigScheme.SORT_PARAMS]
        )
        return SortProducts(name, params)
    }

    fun mapSortFrom(model: SortProducts): Preferences {
        return preferencesOf(
            ProductsWidgetConfigScheme.SORT to model.name.name,
            ProductsWidgetConfigScheme.SORT_PARAMS to model.params?.name.orEmpty(),
        )
    }

    fun mapGroupTo(entity: Preferences): GroupProducts {
        return EnumExtensions.valueOfOrDefault(
            entity[ProductsWidgetConfigScheme.GROUP],
            ProductsWidgetConfigDefaults.GROUP
        )
    }

    fun mapGroupFrom(model: GroupProducts): Preferences {
        return preferencesOf(
            ProductsWidgetConfigScheme.GROUP to model.name
        )
    }
}