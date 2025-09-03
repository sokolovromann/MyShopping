package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.model.PurchasesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<PurchasesEvent> {

    val purchasesState: PurchasesState = PurchasesState()

    private val _screenEventFlow: MutableSharedFlow<PurchasesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<PurchasesScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: PurchasesEvent) {
        when (event) {
            is PurchasesEvent.OnClickShoppingList -> onClickShoppingList(event)

            PurchasesEvent.OnClickAddShoppingList -> onClickAddShoppingList()

            PurchasesEvent.OnClickBack -> onClickBack()

            is PurchasesEvent.OnMoveShoppingListSelected -> onMoveShoppingListSelected(event)

            PurchasesEvent.OnClickPinShoppingLists -> onClickPinShoppingLists()

            PurchasesEvent.OnClickCopyShoppingLists -> onClickCopyShoppingLists()

            is PurchasesEvent.OnClickMoveShoppingListUp -> onClickMoveShoppingListUp(event)

            is PurchasesEvent.OnClickMoveShoppingListDown -> onClickMoveShoppingListDown(event)

            is PurchasesEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is PurchasesEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            PurchasesEvent.OnClickSearchShoppingLists -> onClickSearchShoppingLists()

            is PurchasesEvent.OnSearchValueChanged -> onSearchValueChanged(event)

            PurchasesEvent.OnInvertSearch -> onInvertSearch()

            is PurchasesEvent.OnDisplayProductsSelected -> onDisplayProductsSelected(event)

            is PurchasesEvent.OnSelectDisplayProducts -> onSelectDisplayProducts(event)

            is PurchasesEvent.OnDisplayTotalSelected -> onDisplayTotalSelected(event)

            is PurchasesEvent.OnSelectDisplayTotal -> onSelectDisplayTotal(event)

            is PurchasesEvent.OnSortSelected -> onSortSelected(event)

            PurchasesEvent.OnReverseSort -> onReverseSort()

            is PurchasesEvent.OnSelectSort -> onSelectSort(event)

            PurchasesEvent.OnInvertSortFormatted -> onInvertSortFormatted()

            is PurchasesEvent.OnShowPurchasesMenu -> onShowPurchasesMenu(event)

            is PurchasesEvent.OnShowItemMoreMenu -> onShowItemMoreMenu(event)

            is PurchasesEvent.OnAllShoppingListsSelected -> onAllShoppingListsSelected(event)

            is PurchasesEvent.OnShoppingListSelected -> onShoppingListSelected(event)

            is PurchasesEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)

            is PurchasesEvent.OnSelectView -> onSelectView(event)

            is PurchasesEvent.OnViewSelected -> onViewSelected(event)

            is PurchasesEvent.OnMarkAsSelected -> onMarkAsSelected(event)

            is PurchasesEvent.OnSelectMarkAs -> onSelectMarkAs(event)

            is PurchasesEvent.OnSwipeShoppingLeft -> onSwipeShoppingLeft(event)

            is PurchasesEvent.OnSwipeShoppingRight -> onSwipeShoppingRight(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        purchasesState.onWaiting()

        shoppingListsRepository.getPurchasesWithConfig().collect {
            purchasesState.populate(it)
        }
    }

    private fun onClickShoppingList(
        event: PurchasesEvent.OnClickShoppingList
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnShowProductsScreen(event.uid))
    }

    private fun onClickAddShoppingList() = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.addShopping().onSuccess {
            val event = when (purchasesState.afterAddShopping) {
                AfterAddShopping.OPEN_PRODUCTS_SCREEN -> {
                    PurchasesScreenEvent.OnShowProductsScreen(it)
                }
                AfterAddShopping.OPEN_EDIT_SHOPPING_NAME_SCREEN -> {
                    PurchasesScreenEvent.OnShowEditShoppingListNameScreen(it)
                }
                AfterAddShopping.OPEN_ADD_PRODUCT_SCREEN -> {
                    PurchasesScreenEvent.OnShowAddEditProductScreen(it)
                }
            }
            _screenEventFlow.emit(event)
        }
    }

    private fun onClickBack() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnFinishApp)
    }

    private fun onMoveShoppingListSelected(
        event: PurchasesEvent.OnMoveShoppingListSelected
    ) = viewModelScope.launch(dispatcher) {
        purchasesState.selectedUids?.let {
            when (event.location) {
                ShoppingLocation.PURCHASES -> shoppingListsRepository.moveShoppingListsToPurchases(it)
                ShoppingLocation.ARCHIVE -> shoppingListsRepository.moveShoppingListsToArchive(it)
                ShoppingLocation.TRASH -> shoppingListsRepository.moveShoppingListsToTrash(it)
            }
            purchasesState.onAllShoppingListsSelected(selected = false)
            alarmManager.deleteReminders(it)
        }
    }

    private fun onClickPinShoppingLists() = viewModelScope.launch(dispatcher) {
        purchasesState.selectedUids?.let {
            if (purchasesState.isOnlyPinned()) {
                shoppingListsRepository.unpinShoppingLists(it)
            } else {
                shoppingListsRepository.pinShoppingLists(it)
            }
            purchasesState.onAllShoppingListsSelected(selected = false)
        }
    }

    private fun onClickCopyShoppingLists() = viewModelScope.launch(dispatcher) {
        purchasesState.selectedUids?.let {
            shoppingListsRepository.copyShoppingLists(it)
            purchasesState.onShowItemMoreMenu(expanded = false)
        }
        purchasesState.onAllShoppingListsSelected(selected = false)
    }

    private fun onClickMoveShoppingListUp(
        event: PurchasesEvent.OnClickMoveShoppingListUp
    ) = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.moveShoppingListUp(shoppingUid = event.uid)
    }

    private fun onClickMoveShoppingListDown(
        event: PurchasesEvent.OnClickMoveShoppingListDown
    ) = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.moveShoppingListDown(shoppingUid = event.uid)
    }

    private fun onDrawerScreenSelected(
        event: PurchasesEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: PurchasesEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onClickSearchShoppingLists() {
        purchasesState.onSearch()
    }

    private fun onSearchValueChanged(event: PurchasesEvent.OnSearchValueChanged) {
        purchasesState.onSearchValueChanged(event.value)
    }

    private fun onInvertSearch() = viewModelScope.launch(dispatcher) {
        val display = !purchasesState.displaySearch
        purchasesState.onShowSearch(display)

        if (!display) {
            _screenEventFlow.emit(PurchasesScreenEvent.OnHideKeyboard)
        }
    }

    private fun onDisplayProductsSelected(
        event: PurchasesEvent.OnDisplayProductsSelected
    ) = viewModelScope.launch(dispatcher) {
        appConfigRepository.displayShoppingsProducts(event.displayProducts)
        purchasesState.onSelectDisplayProducts(expanded = false)
    }

    private fun onSelectDisplayProducts(event: PurchasesEvent.OnSelectDisplayProducts) {
        purchasesState.onSelectDisplayProducts(event.expanded)
    }

    private fun onDisplayTotalSelected(
        event: PurchasesEvent.OnDisplayTotalSelected
    ) = viewModelScope.launch(dispatcher) {
        appConfigRepository.displayTotal(event.displayTotal)
        purchasesState.onSelectDisplayTotal(expanded = false)
    }

    private fun onSelectDisplayTotal(event: PurchasesEvent.OnSelectDisplayTotal) {
        purchasesState.onSelectDisplayTotal(event.expanded)
    }

    private fun onSortSelected(
        event: PurchasesEvent.OnSortSelected
    ) = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.sortShoppingLists(
            sort = Sort(event.sortBy),
            automaticSort = purchasesState.sortFormatted
        )
        purchasesState.onSelectSort(expanded = false)
    }

    private fun onReverseSort() = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.reverseShoppingLists(
            automaticSort = purchasesState.sortFormatted
        )
        purchasesState.onSelectSort(expanded = false)
    }

    private fun onSelectSort(event: PurchasesEvent.OnSelectSort) {
        purchasesState.onSelectSort(event.expanded)
    }

    private fun onInvertSortFormatted() = viewModelScope.launch(dispatcher) {
        val sort = if (purchasesState.sortFormatted) {
            purchasesState.sortValue.selected
        } else {
            Sort(SortBy.CREATED)
        }
        shoppingListsRepository.sortShoppingLists(
            sort = sort,
            automaticSort = !purchasesState.sortFormatted
        )
    }

    private fun onShowPurchasesMenu(event: PurchasesEvent.OnShowPurchasesMenu) {
        purchasesState.onShowPurchasesMenu(event.expanded)
    }

    private fun onShowItemMoreMenu(event: PurchasesEvent.OnShowItemMoreMenu) {
        purchasesState.onShowItemMoreMenu(event.expanded)
    }

    private fun onAllShoppingListsSelected(event: PurchasesEvent.OnAllShoppingListsSelected) {
        purchasesState.onAllShoppingListsSelected(event.selected)
    }

    private fun onShoppingListSelected(event: PurchasesEvent.OnShoppingListSelected) {
        purchasesState.onShoppingListSelected(event.selected, event.uid)
    }

    private fun onShowHiddenShoppingLists(event: PurchasesEvent.OnShowHiddenShoppingLists) {
        purchasesState.onShowHiddenShoppingLists(event.display)
    }

    private fun onSelectView(event: PurchasesEvent.OnSelectView) {
        purchasesState.onSelectView(event.expanded)
    }

    private fun onViewSelected(
        event: PurchasesEvent.OnViewSelected
    ) = viewModelScope.launch(dispatcher) {
        if (event.multiColumns != purchasesState.multiColumnsValue.selected) {
            appConfigRepository.invertShoppingListsMultiColumns()
        }
        purchasesState.onSelectView(expanded = false)
    }

    private fun onMarkAsSelected(
        event: PurchasesEvent.OnMarkAsSelected
    ) = viewModelScope.launch(dispatcher) {
        purchasesState.selectedUids?.forEach { shoppingUid ->
            if (event.completed) {
                shoppingListsRepository.completeProducts(shoppingUid).let {
                    doAfterShoppingCompleted(shoppingUid)
                }
            } else {
                shoppingListsRepository.activeProducts(shoppingUid)
            }
        }
        purchasesState.onAllShoppingListsSelected(selected = false)
    }

    private fun onSelectMarkAs(event: PurchasesEvent.OnSelectMarkAs) {
        purchasesState.onSelectMarkAsMenu(event.expanded)
    }

    private fun onSwipeShoppingLeft(
        event: PurchasesEvent.OnSwipeShoppingLeft
    ) = viewModelScope.launch(dispatcher) {
        doAfterSwipeShopping(event.uid, purchasesState.swipeShoppingLeft)
    }

    private fun onSwipeShoppingRight(
        event: PurchasesEvent.OnSwipeShoppingRight
    ) = viewModelScope.launch(dispatcher) {
        doAfterSwipeShopping(event.uid, purchasesState.swipeShoppingRight)
    }

    private suspend fun doAfterSwipeShopping(
        uid: String,
        swipeShopping: SwipeShopping
    ) = withContext(dispatcher) {
        when (swipeShopping) {
            SwipeShopping.DISABLED -> {}
            SwipeShopping.ARCHIVE -> {
                shoppingListsRepository.moveShoppingListToArchive(uid)
            }
            SwipeShopping.DELETE -> {
                shoppingListsRepository.moveShoppingListToTrash(uid)
            }
            SwipeShopping.DELETE_PRODUCTS -> {
                shoppingListsRepository.deleteProductsByShoppingUid(uid)
            }
            SwipeShopping.COMPLETE -> {
                purchasesState.isShoppingListCompleted(uid)?.let { completed ->
                    invertShoppingStatus(uid, completed).let {
                        doAfterShoppingCompleted(uid)
                    }
                }
            }
        }
    }

    private suspend fun invertShoppingStatus(
        uid: String,
        completed: Boolean
    ) = withContext(dispatcher) {
        if (completed) {
            shoppingListsRepository.activeProducts(uid)
        } else {
            shoppingListsRepository.completeProducts(uid)
        }
    }

    private suspend fun doAfterShoppingCompleted(uid: String) = withContext(dispatcher) {
        when (purchasesState.getAfterShoppingCompleted()) {
            AfterShoppingCompleted.NOTHING -> {}
            AfterShoppingCompleted.ARCHIVE -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.moveShoppingListToArchive(uid)
                }
            }
            AfterShoppingCompleted.DELETE -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.moveShoppingListToTrash(uid)
                }
            }
            AfterShoppingCompleted.DELETE_PRODUCTS -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.deleteProductsByStatus(uid, DisplayTotal.ALL)
                }
            }
            AfterShoppingCompleted.DELETE_LIST_AND_PRODUCTS -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.moveShoppingListToTrash(uid)
                    shoppingListsRepository.deleteProductsByStatus(uid, DisplayTotal.ALL)
                }
            }
        }
    }
}