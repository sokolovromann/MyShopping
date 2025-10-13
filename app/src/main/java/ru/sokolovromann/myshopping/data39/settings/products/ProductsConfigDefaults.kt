package ru.sokolovromann.myshopping.data39.settings.products

object ProductsConfigDefaults {
    val VIEW_MODE: ProductsViewMode = ProductsViewMode.List
    val SORT: SortProducts = SortProducts(
        name = SortProductsName.DoNotSort,
        params = null
    )
    val GROUP: GroupProducts = GroupProducts.ActiveFirst
    val ADDING_MODE: AddingProductMode = AddingProductMode.Simple
    val CALCULATE_TOTAL: CalculateProductsTotal = CalculateProductsTotal(
        name = CalculateProductsTotalName.AllProducts,
        params = CalculateProductsTotalParams.Short
    )
    val STRIKETHROUGH_COMPLETED: StrikethroughCompletedProducts = StrikethroughCompletedProducts.Off
    val AFTER_COMPLETING: AfterCompletingProduct = AfterCompletingProduct.DoNothing
    val AFTER_TAPPING_BY_CHECKBOX: AfterTappingByProductCheckbox = AfterTappingByProductCheckbox.CompleteOrActiveProduct
    val CHECKBOXES_COLOR: ProductsCheckboxesColor = ProductsCheckboxesColor.RedOrGreen
    val AFTER_TAPPING_BY_ITEM: AfterTappingByProductItem = AfterTappingByProductItem.DoNothing
    val SWIPE_LEFT: SwipeProduct = SwipeProduct.EditProduct
    val SWIPE_RIGHT: SwipeProduct = SwipeProduct.DeleteProduct
}