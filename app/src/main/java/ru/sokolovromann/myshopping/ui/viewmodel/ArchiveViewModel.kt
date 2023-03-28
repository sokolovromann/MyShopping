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
            is ArchiveEvent.MoveShoppingListToPurchases -> moveShoppingListToPurchases(event)

            is ArchiveEvent.MoveShoppingListToTrash -> moveShoppingListToTrash(event)

            ArchiveEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is ArchiveEvent.SelectNavigationItem -> selectNavigationItem(event)

            ArchiveEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            ArchiveEvent.SelectShoppingListsToPurchases -> selectShoppingListsToPurchases()

            ArchiveEvent.SelectShoppingListsToTrash -> selectShoppingListsToTrash()

            is ArchiveEvent.SortShoppingLists -> sortShoppingLists(event)

            is ArchiveEvent.MoveAllShoppingListsTo -> moveAllShoppingListsTo(event)

            is ArchiveEvent.MoveCompletedShoppingListsTo -> moveCompletedShoppingListsTo(event)

            is ArchiveEvent.MoveActiveShoppingListsTo -> moveActiveShoppingListsTo(event)

            is ArchiveEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            ArchiveEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            ArchiveEvent.ShowBackScreen -> showBackScreen()

            is ArchiveEvent.ShowProducts -> showProducts(event)

            ArchiveEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is ArchiveEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            ArchiveEvent.ShowArchiveMenu -> showArchiveMenu()

            ArchiveEvent.HideNavigationDrawer -> hideNavigationDrawer()

            ArchiveEvent.HideShoppingListMenu -> hideShoppingListMenu()

            ArchiveEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

            ArchiveEvent.HideArchiveMenu -> hideArchiveMenu()

            ArchiveEvent.HideShoppingListsSort -> hideShoppingListsSort()

            ArchiveEvent.HideShoppingListsToPurchases -> hideShoppingListsToPurchases()

            ArchiveEvent.HideShoppingListsToTrash -> hideShoppingListsToTrash()
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

    private fun moveShoppingListToPurchases(
        event: ArchiveEvent.MoveShoppingListToPurchases
    ) = viewModelScope.launch {
        repository.moveShoppingListToPurchases(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun moveShoppingListToTrash(
        event: ArchiveEvent.MoveShoppingListToTrash
    ) = viewModelScope.launch {
        repository.moveShoppingListToTrash(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
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

    private fun selectShoppingListsToPurchases() {
        archiveState.showToPurchases()
    }

    private fun selectShoppingListsToTrash() {
        archiveState.showToTrash()
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

    private fun moveAllShoppingListsTo(
        event: ArchiveEvent.MoveAllShoppingListsTo
    ) = viewModelScope.launch {
        archiveState.getShoppingListsResult().onSuccess { shoppingLists ->
            shoppingLists.forEach {
                moveShoppingListToPurchasesOrTrash(it.uid, event.toPurchases)
            }
        }

        withContext(dispatchers.main) {
            hideShoppingListsToPurchasesOrToTrash(event.toPurchases)
        }
    }

    private fun moveCompletedShoppingListsTo(
        event: ArchiveEvent.MoveCompletedShoppingListsTo
    ) = viewModelScope.launch {
        archiveState.getShoppingListsResult().onSuccess { shoppingLists ->
            shoppingLists.forEach {
                if (it.completed) {
                    moveShoppingListToPurchasesOrTrash(it.uid, event.toPurchases)
                }
            }
        }

        withContext(dispatchers.main) {
            hideShoppingListsToPurchasesOrToTrash(event.toPurchases)
        }
    }

    private fun moveActiveShoppingListsTo(
        event: ArchiveEvent.MoveActiveShoppingListsTo
    ) = viewModelScope.launch {
        archiveState.getShoppingListsResult().onSuccess { shoppingLists ->
            shoppingLists.forEach {
                if (!it.completed) {
                    moveShoppingListToPurchasesOrTrash(it.uid, event.toPurchases)
                }
            }
        }

        withContext(dispatchers.main) {
            hideShoppingListsToPurchasesOrToTrash(event.toPurchases)
        }
    }

    private fun moveShoppingListToPurchasesOrTrash(
        uid: String,
        toPurchases: Boolean
    ) = viewModelScope.launch {
        if (toPurchases) {
            repository.moveShoppingListToPurchases(uid, System.currentTimeMillis())
        } else {
            repository.moveShoppingListToTrash(uid, System.currentTimeMillis())
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

    private fun showShoppingListMenu(event: ArchiveEvent.ShowShoppingListMenu) {
        archiveState.showShoppingListMenu(event.uid)
    }

    private fun showArchiveMenu() {
        archiveState.showArchiveMenu()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ArchiveScreenEvent.HideNavigationDrawer)
    }

    private fun hideShoppingListMenu() {
        archiveState.hideShoppingListMenu()
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

    private fun hideShoppingListsToPurchases() {
        archiveState.hideToPurchases()
    }

    private fun hideShoppingListsToTrash() {
        archiveState.hideToTrash()
    }

    private fun hideShoppingListsToPurchasesOrToTrash(toPurchases: Boolean) {
        if (toPurchases) {
            hideShoppingListsToPurchases()
        } else {
            hideShoppingListsToTrash()
        }
    }
}