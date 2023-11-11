package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<ProductsEvent> {

    val productsState: ProductsState = ProductsState()

    private val _screenEventFlow: MutableSharedFlow<ProductsScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ProductsScreenEvent> = _screenEventFlow

    private val shoppingUid: String = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key) ?: ""

    init {
        getProducts()
    }

    override fun onEvent(event: ProductsEvent) {
        when (event) {
            ProductsEvent.AddProduct -> addProduct()

            is ProductsEvent.EditProduct -> editProduct(event)

            ProductsEvent.EditShoppingListName -> editShoppingListName()

            ProductsEvent.EditShoppingListReminder -> editShoppingListReminder()

            ProductsEvent.EditShoppingListTotal -> editShoppingListTotal()

            ProductsEvent.DeleteShoppingListTotal -> deleteShoppingListTotal()

            ProductsEvent.CopyProductsToShoppingList -> copyProductsToShoppingList()

            ProductsEvent.MoveProductsToShoppingList -> moveProductsToShoppingList()

            ProductsEvent.MoveShoppingListToPurchases -> moveShoppingListToPurchases()

            ProductsEvent.MoveShoppingListToArchive -> moveShoppingListToArchive()

            ProductsEvent.MoveShoppingListToTrash -> moveShoppingListToTrash()

            ProductsEvent.CopyShoppingList -> copyShoppingList()

            is ProductsEvent.MoveProductUp -> moveProductUp(event)

            is ProductsEvent.MoveProductDown -> moveProductDown(event)

            ProductsEvent.DeleteProducts -> deleteProducts()

            ProductsEvent.ShareProducts -> shareProducts()

            ProductsEvent.SelectProductsSort -> selectProductsSort()

            ProductsEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is ProductsEvent.SelectProduct -> selectProduct(event)

            ProductsEvent.SelectAllProducts -> selectAllProducts()

            is ProductsEvent.UnselectProduct -> unselectProduct(event)

            ProductsEvent.CancelSelectingProducts -> cancelSelectingProducts()

            is ProductsEvent.SortProducts -> sortProducts(event)

            ProductsEvent.ReverseSortProducts -> reverseSortProducts()

            is ProductsEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            ProductsEvent.DisplayHiddenProducts -> displayHiddenProducts()

            is ProductsEvent.CompleteProduct -> completeProduct(event)

            is ProductsEvent.ActiveProduct -> activeProduct(event)

            ProductsEvent.ShowBackScreen -> showBackScreen()

            ProductsEvent.ShowProductsMenu -> showProductsMenu()

            ProductsEvent.ShowSelectedMenu -> showSelectedMenu()

            ProductsEvent.ShowShoppingListMenu -> showShoppingListMenu()

            ProductsEvent.HideProductsMenu -> hideProductsMenu()

            ProductsEvent.HideSelectedMenu -> hideSelectedMenu()

            ProductsEvent.HideShoppingListMenu -> hideShoppingListMenu()

            ProductsEvent.HideProductsSort -> hideProductsSort()

            ProductsEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

            ProductsEvent.CalculateChange -> calculateChange()

            ProductsEvent.InvertProductsMultiColumns -> invertProductsMultiColumns()

            ProductsEvent.InvertAutomaticSorting -> invertAutomaticSorting()

            ProductsEvent.PinProducts -> pinProducts()

            ProductsEvent.UnpinProducts -> unpinProducts()
        }
    }

    private fun getProducts() = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            productsState.showLoading()
        }

        shoppingListsRepository.getShoppingListWithConfig(shoppingUid).collect {
            shoppingListLoaded(it)
        }
    }

    private suspend fun shoppingListLoaded(
        shoppingListWithConfig: ShoppingListWithConfig
    ) = withContext(AppDispatchers.Main) {
        if (shoppingListWithConfig.isProductsEmpty()) {
            productsState.showNotFound(shoppingListWithConfig)
        } else {
            productsState.showProducts(shoppingListWithConfig)
        }
    }

    private suspend fun updateProductsWidget() = withContext(AppDispatchers.Main) {
        val uid = productsState.getShoppingListUid()
        val event = ProductsScreenEvent.UpdateProductsWidget(uid)
        _screenEventFlow.emit(event)
    }

    private fun addProduct() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.AddProduct(shoppingUid))
    }

    private fun editProduct(
        event: ProductsEvent.EditProduct
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditProduct(shoppingUid, event.uid))
        unselectAllProducts()
    }

    private fun editShoppingListName() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListName(shoppingUid))
        hideProductsMenu()
    }

    private fun editShoppingListReminder() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListReminder(shoppingUid))
        hideProductsMenu()
    }

    private fun editShoppingListTotal() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListTotal(shoppingUid))
        hideDisplayPurchasesTotal()
    }

    private fun deleteShoppingListTotal() = viewModelScope.launch {
        shoppingListsRepository.deleteShoppingListTotal(shoppingUid)

        withContext(AppDispatchers.Main) {
            hideDisplayPurchasesTotal()
        }
    }

    private fun moveProductUp(event: ProductsEvent.MoveProductUp) = viewModelScope.launch {
        shoppingListsRepository.moveProductUp(
            shoppingUid = shoppingUid,
            productUid = event.uid
        ).onSuccess {
            productsState.selectProduct(event.uid)
            updateProductsWidget()
        }
    }

    private fun moveProductDown(event: ProductsEvent.MoveProductDown) = viewModelScope.launch {
        shoppingListsRepository.moveProductDown(
            shoppingUid = shoppingUid,
            productUid = event.uid
        ).onSuccess {
            productsState.selectProduct(event.uid)
            updateProductsWidget()
        }
    }

    private fun deleteProducts() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            shoppingListsRepository.deleteProductsByProductUids(
                productsUids = it,
                shoppingUid = shoppingUid
            )

            updateProductsWidget()
        }

        withContext(AppDispatchers.Main) {
            unselectAllProducts()
        }
    }

    private fun shareProducts() = viewModelScope.launch(AppDispatchers.Main) {
        val shareText = productsState.getShareProductsResult()
            .getOrElse { return@launch }

        _screenEventFlow.emit(ProductsScreenEvent.ShareProducts(shareText))
        hideProductsMenu()
    }

    private fun copyProductsToShoppingList() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            val uids = uidsToString(it)
            _screenEventFlow.emit(ProductsScreenEvent.CopyProductToShoppingList(uids))
        }

        withContext(AppDispatchers.Main) {
            hideSelectedMenu()
            unselectAllProducts()
        }
    }

    private fun moveProductsToShoppingList() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            val uids = uidsToString(it)
            _screenEventFlow.emit(ProductsScreenEvent.MoveProductToShoppingList(uids))
        }

        withContext(AppDispatchers.Main) {
            hideSelectedMenu()
            unselectAllProducts()
        }
    }

    private fun moveShoppingListToPurchases() = viewModelScope.launch {
        shoppingListsRepository.moveShoppingListToPurchases(productsState.getShoppingListUid())

        withContext(AppDispatchers.Main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun moveShoppingListToArchive() = viewModelScope.launch {
        shoppingListsRepository.moveShoppingListToArchive(productsState.getShoppingListUid())

        withContext(AppDispatchers.Main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun moveShoppingListToTrash() = viewModelScope.launch {
        shoppingListsRepository.moveShoppingListToTrash(productsState.getShoppingListUid())

        withContext(AppDispatchers.Main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun copyShoppingList() = viewModelScope.launch {
        shoppingListsRepository.copyShoppingList(shoppingUid)

        withContext(AppDispatchers.Main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun completeProduct(
        event: ProductsEvent.CompleteProduct
    ) = viewModelScope.launch {
        shoppingListsRepository.completeProduct(event.uid)

        if (productsState.isEditProductAfterCompleted()) {
            withContext(AppDispatchers.Main) {
                _screenEventFlow.emit(ProductsScreenEvent.EditProduct(shoppingUid, event.uid))
            }
        }
    }

    private fun activeProduct(
        event: ProductsEvent.ActiveProduct
    ) = viewModelScope.launch {
        shoppingListsRepository.activeProduct(event.uid)
    }

    private fun calculateChange() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.CalculateChange(shoppingUid))
        hideProductsMenu()
    }

    private fun selectProductsSort() {
        productsState.showSort()
    }

    private fun selectDisplayPurchasesTotal() {
        productsState.selectDisplayPurchasesTotal()
    }

    private fun selectProduct(event: ProductsEvent.SelectProduct) {
        productsState.selectProduct(event.uid)
    }

    private fun selectAllProducts() {
        productsState.selectAllProducts()
    }

    private fun unselectProduct(event: ProductsEvent.UnselectProduct) {
        productsState.unselectProduct(event.uid)
    }

    private fun unselectAllProducts() {
        productsState.unselectAllProducts()
    }

    private fun cancelSelectingProducts() {
        productsState.unselectAllProducts()
    }

    private fun sortProducts(event: ProductsEvent.SortProducts) = viewModelScope.launch {
        shoppingListsRepository.sortProducts(
            shoppingUid = shoppingUid,
            sort = Sort(event.sortBy),
            automaticSort = productsState.screenData.automaticSorting
        )

        withContext(AppDispatchers.Main) {
            hideProductsSort()
        }
    }

    private fun reverseSortProducts() = viewModelScope.launch {
        shoppingListsRepository.reverseProducts(
            shoppingUid = shoppingUid,
            automaticSort = productsState.screenData.automaticSorting
        )

        withContext(AppDispatchers.Main) {
            hideProductsSort()
        }
    }

    private fun displayPurchasesTotal(
        event: ProductsEvent.DisplayPurchasesTotal
    ) = viewModelScope.launch {
        appConfigRepository.displayTotal(event.displayTotal)

        withContext(AppDispatchers.Main) {
            hideDisplayPurchasesTotal()
        }

        updateProductsWidget()
    }

    private fun displayHiddenProducts() {
        productsState.displayHiddenProducts()
    }

    private fun showProductsMenu() {
        productsState.showProductsMenu()
    }

    private fun showSelectedMenu() {
        productsState.showSelectedMenu()
    }

    private fun showShoppingListMenu() {
        productsState.showShoppingMenu()
    }

    private fun showBackScreen() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
    }

    private fun hideProductsMenu() {
        productsState.hideProductsMenu()
    }

    private fun hideSelectedMenu() {
        productsState.hideSelectedMenu()
    }

    private fun hideShoppingListMenu() {
        productsState.hideShoppingMenu()
    }

    private fun hideProductsSort() {
        productsState.hideSort()
    }

    private fun hideDisplayPurchasesTotal() {
        productsState.hideDisplayPurchasesTotal()
    }

    private fun invertProductsMultiColumns() = viewModelScope.launch {
        appConfigRepository.invertProductsMultiColumns()
    }

    private fun invertAutomaticSorting() = viewModelScope.launch {
        shoppingListsRepository.sortProducts(
            shoppingUid = shoppingUid,
            sort = productsState.screenData.sort,
            automaticSort = !productsState.screenData.automaticSorting
        )
    }

    private fun pinProducts() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            shoppingListsRepository.pinProducts(it)
            updateProductsWidget()
        }

        withContext(AppDispatchers.Main) {
            unselectAllProducts()
        }
    }

    private fun unpinProducts() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            shoppingListsRepository.unpinProducts(it)
            updateProductsWidget()
        }

        withContext(AppDispatchers.Main) {
            unselectAllProducts()
        }
    }

    private fun uidsToString(uids: List<String>): String {
        return uids.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "")
    }
}