package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.ProductsWidgetPreferencesScheme
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.SortProducts
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class ProductsWidgetPreferencesMapper @Inject constructor() : DataStoreMapper<ProductsWidgetPreferences>() {

    override fun toModel(preferences: Preferences) = ProductsWidgetPreferences(
        EnumUtils.valueOfOrDefault(
            preferences[ProductsWidgetPreferencesScheme.THEME_KEY],
            Theme.Default
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsWidgetPreferencesScheme.FONT_SIZE_KEY],
            FontSize.Medium
        ),
        toSort(
            preferences[ProductsWidgetPreferencesScheme.SORT_KEY],
            preferences[ProductsWidgetPreferencesScheme.SORT_BY_ASCENDING_KEY]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[ProductsWidgetPreferencesScheme.GROUP_BY_STATUS_KEY],
            GroupProductsByStatus.ActiveFirst
        )
    )

    override fun toPreferences(model: ProductsWidgetPreferences) = preferencesOf(
        ProductsWidgetPreferencesScheme.THEME_KEY
                to model.theme.toString(),
        ProductsWidgetPreferencesScheme.FONT_SIZE_KEY
                to model.fontSize.toString(),
        ProductsWidgetPreferencesScheme.SORT_KEY
                to model.sortProducts.javaClass.simpleName,
        ProductsWidgetPreferencesScheme.SORT_BY_ASCENDING_KEY
                to model.sortProducts.isByAscending().toString(),
        ProductsWidgetPreferencesScheme.GROUP_BY_STATUS_KEY
                to model.groupProductsByStatus.javaClass.simpleName,
    )

    private fun toSort(sort: String?, sortByAscending: String?): SortProducts {
        val byAscending = sortByAscending.toBoolean()
        return when (sort) {
            "ByCreated" -> SortProducts.ByCreated(byAscending)
            "ByLastModified" -> SortProducts.ByLastModified(byAscending)
            "ByName" -> SortProducts.ByName(byAscending)
            "ByCost" -> SortProducts.ByCost(byAscending)
            "DoNotSort" -> SortProducts.DoNotSort
            else -> SortProducts.DoNotSort
        }
    }
}