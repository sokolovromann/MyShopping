package ru.sokolovromann.myshopping.data39.settings.carts

object CartsConfigDefaults {
    val VIEW_MODE: CartsViewMode = CartsViewMode(
        name = CartsViewModeName.List,
        params = CartsViewModeParams.ProductHorizontally
    )
    val SORT: SortCarts = SortCarts(
        name = SortCartsName.DoNotSort,
        params = null
    )
    val GROUP: GroupCarts = GroupCarts.ActiveFirst
    val CALCULATE_TOTAL: CalculateCartsTotal = CalculateCartsTotal.AllProducts
    val AFTER_ADDING: AfterAddingCart = AfterAddingCart.OpenProductsScreen
    val AFTER_COMPLETING: AfterCompletingCart = AfterCompletingCart.DoNothing
    val AFTER_ARCHIVING: AfterArchivingCart = AfterArchivingCart.DoNothing
    val AFTER_TAPPING_BY_CHECKBOX: AfterTappingByCartCheckbox = AfterTappingByCartCheckbox.DoNothing
    val CHECKBOXES_COLOR: CartsCheckboxesColor = CartsCheckboxesColor.RedOrGreen
    val SWIPE_LEFT: SwipeCart = SwipeCart.CompleteOrActiveCart
    val SWIPE_RIGHT: SwipeCart = SwipeCart.ArchiveOrUnarchiveCart
    val EMPTY_CARTS: EmptyCarts = EmptyCarts.Show
    val DELETION_FROM_TRASH: DeletionCartsFromTrash = DeletionCartsFromTrash.After7Days
}