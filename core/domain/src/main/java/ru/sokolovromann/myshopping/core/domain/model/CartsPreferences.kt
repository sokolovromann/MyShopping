package ru.sokolovromann.myshopping.core.domain.model

data class CartsPreferences(
    val view: CartsView,
    val sort: SortCarts,
    val groupByStatus: GroupCartsByStatus,
    val calculateProductsTotal: CalculateProductsTotal,
    val afterAdding: AfterAddingCart,
    val afterCompleting: AfterCompletingCart,
    val afterArchiving: AfterArchivingCart,
    val afterTappingByCheckbox: AfterTappingByCartCheckbox,
    val checkboxColor: CheckboxColor,
    val swipeLeft: SwipeCart.Left,
    val swipeRight: SwipeCart.Right,
    val deletionFromTrash: DeletionCartFromTrash
)