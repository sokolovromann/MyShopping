package ru.sokolovromann.myshopping.settings.products

data class ProductsConfig(
    val viewMode: ProductsViewMode,
    val sort: SortProducts,
    val group: GroupProducts,
    val addingMode: AddingProductMode,
    val calculateTotal: CalculateProductsTotal,
    val strikethroughCompleted: StrikethroughCompletedProducts,
    val afterCompleting: AfterCompletingProduct,
    val afterTappingByCheckbox: AfterTappingByProductCheckbox,
    val checkboxesColor: ProductsCheckboxesColor,
    val afterTappingByItem: AfterTappingByProductItem,
    val swipeLeft: SwipeProduct,
    val swipeRight: SwipeProduct
)