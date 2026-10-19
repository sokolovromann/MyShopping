package ru.sokolovromann.myshopping.feature.settings.action

import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.Theme

sealed class ProductsWidgetSettingsAction {

    data class OnThemeSelected(val theme: Theme) : ProductsWidgetSettingsAction()

    data class OnFontSizeSelected(val fontSize: FontSize) : ProductsWidgetSettingsAction()

    data class OnGroupProductsSelected(val groupProducts: GroupProductsByStatus) : ProductsWidgetSettingsAction()
}