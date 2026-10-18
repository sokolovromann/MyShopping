package ru.sokolovromann.myshopping.feature.settings.action

import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct

sealed class ProductsSettingsAction {

    data class OnAddingModeSelected(val addingMode: ProductsAddingMode) : ProductsSettingsAction()

    data class OnStrikethroughCompletedSelected(val strikethroughCompleted: StrikethroughCompletedProducts) : ProductsSettingsAction()

    data class OnAfterCompletingSelected(val afterCompleting: AfterCompletingProduct) : ProductsSettingsAction()

    data class OnAfterTappingCheckboxSelected(val afterTappingCheckbox: AfterTappingByProductCheckbox) : ProductsSettingsAction()

    data class OnCheckboxColorSelected(val checkboxColor: CheckboxColor) : ProductsSettingsAction()

    data class OnAfterTappingItemSelected(val afterTappingItem: AfterTappingByProductItem) : ProductsSettingsAction()

    data class OnSwipeLeftSelected(val swipe: SwipeProduct.Left) : ProductsSettingsAction()

    data class OnSwipeRightSelected(val swipe: SwipeProduct.Right) : ProductsSettingsAction()
}