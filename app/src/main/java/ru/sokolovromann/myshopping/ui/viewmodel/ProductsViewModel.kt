package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.ProductsScreenEvent
import ru.sokolovromann.myshopping.ui.model.ProductsState
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

    init { onInit() }

    override fun onEvent(event: ProductsEvent) {
        when (event) {
            is ProductsEvent.OnClickProduct -> onClickProduct(event)

            is ProductsEvent.OnClickAddProduct -> onClickAddProduct()

            is ProductsEvent.OnClickEditProduct -> onClickEditProduct(event)

            ProductsEvent.OnClickBack -> onClickBack()

            ProductsEvent.OnClickEditName -> onClickEditName()

            ProductsEvent.OnClickEditReminder -> onClickEditReminder()

            ProductsEvent.OnClickEditTotal -> onClickEditTotal()

            ProductsEvent.OnClickDeleteTotal -> onClickDeleteTotal()

            ProductsEvent.OnClickPinProducts -> onClickPinProducts()

            ProductsEvent.OnClickCopyProducts -> onClickCopyProducts()

            ProductsEvent.OnClickMoveProducts -> onClickMoveProducts()

            ProductsEvent.OnClickCopyShoppingList -> onClickCopyShoppingList()

            is ProductsEvent.OnClickMoveProductUp -> onClickMoveProductUp(event)

            is ProductsEvent.OnClickMoveProductDown -> onClickMoveProductDown(event)

            ProductsEvent.OnClickDeleteProducts -> onClickDeleteProducts()

            ProductsEvent.OnClickShareProducts -> onClickShareProducts()

            ProductsEvent.OnClickCalculateChange -> onClickCalculateChange()

            ProductsEvent.OnClickSearchProducts -> onClickSearchProducts()

            is ProductsEvent.OnSearchValueChanged -> onSearchValueChanged(event)

            ProductsEvent.OnInvertSearch -> onInvertSearch()

            is ProductsEvent.OnMoveShoppingListSelected -> onMoveShoppingListSelected(event)

            is ProductsEvent.OnDisplayTotalSelected -> onDisplayTotalSelected(event)

            is ProductsEvent.OnSelectDisplayTotal -> onSelectDisplayTotal(event)

            ProductsEvent.OnInvertDisplayLongTotal -> onInvertDisplayLongTotal()

            is ProductsEvent.OnSortSelected -> onSortSelected(event)

            ProductsEvent.OnReverseSort -> onReverseSort()

            is ProductsEvent.OnSelectSort -> onSelectSort(event)

            ProductsEvent.OnInvertSortFormatted -> onInvertSortFormatted()

            is ProductsEvent.OnShowProductsMenu -> onShowProductsMenu(event)

            is ProductsEvent.OnShowItemMoreMenu -> onShowItemMoreMenu(event)

            is ProductsEvent.OnShowShoppingMenu -> onShowShoppingMenu(event)

            is ProductsEvent.OnAllProductsSelected -> onAllProductsSelected(event)

            is ProductsEvent.OnProductSelected -> onProductSelected(event)

            is ProductsEvent.OnShowHiddenProducts -> onShowHiddenProducts(event)

            ProductsEvent.OnInvertMultiColumns -> onInvertMultiColumns()
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onWaiting()

        shoppingListsRepository.getShoppingListWithConfig(shoppingUid).collect {
            productsState.populate(it)
        }
    }

    private fun onClickProduct(
        event: ProductsEvent.OnClickProduct
    ) = viewModelScope.launch(AppDispatchers.Main) {
        if (event.completed) {
            shoppingListsRepository.activeProduct(event.productUid)
        } else {
            shoppingListsRepository.completeProduct(event.productUid)
            if (productsState.isEditProductAfterCompleted()) {
                _screenEventFlow.emit(ProductsScreenEvent.OnShowEditProductScreen(shoppingUid, event.productUid))
            }
        }
    }

    private fun onClickAddProduct() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowAddProductScreen(shoppingUid))
    }

    private fun onClickEditProduct(
        event: ProductsEvent.OnClickEditProduct
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowEditProductScreen(shoppingUid, event.productUid))
        productsState.onAllProductsSelected(selected = false)
    }

    private fun onClickBack() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowBackScreen)
    }

    private fun onClickEditName() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowEditNameScreen(shoppingUid))
        productsState.onShowProductsMenu(expanded = false)
    }

    private fun onClickEditReminder() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowEditReminderScreen(shoppingUid))
        productsState.onShowProductsMenu(expanded = false)
    }

    private fun onClickEditTotal() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowEditTotalScreen(shoppingUid))
        productsState.onSelectDisplayTotal(expanded = false)
    }

    private fun onClickDeleteTotal() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.deleteShoppingListTotal(shoppingUid)
        productsState.onSelectDisplayTotal(expanded = false)
    }

    private fun onClickPinProducts() = viewModelScope.launch(AppDispatchers.Main) {
        productsState.selectedUids?.let {
            if (productsState.isOnlyPinned()) {
                shoppingListsRepository.unpinProducts(it)
            } else {
                shoppingListsRepository.pinProducts(it)
            }

            productsState.onAllProductsSelected(selected = false)
            _screenEventFlow.emit(ProductsScreenEvent.OnUpdateProductsWidget(shoppingUid))
        }
    }

    private fun onClickCopyProducts() = viewModelScope.launch(AppDispatchers.Main) {
        productsState.selectedUids?.let {
            val uids = uidsToString(it)
            _screenEventFlow.emit(ProductsScreenEvent.OnShowCopyProductsScreen(uids))

            productsState.onAllProductsSelected(selected = false)
        }
    }

    private fun onClickMoveProducts() = viewModelScope.launch(AppDispatchers.Main) {
        productsState.selectedUids?.let {
            val uids = uidsToString(it)
            _screenEventFlow.emit(ProductsScreenEvent.OnShowMoveProductsScreen(uids))

            productsState.onAllProductsSelected(selected = false)
        }
    }

    private fun onClickCopyShoppingList() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.copyShoppingList(shoppingUid)
        _screenEventFlow.emit(ProductsScreenEvent.OnShowBackScreen)
    }

    private fun onClickMoveProductUp(
        event: ProductsEvent.OnClickMoveProductUp
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.moveProductUp(
            shoppingUid = shoppingUid,
            productUid = event.productUid
        )
        _screenEventFlow.emit(ProductsScreenEvent.OnUpdateProductsWidget(shoppingUid))
    }

    private fun onClickMoveProductDown(
        event: ProductsEvent.OnClickMoveProductDown
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.moveProductDown(
            shoppingUid = shoppingUid,
            productUid = event.productUid
        )
        _screenEventFlow.emit(ProductsScreenEvent.OnUpdateProductsWidget(shoppingUid))
    }

    private fun onClickDeleteProducts() = viewModelScope.launch(AppDispatchers.Main) {
        productsState.selectedUids?.let {
            shoppingListsRepository.deleteProductsByProductUids(
                productsUids = it,
                shoppingUid = shoppingUid
            )
            productsState.onAllProductsSelected(selected = false)
            _screenEventFlow.emit(ProductsScreenEvent.OnUpdateProductsWidget(shoppingUid))
        }
    }

    private fun onClickShareProducts() = viewModelScope.launch(AppDispatchers.Main) {
        val shareText = productsState.getShareText()
        _screenEventFlow.emit(ProductsScreenEvent.OnShareProducts(shareText))
        productsState.onShowProductsMenu(expanded = false)
    }

    private fun onClickCalculateChange() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsScreenEvent.OnShowCalculateChangeScreen(shoppingUid))
        productsState.onShowProductsMenu(expanded = false)
    }

    private fun onClickSearchProducts() {
        productsState.onSearch()
    }

    private fun onSearchValueChanged(event: ProductsEvent.OnSearchValueChanged) {
        productsState.onSearchValueChanged(event.value)
    }

    private fun onInvertSearch() = viewModelScope.launch(AppDispatchers.Main) {
        val display = !productsState.displaySearch
        productsState.onShowSearch(display)

        if (!display) {
            _screenEventFlow.emit(ProductsScreenEvent.OnHideKeyboard)
        }
    }

    private fun onMoveShoppingListSelected(
        event: ProductsEvent.OnMoveShoppingListSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        when (event.location) {
            ShoppingLocation.PURCHASES -> {
                shoppingListsRepository.moveShoppingListToPurchases(shoppingUid)
            }

            ShoppingLocation.ARCHIVE -> {
                shoppingListsRepository.moveShoppingListToArchive(shoppingUid)
            }

            ShoppingLocation.TRASH -> {
                shoppingListsRepository.moveShoppingListToTrash(shoppingUid)
            }
        }
        _screenEventFlow.emit(ProductsScreenEvent.OnShowBackScreen)
    }

    private fun onDisplayTotalSelected(
        event: ProductsEvent.OnDisplayTotalSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.displayTotal(event.displayTotal)
        productsState.onSelectDisplayTotal(expanded = false)
        _screenEventFlow.emit(ProductsScreenEvent.OnUpdateProductsWidget(shoppingUid))
    }

    private fun onSelectDisplayTotal(
        event: ProductsEvent.OnSelectDisplayTotal
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onSelectDisplayTotal(event.expanded)
    }

    private fun onInvertDisplayLongTotal() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.invertLongTotal()
        productsState.onSelectDisplayTotal(expanded = false)
    }

    private fun onSortSelected(
        event: ProductsEvent.OnSortSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.sortProducts(
            shoppingUid = shoppingUid,
            sort = Sort(event.sortBy),
            automaticSort = productsState.sortFormatted
        )
        productsState.onSelectSort(expanded = false)
    }

    private fun onReverseSort() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.reverseProducts(
            shoppingUid = shoppingUid,
            automaticSort = productsState.sortFormatted
        )
        productsState.onSelectSort(expanded = false)
    }

    private fun onSelectSort(
        event: ProductsEvent.OnSelectSort
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onSelectSort(event.expanded)
    }

    private fun onInvertSortFormatted() = viewModelScope.launch(AppDispatchers.Main) {
        val sort = if (productsState.sortFormatted) {
            productsState.sortValue.selected
        } else {
            Sort(SortBy.CREATED)
        }
        shoppingListsRepository.sortProducts(
            shoppingUid = shoppingUid,
            sort = sort,
            automaticSort = !productsState.sortFormatted
        )
    }

    private fun onShowProductsMenu(
        event: ProductsEvent.OnShowProductsMenu
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onShowProductsMenu(event.expanded)
    }

    private fun onShowItemMoreMenu(
        event: ProductsEvent.OnShowItemMoreMenu
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onShowItemMoreMenu(event.expanded)
    }

    private fun onShowShoppingMenu(
        event: ProductsEvent.OnShowShoppingMenu
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onShowShoppingMenu(event.expanded)
    }

    private fun onAllProductsSelected(
        event: ProductsEvent.OnAllProductsSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onAllProductsSelected(event.selected)
    }

    private fun onProductSelected(
        event: ProductsEvent.OnProductSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onProductSelected(event.selected, event.productUid)
    }

    private fun onShowHiddenProducts(
        event: ProductsEvent.OnShowHiddenProducts
    ) = viewModelScope.launch(AppDispatchers.Main) {
        productsState.onShowHiddenProducts(event.display)
    }

    private fun onInvertMultiColumns() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.invertProductsMultiColumns()
        productsState.onShowProductsMenu(expanded = false)
    }

    private fun uidsToString(uids: List<String>): String {
        return uids.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "")
    }
}