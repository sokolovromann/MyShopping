package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.TrashRepository
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val repository: TrashRepository,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<TrashEvent> {

    val trashState: TrashState = TrashState()

    private val _screenEventFlow: MutableSharedFlow<TrashScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<TrashScreenEvent> = _screenEventFlow

    init {
        getShoppingLists()
    }

    override fun onEvent(event: TrashEvent) {
        when (event) {
            is TrashEvent.MoveShoppingListToPurchases -> moveShoppingListToPurchases(event)

            is TrashEvent.MoveShoppingListToArchive -> moveShoppingListToArchive(event)

            TrashEvent.DeleteShoppingLists -> deleteShoppingLists()

            is TrashEvent.DeleteShoppingList -> deleteShoppingList(event)

            TrashEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            TrashEvent.SelectShoppingListsDisplayCompleted -> selectShoppingListsDisplayCompleted()

            TrashEvent.SelectShoppingListsDisplayTotal -> selectShoppingListsDisplayTotal()

            is TrashEvent.SelectNavigationItem -> selectNavigationItem(event)

            is TrashEvent.SortShoppingLists -> sortShoppingLists(event)

            is TrashEvent.DisplayShoppingListsCompleted -> displayShoppingListsCompleted(event)

            is TrashEvent.DisplayShoppingListsTotal -> displayShoppingListsTotal(event)

            TrashEvent.InvertShoppingListsSort -> invertShoppingListsSort()

            TrashEvent.ShowBackScreen -> showBackScreen()

            is TrashEvent.ShowProducts -> showProducts(event)

            TrashEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is TrashEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            TrashEvent.HideNavigationDrawer -> hideNavigationDrawer()

            TrashEvent.HideShoppingListMenu -> hideShoppingListMenu()

            TrashEvent.HideShoppingListsSort -> hideShoppingListsSort()

            TrashEvent.HideShoppingListsDisplayCompleted -> hideShoppingListsDisplayCompleted()

            TrashEvent.HideShoppingListsDisplayTotal -> hideShoppingListsDisplayTotal()
        }
    }

    private fun getShoppingLists() = viewModelScope.launch {
        withContext(dispatchers.main) {
            trashState.showLoading()
        }

        repository.getShoppingLists().collect {
            shoppingListsLoaded(it)
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingLists: ShoppingLists
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            trashState.showNotFound(shoppingLists.preferences)
        } else {
            trashState.showShoppingLists(shoppingLists)
        }
    }

    private fun deleteShoppingLists() = viewModelScope.launch {
        val uids = trashState.getUidsResult()
            .getOrElse { return@launch }
        repository.deleteShoppingLists(uids)
    }

    private fun deleteShoppingList(
        event: TrashEvent.DeleteShoppingList
    ) = viewModelScope.launch {
        repository.deleteShoppingList(event.uid)

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun moveShoppingListToPurchases(
        event: TrashEvent.MoveShoppingListToPurchases
    ) = viewModelScope.launch {
        repository.moveShoppingListToPurchases(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun moveShoppingListToArchive(
        event: TrashEvent.MoveShoppingListToArchive
    ) = viewModelScope.launch {
        repository.moveShoppingListToArchive(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun selectShoppingListsSort() {
        trashState.showSort()
    }

    private fun selectShoppingListsDisplayCompleted() {
        trashState.showDisplayCompleted()
    }

    private fun selectShoppingListsDisplayTotal() {
        trashState.showDisplayTotal()
    }

    private fun selectNavigationItem(
        event: TrashEvent.SelectNavigationItem
    ) = viewModelScope.launch(dispatchers.main) {
        when (event.route) {
            UiRoute.Purchases -> _screenEventFlow.emit(TrashScreenEvent.ShowPurchases)
            UiRoute.Archive -> _screenEventFlow.emit(TrashScreenEvent.ShowArchive)
            UiRoute.Autocompletes -> _screenEventFlow.emit(TrashScreenEvent.ShowAutocompletes)
            UiRoute.Settings -> _screenEventFlow.emit(TrashScreenEvent.ShowSettings)
            else -> return@launch
        }
    }

    private fun sortShoppingLists(event: TrashEvent.SortShoppingLists) = viewModelScope.launch {
        when (event.sortBy) {
            SortBy.CREATED -> repository.sortShoppingListsByCreated()
            SortBy.LAST_MODIFIED -> repository.sortShoppingListsByLastModified()
            SortBy.NAME -> repository.sortShoppingListsByName()
            SortBy.TOTAL -> repository.sortShoppingListsByTotal()
        }

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun displayShoppingListsCompleted(
        event: TrashEvent.DisplayShoppingListsCompleted
    ) = viewModelScope.launch {
        when (event.displayCompleted) {
            DisplayCompleted.FIRST -> repository.displayShoppingListsCompletedFirst()
            DisplayCompleted.LAST -> repository.displayShoppingListsCompletedLast()
            DisplayCompleted.HIDE -> repository.hideShoppingListsCompleted()
        }

        withContext(dispatchers.main) {
            hideShoppingListsDisplayCompleted()
        }
    }

    private fun displayShoppingListsTotal(
        event: TrashEvent.DisplayShoppingListsTotal
    ) = viewModelScope.launch {
        when (event.displayTotal) {
            DisplayTotal.ALL -> repository.displayShoppingListsAllTotal()
            DisplayTotal.COMPLETED -> repository.displayShoppingListsCompletedTotal()
            DisplayTotal.ACTIVE -> repository.displayShoppingListsActiveTotal()
        }

        withContext(dispatchers.main) {
            hideShoppingListsDisplayTotal()
        }
    }

    private fun invertShoppingListsSort() = viewModelScope.launch {
        repository.invertShoppingListsSort()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.ShowBackScreen)
    }

    private fun showProducts(event: TrashEvent.ShowProducts) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.ShowProducts(event.uid))
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.ShowNavigationDrawer)
    }

    private fun showShoppingListMenu(event: TrashEvent.ShowShoppingListMenu) {
        trashState.showShoppingListMenu(event.uid)
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.HideNavigationDrawer)
    }

    private fun hideShoppingListMenu() {
        trashState.hideShoppingListMenu()
    }

    private fun hideShoppingListsSort() {
        trashState.hideSort()
    }

    private fun hideShoppingListsDisplayCompleted() {
        trashState.hideDisplayCompleted()
    }

    private fun hideShoppingListsDisplayTotal() {
        trashState.hideDisplayTotal()
    }
}