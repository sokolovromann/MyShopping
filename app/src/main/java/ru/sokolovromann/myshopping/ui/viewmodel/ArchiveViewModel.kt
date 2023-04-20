package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.ArchiveRepository
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val repository: ArchiveRepository,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<ArchiveEvent> {

    val archiveState: ArchiveState = ArchiveState()

    private val _screenEventFlow: MutableSharedFlow<ArchiveScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ArchiveScreenEvent> = _screenEventFlow

    init {
        getShoppingLists()
    }

    override fun onEvent(event: ArchiveEvent) {
        when (event) {
            ArchiveEvent.MoveShoppingListsToPurchases -> moveShoppingListsToPurchases()

            ArchiveEvent.MoveShoppingListsToTrash -> moveShoppingListsToTrash()

            ArchiveEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is ArchiveEvent.SelectNavigationItem -> selectNavigationItem(event)

            ArchiveEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            is ArchiveEvent.SelectShoppingList -> selectShoppingList(event)

            ArchiveEvent.SelectSelectShoppingLists -> selectSelectShoppingLists()

            ArchiveEvent.SelectAllShoppingLists -> selectAllShoppingLists()

            ArchiveEvent.SelectCompletedShoppingLists -> selectCompletedShoppingLists()

            ArchiveEvent.SelectActiveShoppingLists -> selectActiveShoppingLists()

            is ArchiveEvent.UnselectShoppingList -> unselectShoppingList(event)

            ArchiveEvent.CancelSelectingShoppingLists -> cancelSelectingShoppingLists()

            is ArchiveEvent.SortShoppingLists -> sortShoppingLists(event)

            is ArchiveEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            ArchiveEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            ArchiveEvent.ShowBackScreen -> showBackScreen()

            is ArchiveEvent.ShowProducts -> showProducts(event)

            ArchiveEvent.ShowNavigationDrawer -> showNavigationDrawer()

            ArchiveEvent.ShowArchiveMenu -> showArchiveMenu()

            ArchiveEvent.HideNavigationDrawer -> hideNavigationDrawer()

            ArchiveEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

            ArchiveEvent.HideArchiveMenu -> hideArchiveMenu()

            ArchiveEvent.HideShoppingListsSort -> hideShoppingListsSort()

            ArchiveEvent.HideSelectShoppingLists -> hideSelectShoppingLists()
        }
    }

    private fun getShoppingLists() = viewModelScope.launch {
        withContext(dispatchers.main) {
            archiveState.showLoading()
        }

        repository.getShoppingLists().collect {
            shoppingListsLoaded(it)
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingLists: ShoppingLists
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            archiveState.showNotFound(shoppingLists.preferences)
        } else {
            archiveState.showShoppingLists(shoppingLists)
        }
    }

    private fun moveShoppingListsToPurchases() = viewModelScope.launch {
        archiveState.screenData.selectedUids?.let {
            repository.moveShoppingListsToPurchases(
                uids = it,
                lastModified = System.currentTimeMillis()
            )

            withContext(dispatchers.main) {
                unselectAllShoppingLists()
            }
        }
    }

    private fun moveShoppingListsToTrash() = viewModelScope.launch {
        archiveState.screenData.selectedUids?.let {
            repository.moveShoppingListsToTrash(
                uids = it,
                lastModified = System.currentTimeMillis()
            )

            withContext(dispatchers.main) {
                unselectAllShoppingLists()
            }
        }
    }

    private fun selectDisplayPurchasesTotal() {
        archiveState.selectDisplayPurchasesTotal()
    }

    private fun selectNavigationItem(
        event: ArchiveEvent.SelectNavigationItem
    ) = viewModelScope.launch(dispatchers.main) {
        when (event.route) {
            UiRoute.Purchases -> _screenEventFlow.emit(ArchiveScreenEvent.ShowPurchases)
            UiRoute.Trash -> _screenEventFlow.emit(ArchiveScreenEvent.ShowTrash)
            UiRoute.Autocompletes -> _screenEventFlow.emit(ArchiveScreenEvent.ShowAutocompletes)
            UiRoute.Settings -> _screenEventFlow.emit(ArchiveScreenEvent.ShowSettings)
            else -> return@launch
        }
    }

    private fun selectShoppingListsSort() {
        archiveState.showSort()
    }

    private fun selectShoppingList(event: ArchiveEvent.SelectShoppingList) {
        archiveState.selectShoppingList(event.uid)
    }

    private fun selectSelectShoppingLists() {
        archiveState.showSelectingMenu()
    }

    private fun selectAllShoppingLists() {
        archiveState.selectAllShoppingList()
    }

    private fun selectCompletedShoppingLists() {
        archiveState.selectCompletedShoppingList()
    }

    private fun selectActiveShoppingLists() {
        archiveState.selectActiveShoppingList()
    }

    private fun unselectShoppingList(event: ArchiveEvent.UnselectShoppingList) {
        archiveState.unselectShoppingList(event.uid)
    }

    private fun unselectAllShoppingLists() {
        archiveState.unselectAllShoppingList()
    }

    private fun cancelSelectingShoppingLists() {
        unselectAllShoppingLists()
    }

    private fun sortShoppingLists(event: ArchiveEvent.SortShoppingLists) = viewModelScope.launch {
        val shoppingLists = archiveState.sortShoppingListsResult(event.sortBy).getOrElse {
            withContext(dispatchers.main) { hideShoppingListsSort() }
            return@launch
        }

        repository.swapShoppingLists(shoppingLists)

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun displayPurchasesTotal(
        event: ArchiveEvent.DisplayPurchasesTotal
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
        archiveState.displayHiddenShoppingLists()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ArchiveScreenEvent.ShowBackScreen)
    }

    private fun showProducts(event: ArchiveEvent.ShowProducts) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ArchiveScreenEvent.ShowProducts(event.uid))
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ArchiveScreenEvent.ShowNavigationDrawer)
    }

    private fun showArchiveMenu() {
        archiveState.showArchiveMenu()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ArchiveScreenEvent.HideNavigationDrawer)
    }

    private fun hideDisplayPurchasesTotal() {
        archiveState.hideDisplayPurchasesTotal()
    }

    private fun hideArchiveMenu() {
        archiveState.hideArchiveMenu()
    }

    private fun hideShoppingListsSort() {
        archiveState.hideSort()
    }

    private fun hideSelectShoppingLists() {
        archiveState.hideSelectingMenu()
    }
}