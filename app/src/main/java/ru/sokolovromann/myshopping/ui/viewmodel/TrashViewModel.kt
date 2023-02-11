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
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
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

            TrashEvent.SelectShoppingListsDisplayTotal -> selectShoppingListsDisplayTotal()

            is TrashEvent.SelectNavigationItem -> selectNavigationItem(event)

            is TrashEvent.DisplayShoppingListsTotal -> displayShoppingListsTotal(event)

            TrashEvent.ShowBackScreen -> showBackScreen()

            is TrashEvent.ShowProducts -> showProducts(event)

            TrashEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is TrashEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            TrashEvent.HideNavigationDrawer -> hideNavigationDrawer()

            TrashEvent.HideShoppingListMenu -> hideShoppingListMenu()

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

    private fun hideShoppingListsDisplayTotal() {
        trashState.hideDisplayTotal()
    }
}