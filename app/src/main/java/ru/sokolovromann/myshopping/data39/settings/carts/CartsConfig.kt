package ru.sokolovromann.myshopping.data39.settings.carts

data class CartsConfig(
    val viewMode: CartsViewMode,
    val sort: SortCarts,
    val group: GroupCarts,
    val calculateTotal: CalculateCartsTotal,
    val afterAdding: AfterAddingCart,
    val afterCompleting: AfterCompletingCart,
    val afterArchiving: AfterArchivingCart,
    val afterTappingByCheckbox: AfterTappingByCartCheckbox,
    val checkboxesColor: CartsCheckboxesColor,
    val swipeLeft: SwipeCart,
    val swipeRight: SwipeCart,
    val emptyCarts: EmptyCarts,
    val deletionFromTrash: DeletionCartsFromTrash
)