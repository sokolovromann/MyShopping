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

            is PurchasesEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            is PurchasesEvent.ShowProducts -> showProducts(event)

            PurchasesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is PurchasesEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            PurchasesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            PurchasesEvent.HideShoppingListMenu -> hideShoppingListMenu()

            PurchasesEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()

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

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.HideNavigationDrawer)
    }

    private fun hideShoppingListMenu() {
        purchasesState.hideShoppingListMenu()
    }

    private fun hideDisplayPurchasesTotal() {
        purchasesState.hideDisplayPurchasesTotal()
    }

    private fun finishApp() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.FinishApp)
    }
}