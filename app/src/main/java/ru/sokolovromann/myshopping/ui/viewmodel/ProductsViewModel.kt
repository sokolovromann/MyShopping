package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.ProductsRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val dispatchers: AppDispatchers,
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

            ProductsEvent.CopyProductsToShoppingList -> copyProductsToShoppingList()

            ProductsEvent.MoveProductsToShoppingList -> moveProductsToShoppingList()

            ProductsEvent.MoveShoppingListToPurchases -> moveShoppingListToPurchases()

            ProductsEvent.MoveShoppingListToArchive -> moveShoppingListToArchive()

            ProductsEvent.MoveShoppingListToTrash -> moveShoppingListToTrash()

            ProductsEvent.DeleteProducts -> deleteProducts()

            ProductsEvent.ShareProducts -> shareProducts()

            ProductsEvent.SelectProductsSort -> selectProductsSort()

            ProductsEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is ProductsEvent.SelectProduct -> selectProduct(event)

            ProductsEvent.SelectSelectProducts -> selectSelectProducts()

            ProductsEvent.SelectAllProducts -> selectAllProducts()

            ProductsEvent.SelectCompletedProducts -> selectCompletedProducts()

            ProductsEvent.SelectActiveProducts -> selectActiveProducts()

            is ProductsEvent.UnselectProduct -> unselectProduct(event)

            ProductsEvent.CancelSelectingProducts -> cancelSelectingProducts()

            is ProductsEvent.SortProducts -> sortProducts(event)

            is ProductsEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            ProductsEvent.DisplayHiddenProducts -> displayHiddenProducts()

            is ProductsEvent.CompleteProduct -> completeProduct(event)

            is ProductsEvent.ActiveProduct -> activeProduct(event)

            ProductsEvent.ShowBackScreen -> showBackScreen()

            ProductsEvent.ShowProductsMenu -> showProductsMenu()

            ProductsEvent.HideProductsMenu -> hideProductsMenu()

            ProductsEvent.HideProductsSort -> hideProductsSort()

            ProductsEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

            ProductsEvent.HideSelectProducts -> hideSelectProducts()

            ProductsEvent.CalculateChange -> calculateChange()

            else -> {}
        }
    }

    private fun getProducts() = viewModelScope.launch {
        withContext(dispatchers.main) {
            productsState.showLoading()
        }

        repository.getProducts(shoppingUid).collect {
            productsLoaded(it)
        }
    }

    private suspend fun productsLoaded(products: Products?) = withContext(dispatchers.main) {
        if (products == null) {
            return@withContext
        }

        if (products.shoppingList.products.isEmpty()) {
            productsState.showNotFound(products)
        } else {
            productsState.showProducts(products)
        }
    }

    private fun addProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.AddProduct(shoppingUid))
    }

    private fun editProduct(
        event: ProductsEvent.EditProduct
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditProduct(shoppingUid, event.uid))

        withContext(dispatchers.main) {
            unselectAllProducts()
        }
    }

    private fun editShoppingListName() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListName(shoppingUid))
        hideProductsMenu()
    }

    private fun editShoppingListReminder() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListReminder(shoppingUid))
        hideProductsMenu()
    }

    private fun deleteProducts() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            repository.deleteProducts(
                uids = it,
                shoppingUid = shoppingUid,
                lastModified = System.currentTimeMillis()
            )
        }

        withContext(dispatchers.main) {
            unselectAllProducts()
        }
    }

    private fun shareProducts() = viewModelScope.launch(dispatchers.main) {
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

        withContext(dispatchers.main) {
            unselectAllProducts()
        }
    }

    private fun moveProductsToShoppingList() = viewModelScope.launch {
        productsState.screenData.selectedUids?.let {
            val uids = uidsToString(it)
            _screenEventFlow.emit(ProductsScreenEvent.MoveProductToShoppingList(uids))
        }

        withContext(dispatchers.main) {
            unselectAllProducts()
        }
    }

    private fun moveShoppingListToPurchases() = viewModelScope.launch {
        repository.moveShoppingListToPurchases(
            uid = productsState.shoppingListUid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun moveShoppingListToArchive() = viewModelScope.launch {
        repository.moveShoppingListToArchive(
            uid = productsState.shoppingListUid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun moveShoppingListToTrash() = viewModelScope.launch {
        repository.moveShoppingListToTrash(
            uid = productsState.shoppingListUid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
        }
    }

    private fun completeProduct(
        event: ProductsEvent.CompleteProduct
    ) = viewModelScope.launch {
        repository.completeProduct(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        if (productsState.editCompleted) {
            withContext(dispatchers.main) {
                _screenEventFlow.emit(ProductsScreenEvent.EditProduct(shoppingUid, event.uid))
            }
        }
    }

    private fun activeProduct(
        event: ProductsEvent.ActiveProduct
    ) = viewModelScope.launch {
        repository.activeProduct(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )
    }

    private fun calculateChange() = viewModelScope.launch(dispatchers.main) {
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

    private fun selectSelectProducts() {
        productsState.showSelectingMenu()
    }

    private fun selectAllProducts() {
        productsState.selectAllProducts()
    }

    private fun selectCompletedProducts() {
        productsState.selectCompletedProducts()
    }

    private fun selectActiveProducts() {
        productsState.selectActiveProducts()
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
        val products = productsState.sortProductsResult(event.sortBy).getOrElse {
            withContext(dispatchers.main) { hideProductsSort() }
            return@launch
        }

        repository.swapProducts(products)

        withContext(dispatchers.main) {
            hideProductsSort()
        }
    }

    private fun displayPurchasesTotal(
        event: ProductsEvent.DisplayPurchasesTotal
    ) = viewModelScope.launch {
        when (event.displayTotal) {
            DisplayTotal.ALL -> repository.displayAllPurchasesTotal()
            DisplayTotal.COMPLETED -> repository.displayCompletedPurchasesTotal()
            DisplayTotal.ACTIVE -> repository.displayActivePurchasesTotal()
        }

        withContext(dispatchers.main) {
            hideDisplayPurchasesTotal()
        }
    }

    private fun displayHiddenProducts() {
        productsState.displayHiddenProducts()
    }

    private fun showProductsMenu() {
        productsState.showProductsMenu()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
    }

    private fun hideProductsMenu() {
        productsState.hideProductsMenu()
    }

    private fun hideProductsSort() {
        productsState.hideSort()
    }

    private fun hideSelectProducts() {
        productsState.hideSelectingMenu()
    }

    private fun hideDisplayPurchasesTotal() {
        productsState.hideDisplayPurchasesTotal()
    }

    private fun uidsToString(uids: List<String>): String {
        return uids.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "")
    }
}