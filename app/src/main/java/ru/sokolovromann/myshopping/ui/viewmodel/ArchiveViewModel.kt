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
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.data.repository.model.SortBy
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

            ArchiveEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            ArchiveEvent.SelectShoppingListsDisplayCompleted -> selectShoppingListsDisplayCompleted()

            ArchiveEvent.SelectShoppingListsDisplayTotal -> selectShoppingListsDisplayTotal()

            is ArchiveEvent.SelectNavigationItem -> selectNavigationItem(event)

            is ArchiveEvent.SortShoppingLists -> sortShoppingLists(event)

            is ArchiveEvent.DisplayShoppingListsCompleted -> displayShoppingListsCompleted(event)

            is ArchiveEvent.DisplayShoppingListsTotal -> displayShoppingListsTotal(event)

            ArchiveEvent.InvertShoppingListsSort -> invertShoppingListsSort()

            ArchiveEvent.ShowBackScreen -> showBackScreen()

            is ArchiveEvent.ShowProducts -> showProducts(event)

            ArchiveEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is ArchiveEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            ArchiveEvent.HideNavigationDrawer -> hideNavigationDrawer()

            ArchiveEvent.HideShoppingListMenu -> hideShoppingListMenu()

            ArchiveEvent.HideShoppingListsSort -> hideShoppingListsSort()

            ArchiveEvent.HideShoppingListsDisplayCompleted -> hideShoppingListsDisplayCompleted()

            ArchiveEvent.HideShoppingListsDisplayTotal -> hideShoppingListsDisplayTotal()
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

    private fun selectShoppingListsSort() {
        archiveState.showSort()
    }

    private fun selectShoppingListsDisplayCompleted() {
        archiveState.showDisplayCompleted()
    }

    private fun selectShoppingListsDisplayTotal() {
        archiveState.showDisplayTotal()
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

    private fun sortShoppingLists(event: ArchiveEvent.SortShoppingLists) = viewModelScope.launch {
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
        event: ArchiveEvent.DisplayShoppingListsCompleted
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
        event: ArchiveEvent.DisplayShoppingListsTotal
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

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(ArchiveScreenEvent.HideNavigationDrawer)
    }

    private fun hideShoppingListMenu() {
        archiveState.hideShoppingListMenu()
    }

    private fun hideShoppingListsSort() {
        archiveState.hideSort()
    }

    private fun hideShoppingListsDisplayCompleted() {
        archiveState.hideDisplayCompleted()
    }

    private fun hideShoppingListsDisplayTotal() {
        archiveState.hideDisplayTotal()
    }
}