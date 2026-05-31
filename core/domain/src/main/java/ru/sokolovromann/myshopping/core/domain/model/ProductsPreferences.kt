package ru.sokolovromann.myshopping.core.domain.model

data class ProductsPreferences(
    val view: ProductsView,
    val sort: SortProducts,
    val groupByStatus: GroupProductsByStatus,
    val addingMode: ProductsAddingMode,
    val calculateTotal: CalculateProductsTotal,
    val strikethroughCompleted: StrikethroughCompletedProducts,
    val afterCompleting: AfterCompletingProduct,
    val afterTappingByCheckbox: AfterTappingByProductCheckbox,
    val checkboxColor: CheckboxColor,
    val afterTappingByItem: AfterTappingByProductItem,
    val swipeLeft: SwipeProduct.Left,
    val swipeRight: SwipeProduct.Right
)