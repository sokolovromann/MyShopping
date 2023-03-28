package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.PurchasesRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: PurchasesRepository,
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

            is PurchasesEvent.MoveShoppingListToArchive -> moveShoppingListToArchive(event)

            is PurchasesEvent.MoveShoppingListToTrash -> moveShoppingListToTrash(event)

            is PurchasesEvent.MoveShoppingListUp -> moveShoppingListUp(event)

            is PurchasesEvent.MoveShoppingListDown -> moveShoppingListDown(event)

            PurchasesEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is PurchasesEvent.SelectNavigationItem -> selectNavigationItem(event)

            PurchasesEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            PurchasesEvent.SelectShoppingListsToArchive -> selectShoppingListsToArchive()

            PurchasesEvent.SelectShoppingListsToDelete -> selectShoppingListsToDelete()

            is PurchasesEvent.SortShoppingLists -> sortShoppingLists(event)

            is PurchasesEvent.MoveAllShoppingListsTo -> moveAllShoppingListsTo(event)

            is PurchasesEvent.MoveCompletedShoppingListsTo -> moveCompletedShoppingListsTo(event)

            is PurchasesEvent.MoveActiveShoppingListsTo -> moveActiveShoppingListsTo(event)

            is PurchasesEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            PurchasesEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            is PurchasesEvent.ShowProducts -> showProducts(event)

            PurchasesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is PurchasesEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            PurchasesEvent.ShowPurchasesMenu -> showPurchasesMenu()

            PurchasesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            PurchasesEvent.HideShoppingListMenu -> hideShoppingListMenu()

            PurchasesEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

            PurchasesEvent.HidePurchasesMenu -> hidePurchasesMenu()

            PurchasesEvent.HideShoppingListsSort -> hideShoppingListsSort()

            PurchasesEvent.HideShoppingListsToArchive -> hideShoppingListsToArchive()

            PurchasesEvent.HideShoppingListsToDelete -> hideShoppingListsToDelete()

            PurchasesEvent.FinishApp -> finishApp()
        }
    }

    private fun getShoppingLists() = viewModelScope.launch {
        withContext(dispatchers.main) {
            purchasesState.showLoading()
        }

        repository.getShoppingLists().collect {
            shoppingListsLoaded(it)
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingLists: ShoppingLists
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            purchasesState.showNotFound(shoppingLists.preferences)
        } else {
            purchasesState.showShoppingLists(shoppingLists)
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        val shoppingList = purchasesState.getShoppingListResult().getOrElse {
            return@launch
        }
        repository.addShoppingList(shoppingList)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(PurchasesScreenEvent.ShowProducts(shoppingList.uid))
        }
    }

    private fun moveShoppingListToArchive(
        event: PurchasesEvent.MoveShoppingListToArchive
    ) = viewModelScope.launch {
        repository.moveShoppingListToArchive(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun moveShoppingListToTrash(
        event: PurchasesEvent.MoveShoppingListToTrash
    ) = viewModelScope.launch {
        repository.moveShoppingListToTrash(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun moveShoppingListUp(
        event: PurchasesEvent.MoveShoppingListUp
    ) = viewModelScope.launch {
        purchasesState.getShoppingListsUpResult(event.uid)
            .onSuccess { repository.swapShoppingLists(it.first, it.second) }

        purchasesState.hideShoppingListMenu()
    }

    private fun moveShoppingListDown(
        event: PurchasesEvent.MoveShoppingListDown
    ) = viewModelScope.launch {
        purchasesState.getShoppingListsDownResult(event.uid)
            .onSuccess { repository.swapShoppingLists(it.first, it.second) }

        purchasesState.hideShoppingListMenu()
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

    private fun selectShoppingListsToArchive() {
        purchasesState.showToArchive()
    }

    private fun selectShoppingListsToDelete() {
        purchasesState.showToDelete()
    }

    private fun sortShoppingLists(event: PurchasesEvent.SortShoppingLists) = viewModelScope.launch {
        val shoppingLists = purchasesState.sortShoppingListsResult(event.sortBy).getOrElse {
            withContext(dispatchers.main) { hideShoppingListsSort() }
            return@launch
        }

        repository.swapShoppingLists(shoppingLists)

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun moveAllShoppingListsTo(
        event: PurchasesEvent.MoveAllShoppingListsTo
    ) = viewModelScope.launch {
        purchasesState.getShoppingListsResult().onSuccess { shoppingLists ->
            shoppingLists.forEach {
                moveShoppingListToArchiveOrTrash(it.uid, event.toArchive)
            }
        }

        withContext(dispatchers.main) {
            hideShoppingListsToArchiveOrToDelete(event.toArchive)
        }
    }

    private fun moveCompletedShoppingListsTo(
        event: PurchasesEvent.MoveCompletedShoppingListsTo
    ) = viewModelScope.launch {
        purchasesState.getShoppingListsResult().onSuccess { shoppingLists ->
            shoppingLists.forEach {
                if (it.completed) {
                    moveShoppingListToArchiveOrTrash(it.uid, event.toArchive)
                }
            }
        }

        withContext(dispatchers.main) {
            hideShoppingListsToArchiveOrToDelete(event.toArchive)
        }
    }

    private fun moveActiveShoppingListsTo(
        event: PurchasesEvent.MoveActiveShoppingListsTo
    ) = viewModelScope.launch {
        purchasesState.getShoppingListsResult().onSuccess { shoppingLists ->
            shoppingLists.forEach {
                if (!it.completed) {
                    moveShoppingListToArchiveOrTrash(it.uid, event.toArchive)
                }
            }
        }

        withContext(dispatchers.main) {
            hideShoppingListsToArchiveOrToDelete(event.toArchive)
        }
    }

    private fun moveShoppingListToArchiveOrTrash(
        uid: String,
        toArchive: Boolean
    ) = viewModelScope.launch {
        if (toArchive) {
            repository.moveShoppingListToArchive(uid, System.currentTimeMillis())
        } else {
            repository.moveShoppingListToTrash(uid, System.currentTimeMillis())
        }
    }

    private fun displayPurchasesTotal(
        event: PurchasesEvent.DisplayPurchasesTotal
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

    private fun showShoppingListMenu(event: PurchasesEvent.ShowShoppingListMenu) {
        purchasesState.showShoppingListMenu(event.uid)
    }

    private fun showPurchasesMenu() {
        purchasesState.showPurchasesMenu()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.HideNavigationDrawer)
    }

    private fun hideShoppingListMenu() {
        purchasesState.hideShoppingListMenu()
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

    private fun hideShoppingListsToArchive() {
        purchasesState.hideToArchive()
    }

    private fun hideShoppingListsToDelete() {
        purchasesState.hideToDelete()
    }

    private fun hideShoppingListsToArchiveOrToDelete(toArchive: Boolean) {
        if (toArchive) {
            hideShoppingListsToArchive()
        } else {
            hideShoppingListsToDelete()
        }
    }

    private fun finishApp() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.FinishApp)
    }
}