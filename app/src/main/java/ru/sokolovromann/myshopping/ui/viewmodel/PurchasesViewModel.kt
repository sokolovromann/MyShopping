package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<PurchasesEvent> {

    val purchasesState: PurchasesState = PurchasesState()

    private val _screenEventFlow: MutableSharedFlow<PurchasesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<PurchasesScreenEvent> = _screenEventFlow

    init {
        getShoppingLists()
    }

    override fun onEvent(event: PurchasesEvent) {
        when (event) {
            PurchasesEvent.AddShoppingList -> addShoppingList()

            PurchasesEvent.MoveShoppingListsToArchive -> moveShoppingListsToArchive()

            PurchasesEvent.MoveShoppingListsToTrash -> moveShoppingListsToTrash()

            PurchasesEvent.CopyShoppingLists -> copyShoppingLists()

            is PurchasesEvent.MoveShoppingListUp -> moveShoppingListUp(event)

            is PurchasesEvent.MoveShoppingListDown -> moveShoppingListDown(event)

            PurchasesEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is PurchasesEvent.SelectNavigationItem -> selectNavigationItem(event)

            PurchasesEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            is PurchasesEvent.SelectShoppingList -> selectShoppingList(event)

            PurchasesEvent.SelectAllShoppingLists -> selectAllShoppingLists()

            is PurchasesEvent.UnselectShoppingList -> unselectShoppingList(event)

            PurchasesEvent.CancelSelectingShoppingLists -> cancelSelectingShoppingLists()

            is PurchasesEvent.SortShoppingLists -> sortShoppingLists(event)

            PurchasesEvent.ReverseSortShoppingLists -> reverseSortShoppingLists()

            is PurchasesEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            PurchasesEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            is PurchasesEvent.ShowProducts -> showProducts(event)

            PurchasesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            PurchasesEvent.ShowPurchasesMenu -> showPurchasesMenu()

            PurchasesEvent.ShowSelectedMenu -> showSelectedMenu()

            PurchasesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            PurchasesEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

            PurchasesEvent.HidePurchasesMenu -> hidePurchasesMenu()

            PurchasesEvent.HideShoppingListsSort -> hideShoppingListsSort()

            PurchasesEvent.HideSelectedMenu -> hideSelectedMenu()

            PurchasesEvent.FinishApp -> finishApp()

            PurchasesEvent.InvertShoppingsMultiColumns -> invertShoppingListsMultiColumns()

            PurchasesEvent.PinShoppingLists -> pinShoppingLists()

            PurchasesEvent.UnpinShoppingLists -> unpinShoppingLists()
        }
    }

    private fun getShoppingLists() = viewModelScope.launch {
        withContext(dispatchers.main) {
            purchasesState.showLoading()
        }

        shoppingListsRepository.getPurchasesWithConfig().collect {
            shoppingListsLoaded(it)
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingListsWithConfig: ShoppingListsWithConfig
    ) = withContext(dispatchers.main) {
        val shoppingLists = ShoppingListsMapper.toShoppingLists(shoppingListsWithConfig)
        if (shoppingLists.isShoppingListsEmpty()) {
            purchasesState.showNotFound(shoppingLists)
        } else {
            purchasesState.showShoppingLists(shoppingLists)
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        shoppingListsRepository.addShopping()
            .onSuccess {
                withContext(dispatchers.main) {
                    _screenEventFlow.emit(PurchasesScreenEvent.ShowProducts(it))
                }
            }
    }

    private fun moveShoppingListsToArchive() = viewModelScope.launch {
        purchasesState.screenData.selectedUids?.let {
            shoppingListsRepository.moveShoppingListsToArchive(it)
        }

        withContext(dispatchers.main) {
            unselectAllShoppingList()
        }
    }

    private fun moveShoppingListsToTrash() = viewModelScope.launch {
        purchasesState.screenData.selectedUids?.let {
            shoppingListsRepository.moveShoppingListsToTrash(it)
        }

        withContext(dispatchers.main) {
            unselectAllShoppingList()
        }
    }

    private fun copyShoppingLists() = viewModelScope.launch {
        purchasesState.getCopyShoppingListsResult()
            .onSuccess { shoppingListsRepository.copyShoppingLists(it.map { list -> list.uid }) }

        withContext(dispatchers.main) {
            hideSelectedMenu()
        }
    }

    private fun moveShoppingListUp(
        event: PurchasesEvent.MoveShoppingListUp
    ) = viewModelScope.launch {
        shoppingListsRepository.moveShoppingListUp(shoppingUid = event.uid)
    }

    private fun moveShoppingListDown(
        event: PurchasesEvent.MoveShoppingListDown
    ) = viewModelScope.launch {
        shoppingListsRepository.moveShoppingListDown(shoppingUid = event.uid)
    }

    private fun selectDisplayPurchasesTotal() {
        purchasesState.selectDisplayPurchasesTotal()
    }

    private fun selectNavigationItem(
        event: PurchasesEvent.SelectNavigationItem
    ) = viewModelScope.launch(dispatchers.main) {
        when (event.route) {
            UiRoute.Archive -> _screenEventFlow.emit(PurchasesScreenEvent.ShowArchive)
            UiRoute.Trash -> _screenEventFlow.emit(PurchasesScreenEvent.ShowTrash)
            UiRoute.Autocompletes -> _screenEventFlow.emit(PurchasesScreenEvent.ShowAutocompletes)
            UiRoute.Settings -> _screenEventFlow.emit(PurchasesScreenEvent.ShowSettings)
            else -> return@launch
        }
    }

    private fun selectShoppingListsSort() {
        purchasesState.showSort()
    }

    private fun selectShoppingList(event: PurchasesEvent.SelectShoppingList) {
        purchasesState.selectShoppingList(event.uid)
    }

    private fun selectAllShoppingLists() {
        purchasesState.selectAllShoppingLists()
    }

    private fun unselectShoppingList(event: PurchasesEvent.UnselectShoppingList) {
        purchasesState.unselectShoppingList(event.uid)
    }

    private fun unselectAllShoppingList() {
        purchasesState.unselectAllShoppingLists()
    }

    private fun cancelSelectingShoppingLists() {
        unselectAllShoppingList()
    }

    private fun sortShoppingLists(event: PurchasesEvent.SortShoppingLists) = viewModelScope.launch {
        shoppingListsRepository.sortShoppingLists(
            sort = Sort(event.sortBy)
        )

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun reverseSortShoppingLists() = viewModelScope.launch {
        shoppingListsRepository.reverseShoppingLists()

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun displayPurchasesTotal(
        event: PurchasesEvent.DisplayPurchasesTotal
    ) = viewModelScope.launch {
        appConfigRepository.displayTotal(event.displayTotal)

        withContext(dispatchers.main) {
            hideDisplayPurchasesTotal()
        }
    }

    private fun displayHiddenShoppingLists() {
        purchasesState.displayHiddenShoppingLists()
    }

    private fun showProducts(
        event: PurchasesEvent.ShowProducts
    ) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.ShowProducts(event.uid))
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.ShowNavigationDrawer)
    }

    private fun showPurchasesMenu() {
        purchasesState.showPurchasesMenu()
    }

    private fun showSelectedMenu() {
        purchasesState.showSelectedMenu()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.HideNavigationDrawer)
    }

    private fun hideDisplayPurchasesTotal() {
        purchasesState.hideDisplayPurchasesTotal()
    }

    private fun hidePurchasesMenu() {
        purchasesState.hidePurchasesMenu()
    }

    private fun hideShoppingListsSort() {
        purchasesState.hideSort()
    }

    private fun hideSelectedMenu() {
        purchasesState.hideSelectedMenu()
    }

    private fun finishApp() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.FinishApp)
    }

    private fun invertShoppingListsMultiColumns() = viewModelScope.launch {
        appConfigRepository.invertShoppingListsMultiColumns()
    }

    private fun pinShoppingLists() = viewModelScope.launch {
        purchasesState.screenData.selectedUids?.let {
            shoppingListsRepository.pinShoppingLists(it)
        }

        withContext(dispatchers.main) {
            unselectAllShoppingList()
        }
    }

    private fun unpinShoppingLists() = viewModelScope.launch {
        purchasesState.screenData.selectedUids?.let {
            shoppingListsRepository.unpinShoppingLists(it)
        }

        withContext(dispatchers.main) {
            unselectAllShoppingList()
        }
    }
}