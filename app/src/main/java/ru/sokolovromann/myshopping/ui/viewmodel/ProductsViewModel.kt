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
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.ProductsRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.getDisplayDateAndTime
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsEvent
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<ProductsEvent> {

    val productsState: ListState<ProductItem> = ListState()

    val itemMenuState: ItemMenuState<ProductsItemMenu> = ItemMenuState()

    val productsMenu: MenuIconButtonState<ProductsMenu> = MenuIconButtonState()

    val sortState: MenuButtonState<ProductsSortMenu> = MenuButtonState()

    private val _sortAscendingState: MutableState<IconData> = mutableStateOf(IconData())
    val sortAscendingState: State<IconData> = _sortAscendingState

    val totalState: MenuButtonState<ProductsTotalMenu> = MenuButtonState()

    val completedState: MenuIconButtonState<ProductsCompletedMenu> = MenuIconButtonState()

    private val _addIconState: MutableState<IconData> = mutableStateOf(IconData())
    val addIconState: State<IconData> = _addIconState

    private val _reminderState: MutableState<ItemReminderData> = mutableStateOf(ItemReminderData())
    val reminderState: State<ItemReminderData> = _reminderState

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _bottomBarState: MutableState<BottomBarData> = mutableStateOf(BottomBarData())
    val bottomBarState: State<BottomBarData> = _bottomBarState

    private val _screenEventFlow: MutableSharedFlow<ProductsScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ProductsScreenEvent> = _screenEventFlow

    private var editCompletedProduct: Boolean
        get() { return savedStateHandle.get<Boolean>("editCompletedProduct") ?: false }
        set(value) { savedStateHandle["editCompletedProduct"] = value }

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
        val products = productsState.currentData.items

        val shoppingName = topBarState.value.title.text
        var shareText: String = if (shoppingName == UiText.Nothing) {
            ""
        } else {
            "${mapping.toString(shoppingName)}:\n"
        }

        products.forEach { product ->
            if (!product.completed.checked) {
                shareText += "- ${mapping.toString(product.title.text)} • " +
                        "${mapping.toString(product.body.text)}\n"
            }
        }

        val total = mapping.toString(totalState.currentData.text.text)
        if (total.isEmpty()) {
            if (shareText.isNotEmpty()) {
                shareText = shareText.dropLast(1)
            }
        } else {
            shareText += "\n$total"
        }

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

        if (editCompletedProduct) {
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
        sortState.showMenu()
    }

    private fun selectProductsDisplayCompleted() {
        completedState.showMenu()
    }

    private fun selectProductsDisplayTotal() {
        totalState.showMenu()
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

        val items = products.sortProducts()
        val preferences = products.preferences

        if (items.isEmpty()) {
            showProductNotFound(preferences)
        } else {
            val list = items.map { mapping.toProductItem(it, preferences) }
            productsState.showList(list, preferences.multiColumns)
        }

        showReminder(
            reminder = products.shoppingList.reminder,
            preferences = preferences
        )
        showSort(preferences)
        showSortAscending(preferences)

        showCompleted(preferences)

        if (preferences.displayMoney) {
            showTotal(products.calculateTotal(), preferences)
        } else {
            hideTotal()
        }

        showTopBar(products.formatName())

        itemMenuState.setMenu(mapping.toProductsItemMenu(preferences.fontSize))
        productsMenu.showButton(
            icon = mapping.toMenuIconBody(),
            menu = mapping.toProductsMenu(preferences.fontSize)
        )
    }

    private fun showReminder(reminder: Long?, preferences: ProductPreferences) {
        if (reminder == null) {
            return
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = reminder
        }

        _reminderState.value = ItemReminderData(
            title = mapping.toTitle(
                text = mapping.toResourcesUiText(R.string.products_reminder),
                fontSize = preferences.fontSize
            ),
            body = mapping.toBody(
                text = calendar.getDisplayDateAndTime(),
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showSort(preferences: ProductPreferences) {
        sortState.showButton(
            text = mapping.toShoppingListsSortBody(
                sortBy = preferences.sort.sortBy,
                fontSize = preferences.fontSize
            ),
            menu = mapping.toProductsSortMenu(
                sortBy = preferences.sort.sortBy,
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showSortAscending(preferences: ProductPreferences) {
        _sortAscendingState.value = mapping.toSortAscendingIconBody(
            ascending = preferences.sort.ascending,
        )
    }

    private fun showProductNotFound(preferences: ProductPreferences) {
        val data = mapping.toTitle(
            text = UiText.FromResources(R.string.products_productsNotFound),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnBackground
        )
        productsState.showNotFound(data)
    }

    private fun showProductMenu(event: ProductsEvent.ShowProductMenu) {
        itemMenuState.showMenu(itemUid = event.uid)
    }

    private fun showProductsMenu() {
        productsMenu.showMenu()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ProductsScreenEvent.ShowBackScreen)
    }

    private fun showCompleted(preferences: ProductPreferences) {
        completedState.showButton(
            icon = mapping.toDisplayCompletedIconBody(),
            menu = mapping.toProductsCompletedMenu(
                displayCompleted = preferences.displayCompleted,
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showTotal(total: Money, preferences: ProductPreferences) {
        totalState.showButton(
            text = mapping.toProductsTotalTitle(
                total = total,
                displayTotal = preferences.displayTotal,
                fontSize = preferences.fontSize
            ),
            menu = mapping.toProductsTotalMenu(
                displayTotal = preferences.displayTotal,
                fontSize = preferences.fontSize
            )
        )
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
        itemMenuState.hideMenu()
    }

    private fun hideProductsMenu() {
        productsMenu.hideMenu()
    }

    private fun hideProductsSort() {
        sortState.hideMenu()
    }

    private fun hideProductsDisplayCompleted() {
        completedState.hideMenu()
    }

    private fun hideProductsDisplayTotal() {
        totalState.hideMenu()
    }

    private fun hideTotal() {
        totalState.hideButton()
    }
}