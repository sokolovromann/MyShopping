package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.model.PurchasesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<PurchasesEvent> {

    val purchasesState: PurchasesState = PurchasesState()

    private val _screenEventFlow: MutableSharedFlow<PurchasesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<PurchasesScreenEvent> = _screenEventFlow

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

            is PurchasesEvent.OnDisplayTotalSelected -> onDisplayTotalSelected(event)

            is PurchasesEvent.OnSelectDisplayTotal -> onSelectDisplayTotal(event)

            is PurchasesEvent.OnSortSelected -> onSortSelected(event)

            PurchasesEvent.OnReverseSort -> onReverseSort()

            is PurchasesEvent.OnSelectSort -> onSelectSort(event)

            is PurchasesEvent.OnShowPurchasesMenu -> onShowPurchasesMenu(event)

            is PurchasesEvent.OnShowItemMoreMenu -> onShowItemMoreMenu(event)

            is PurchasesEvent.OnAllShoppingListsSelected -> onAllShoppingListsSelected(event)

            is PurchasesEvent.OnShoppingListSelected -> onShoppingListSelected(event)

            is PurchasesEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)

            PurchasesEvent.OnInvertMultiColumns -> onInvertMultiColumns()
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        purchasesState.onWaiting()

        shoppingListsRepository.getPurchasesWithConfig().collect {
            purchasesState.populate(it)
        }
    }

    private fun onClickShoppingList(
        event: PurchasesEvent.OnClickShoppingList
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnShowProductsScreen(event.uid))
    }

    private fun onClickAddShoppingList() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.addShopping().onSuccess {
            _screenEventFlow.emit(PurchasesScreenEvent.OnShowProductsScreen(it))
        }
    }

    private fun onClickBack() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnFinishApp)
    }

    private fun onMoveShoppingListSelected(
        event: PurchasesEvent.OnMoveShoppingListSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        purchasesState.selectedUids?.let {
            when (event.location) {
                ShoppingLocation.PURCHASES -> shoppingListsRepository.moveShoppingListsToPurchases(it)
                ShoppingLocation.ARCHIVE -> shoppingListsRepository.moveShoppingListsToArchive(it)
                ShoppingLocation.TRASH -> shoppingListsRepository.moveShoppingListsToTrash(it)
            }
            purchasesState.onAllShoppingListsSelected(selected = false)
        }
    }

    private fun onClickPinShoppingLists() = viewModelScope.launch(AppDispatchers.Main) {
        purchasesState.selectedUids?.let {
            if (purchasesState.isOnlyPinned()) {
                shoppingListsRepository.unpinShoppingLists(it)
            } else {
                shoppingListsRepository.pinShoppingLists(it)
            }
            purchasesState.onAllShoppingListsSelected(selected = false)
        }
    }

    private fun onClickCopyShoppingLists() = viewModelScope.launch(AppDispatchers.Main) {
        purchasesState.selectedUids?.let {
            shoppingListsRepository.copyShoppingLists(it)
            purchasesState.onShowItemMoreMenu(expanded = false)
        }
    }

    private fun onClickMoveShoppingListUp(
        event: PurchasesEvent.OnClickMoveShoppingListUp
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.moveShoppingListUp(shoppingUid = event.uid)
    }

    private fun onClickMoveShoppingListDown(
        event: PurchasesEvent.OnClickMoveShoppingListDown
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.moveShoppingListDown(shoppingUid = event.uid)
    }

    private fun onDrawerScreenSelected(
        event: PurchasesEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: PurchasesEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(PurchasesScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onDisplayTotalSelected(
        event: PurchasesEvent.OnDisplayTotalSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.displayTotal(event.displayTotal)
        purchasesState.onSelectDisplayTotal(expanded = false)
    }

    private fun onSelectDisplayTotal(event: PurchasesEvent.OnSelectDisplayTotal) {
        purchasesState.onSelectDisplayTotal(event.expanded)
    }

    private fun onSortSelected(
        event: PurchasesEvent.OnSortSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.sortShoppingLists(sort = Sort(event.sortBy))
        purchasesState.onSelectSort(expanded = false)
    }

    private fun onReverseSort() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.reverseShoppingLists()
        purchasesState.onSelectSort(expanded = false)
    }

    private fun onSelectSort(event: PurchasesEvent.OnSelectSort) {
        purchasesState.onSelectSort(event.expanded)
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

    private fun onInvertMultiColumns() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.invertShoppingListsMultiColumns()
        purchasesState.onShowPurchasesMenu(expanded = false)
    }
}