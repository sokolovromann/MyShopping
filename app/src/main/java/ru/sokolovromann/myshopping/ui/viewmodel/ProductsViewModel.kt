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

            is ProductsEvent.CopyProductToShoppingList -> copyProductToShoppingList(event)

            is ProductsEvent.MoveProductToShoppingList -> moveProductToShoppingList(event)

            is ProductsEvent.MoveProductUp -> moveProductUp(event)

            is ProductsEvent.MoveProductDown -> moveProductDown(event)

            ProductsEvent.HideProducts -> hideProducts()

            is ProductsEvent.HideProduct -> hideProduct(event)

            ProductsEvent.ShareProducts -> shareProducts()

            ProductsEvent.SelectProductsSort -> selectProductsSort()

            ProductsEvent.SelectProductsDisplayTotal -> selectProductsDisplayTotal()

            is ProductsEvent.SortProducts -> sortProducts(event)

            is ProductsEvent.DisplayProductsTotal -> displayProductsTotal(event)

            is ProductsEvent.CompleteProduct -> completeProduct(event)

            is ProductsEvent.ActiveProduct -> activeProduct(event)

            ProductsEvent.ShowBackScreen -> showBackScreen()

            is ProductsEvent.ShowProductMenu -> showProductMenu(event)

            ProductsEvent.ShowProductsMenu -> showProductsMenu()

            ProductsEvent.HideProductMenu -> hideProductMenu()

            ProductsEvent.HideProductsMenu -> hideProductsMenu()

            ProductsEvent.HideProductsSort -> hideProductsSort()

            ProductsEvent.HideProductsDisplayTotal -> hideProductsDisplayTotal()

            ProductsEvent.CalculateChange -> calculateChange()
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
            productsState.showNotFound(
                preferences = products.preferences,
                shoppingListName = products.formatName(),
                reminder = products.shoppingList.reminder
            )
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
        hideProductMenu()
    }

    private fun editShoppingListName() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListName(shoppingUid))
        hideProductsMenu()
    }

    private fun editShoppingListReminder() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditShoppingListReminder(shoppingUid))
        hideProductsMenu()
    }

    private fun hideProducts() = viewModelScope.launch {
        repository.hideProducts(
            shoppingUid = shoppingUid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideProductsMenu()
        }
    }

    private fun hideProduct(
        event: ProductsEvent.HideProduct
    ) = viewModelScope.launch {
        repository.hideProduct(
            shoppingUid = shoppingUid,
            productUid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideProductMenu()
        }
    }

    private fun shareProducts() = viewModelScope.launch(dispatchers.main) {
        val shareText = productsState.getShareProductsResult()
            .getOrElse { return@launch }

        _screenEventFlow.emit(ProductsScreenEvent.ShareProducts(shareText))
        hideProductsMenu()
    }

    private fun copyProductToShoppingList(
        event: ProductsEvent.CopyProductToShoppingList
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.CopyProductToShoppingList(event.uid))
        hideProductMenu()
    }

    private fun moveProductToShoppingList(
        event: ProductsEvent.MoveProductToShoppingList
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.MoveProductToShoppingList(event.uid))

        withContext(dispatchers.main) {
            hideProductMenu()
        }
    }

    private fun moveProductUp(
        event: ProductsEvent.MoveProductUp
    ) = viewModelScope.launch(dispatchers.main) {
        val products = productsState.getProductsUpResult(event.uid).getOrElse {
            productsState.hideProductMenu()
            return@launch
        }

        repository.swapProducts(products.first, products.second)
    }

    private fun moveProductDown(
        event: ProductsEvent.MoveProductDown
    ) = viewModelScope.launch(dispatchers.main) {
        val products = productsState.getProductsDownResult(event.uid).getOrElse {
            productsState.hideProductMenu()
            return@launch
        }

        repository.swapProducts(products.first, products.second)
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

    private fun selectProductsDisplayTotal() {
        productsState.showDisplayTotal()
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

    private fun displayProductsTotal(
        event: ProductsEvent.DisplayProductsTotal
    ) = viewModelScope.launch {
        when (event.displayTotal) {
            DisplayTotal.ALL -> repository.displayProductsAllTotal()
            DisplayTotal.COMPLETED -> repository.displayProductsCompletedTotal()
            DisplayTotal.ACTIVE -> repository.displayProductsActiveTotal()
        }

        withContext(dispatchers.main) {
            hideProductsDisplayTotal()
        }
    }

    private fun showProductMenu(event: ProductsEvent.ShowProductMenu) {
        productsState.showProductMenu(event.uid)
    }

    private fun showProductsMenu() {
        productsState.showProductsMenu()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
    }

    private fun hideProductMenu() {
        productsState.hideProductMenu()
    }

    private fun hideProductsMenu() {
        productsState.hideProductsMenu()
    }

    private fun hideProductsSort() {
        productsState.hideSort()
    }

    private fun hideProductsDisplayTotal() {
        productsState.hideDisplayTotal()
    }
}