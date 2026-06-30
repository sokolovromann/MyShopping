package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
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
            preferences[LocalDataStoreScheme.ProductsWidget.THEME],
            Theme.Default
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.ProductsWidget.FONT_SIZE],
            FontSize.Medium
        ),
        toSort(
            preferences[LocalDataStoreScheme.ProductsWidget.SORT],
            preferences[LocalDataStoreScheme.ProductsWidget.SORT_BY_ASCENDING]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.ProductsWidget.GROUP_BY_STATUS],
            GroupProductsByStatus.ActiveFirst
        )
    )

    override fun toPreferences(model: ProductsWidgetPreferences) = preferencesOf(
        LocalDataStoreScheme.ProductsWidget.THEME
                to model.theme.toString(),
        LocalDataStoreScheme.ProductsWidget.FONT_SIZE
                to model.fontSize.toString(),
        LocalDataStoreScheme.ProductsWidget.SORT
                to model.sortProducts.javaClass.simpleName,
        LocalDataStoreScheme.ProductsWidget.SORT_BY_ASCENDING
                to model.sortProducts.isByAscending().toString(),
        LocalDataStoreScheme.ProductsWidget.GROUP_BY_STATUS
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