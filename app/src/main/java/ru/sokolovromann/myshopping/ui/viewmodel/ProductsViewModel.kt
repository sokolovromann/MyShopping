package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<ProductsEvent> {

    val productsState: ProductsState = ProductsState()

    private val _addIconState: MutableState<IconData> = mutableStateOf(IconData())
    val addIconState: State<IconData> = _addIconState

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _bottomBarState: MutableState<BottomBarData> = mutableStateOf(BottomBarData())
    val bottomBarState: State<BottomBarData> = _bottomBarState

    private val _screenEventFlow: MutableSharedFlow<ProductsScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ProductsScreenEvent> = _screenEventFlow

    private val shoppingUid: String = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key) ?: ""

    init {
        showBottomBar()
        showAddIcon()
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

            ProductsEvent.DeleteProducts -> deleteProducts()

            is ProductsEvent.DeleteProduct -> deleteProduct(event)

            ProductsEvent.ShareProducts -> shareProducts()

            ProductsEvent.SelectProductsSort -> selectProductsSort()

            ProductsEvent.SelectProductsDisplayCompleted -> selectProductsDisplayCompleted()

            ProductsEvent.SelectProductsDisplayTotal -> selectProductsDisplayTotal()

            ProductsEvent.SortProductsByCreated -> sortProductsByCreated()

            ProductsEvent.SortProductsByLastModified -> sortProductsByLastModified()

            ProductsEvent.SortProductsByName -> sortProductsByName()

            ProductsEvent.SortProductsByTotal -> sortProductsByTotal()

            ProductsEvent.DisplayProductsCompletedFirst -> displayProductsCompletedFirst()

            ProductsEvent.DisplayProductsCompletedLast -> displayProductsCompletedLast()

            ProductsEvent.DisplayProductsAllTotal -> displayProductsAllTotal()

            ProductsEvent.DisplayProductsCompletedTotal -> displayProductsCompletedTotal()

            ProductsEvent.DisplayProductsActiveTotal -> displayProductsActiveTotal()

            ProductsEvent.InvertProductsSort -> invertProductsSort()

            is ProductsEvent.CompleteProduct -> completeProduct(event)

            is ProductsEvent.ActiveProduct -> activeProduct(event)

            ProductsEvent.ShowBackScreen -> showBackScreen()

            is ProductsEvent.ShowProductMenu -> showProductMenu(event)

            ProductsEvent.ShowProductsMenu -> showProductsMenu()

            ProductsEvent.HideProductsCompleted -> hideProductsCompleted()

            ProductsEvent.HideProductMenu -> hideProductMenu()

            ProductsEvent.HideProductsMenu -> hideProductsMenu()

            ProductsEvent.HideProductsSort -> hideProductsSort()

            ProductsEvent.HideProductsDisplayCompleted -> hideProductsDisplayCompleted()

            ProductsEvent.HideProductsDisplayTotal -> hideProductsDisplayTotal()

            ProductsEvent.CalculateChange -> calculateChange()
        }
    }

    private fun getProducts() = viewModelScope.launch(dispatchers.io) {
        showProductsLoading()

        repository.getProducts(shoppingUid).collect {
            showProducts(it)
        }
    }

    private fun addProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.AddProduct(shoppingUid))
    }

    private fun editProduct(
        event: ProductsEvent.EditProduct
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.EditProduct(event.uid))
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

    private fun deleteProducts() = viewModelScope.launch(dispatchers.io) {
        repository.deleteProducts(
            shoppingUid = shoppingUid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideProductsMenu()
        }
    }

    private fun deleteProduct(
        event: ProductsEvent.DeleteProduct
    ) = viewModelScope.launch(dispatchers.io) {
        repository.deleteProduct(
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

    private fun completeProduct(
        event: ProductsEvent.CompleteProduct
    ) = viewModelScope.launch(dispatchers.io) {
        repository.completeProduct(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        if (productsState.editCompleted) {
            withContext(dispatchers.main) {
                _screenEventFlow.emit(ProductsScreenEvent.EditProduct(event.uid))
            }
        }
    }

    private fun activeProduct(
        event: ProductsEvent.ActiveProduct
    ) = viewModelScope.launch(dispatchers.io) {
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

    private fun selectProductsDisplayCompleted() {
        productsState.showDisplayCompleted()
    }

    private fun selectProductsDisplayTotal() {
        productsState.showDisplayTotal()
    }

    private fun sortProductsByCreated() = viewModelScope.launch(dispatchers.io) {
        repository.sortProductsByCreated()

        withContext(dispatchers.main) {
            hideProductsSort()
        }
    }

    private fun sortProductsByLastModified() = viewModelScope.launch(dispatchers.io) {
        repository.sortProductsByLastModified()

        withContext(dispatchers.main) {
            hideProductsSort()
        }
    }

    private fun sortProductsByName() = viewModelScope.launch(dispatchers.io) {
        repository.sortProductsByName()

        withContext(dispatchers.main) {
            hideProductsSort()
        }
    }

    private fun sortProductsByTotal() = viewModelScope.launch(dispatchers.io) {
        repository.sortProductsByTotal()

        withContext(dispatchers.main) {
            hideProductsSort()
        }
    }

    private fun displayProductsCompletedFirst() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductsCompletedFirst()

        withContext(dispatchers.main) {
            hideProductsDisplayCompleted()
        }
    }

    private fun displayProductsCompletedLast() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductsCompletedLast()

        withContext(dispatchers.main) {
            hideProductsDisplayCompleted()
        }
    }

    private fun displayProductsAllTotal() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductsAllTotal()

        withContext(dispatchers.main) {
            hideProductsDisplayTotal()
        }
    }

    private fun displayProductsCompletedTotal() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductsCompletedTotal()

        withContext(dispatchers.main) {
            hideProductsDisplayTotal()
        }
    }

    private fun displayProductsActiveTotal() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductsActiveTotal()

        withContext(dispatchers.main) {
            hideProductsDisplayTotal()
        }
    }

    private fun invertProductsSort() = viewModelScope.launch(dispatchers.io) {
        repository.invertProductsSort()
    }

    private suspend fun showProductsLoading() = withContext(dispatchers.main) {
        productsState.showLoading()
    }

    private suspend fun showProducts(products: Products?) = withContext(dispatchers.main) {
        if (products == null) {
            return@withContext
        }

        if (products.shoppingList.productsEmpty) {
            productsState.showNotFound(
                preferences = products.preferences,
                shoppingListName = products.formatName(),
                reminder = products.shoppingList.reminder
            )
        } else {
            productsState.showProducts(products)
        }

        showTopBar(products.formatName())
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

    private fun showTopBar(shoppingListName: String) {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toUiText(shoppingListName),
                fontSize = FontSize.MEDIUM
            ),
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.ArrowBack)
            )
        )
        _topBarState.value = data
    }

    private fun showBottomBar() {
        val data = BottomBarData()
        _bottomBarState.value = data
    }

    private fun showAddIcon() {
        val data = IconData(
            icon = mapping.toUiIcon(Icons.Default.Add)
        )
        _addIconState.value = data
    }

    private fun hideProductsCompleted() = viewModelScope.launch(dispatchers.io) {
        repository.hideProductsCompleted()

        withContext(dispatchers.main) {
            hideProductsDisplayCompleted()
        }
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

    private fun hideProductsDisplayCompleted() {
        productsState.hideDisplayCompleted()
    }

    private fun hideProductsDisplayTotal() {
        productsState.hideDisplayTotal()
    }
}