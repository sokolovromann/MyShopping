package ru.sokolovromann.myshopping.core.domain.model

object UserPreferencesDefaults {

    val General = GeneralPreferences(
        theme = Theme.Default,
        fontSize = FontSize.Medium,
        moneyFormattingMode = MoneyFormattingMode.Simple,
        currency = Currency.Right("")
    )

    val Carts = CartsPreferences(
        view = CartsView.List(CartsProductsDisplayMode.ProductsHorizontally),
        sort = SortCarts.DoNotSort,
        groupByStatus = GroupCartsByStatus.ActiveFirst(displayEmpty = true),
        calculateProductsTotal = CalculateProductsTotal.AllProducts(ProductsTotalCalculatingMode.Short),
        afterAdding = AfterAddingCart.OpenProductsScreen,
        afterCompleting = AfterCompletingCart.DoNothing,
        afterArchiving = AfterArchivingCart.DoNothing,
        swipeLeft = SwipeCart.Left(SwipeCartActionName.ChangeCartStatusToCompletedOrActive),
        swipeRight = SwipeCart.Right(SwipeCartActionName.ArchiveOrUnarchiveCart),
        deletionFromTrash = DeletionCartFromTrash.DoNotDelete
    )

    val Products = ProductsPreferences(
        view = ProductsView.List,
        sort = SortProducts.DoNotSort,
        groupByStatus = GroupProductsByStatus.ActiveFirst,
        addingMode = ProductsAddingMode.Simple,
        calculateTotal = CalculateProductsTotal.AllProducts(ProductsTotalCalculatingMode.Short),
        strikethroughCompleted = StrikethroughCompletedProducts.Off,
        afterCompleting = AfterCompletingProduct.DoNothing,
        swipeLeft = SwipeProduct.Left(SwipeProductActionName.EditProduct),
        swipeRight = SwipeProduct.Right(SwipeProductActionName.DeleteProduct)
    )

    val ProductsWidget = ProductsWidgetPreferences(
        theme = Theme.Default,
        fontSize = FontSize.Medium,
        sortProducts = SortProducts.DoNotSort,
        groupProductsByStatus = GroupProductsByStatus.ActiveFirst
    )

    val AddEditProduct = AddEditProductPreferences(
        lockField = LockProductField.Cost,
        afterTappingByEnter = AfterTappingByProductEnter.GoToNextField,
        afterAdding = AfterAddingProduct.CloseScreen,
        afterEditing = AfterEditingProduct.CloseScreen,
        tax = null
    )

    val Suggestions = SuggestionsPreferences(
        view = SuggestionsView.List(SuggestionsFieldsDisplayMode.All),
        sort = SortSuggestions.ByName(byAscending = true),
        addingMode = SuggestionAddingMode.All
    )

    val Backup = BackupPreferences(
        directory = BackupDirectory("/MyShoppingList")
    )
}