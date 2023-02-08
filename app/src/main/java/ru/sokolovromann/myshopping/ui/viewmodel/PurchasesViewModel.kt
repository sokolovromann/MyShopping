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

            is PurchasesEvent.MoveShoppingListToUp -> moveShoppingListToUp(event)

            is PurchasesEvent.MoveShoppingListToDown -> moveShoppingListToDown(event)

            PurchasesEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            PurchasesEvent.SelectShoppingListsDisplayCompleted -> selectShoppingListsDisplayCompleted()

            PurchasesEvent.SelectShoppingListsDisplayTotal -> selectShoppingListsDisplayTotal()

            is PurchasesEvent.SelectNavigationItem -> selectNavigationItem(event)

            is PurchasesEvent.SortShoppingLists -> sortShoppingLists(event)

            is PurchasesEvent.DisplayShoppingListsCompleted -> displayShoppingListsCompleted(event)

            is PurchasesEvent.DisplayShoppingListsTotal -> displayShoppingListsTotal(event)

            PurchasesEvent.InvertShoppingListsSort -> invertShoppingListsSort()

            is PurchasesEvent.ShowProducts -> showProducts(event)

            PurchasesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is PurchasesEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            PurchasesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            PurchasesEvent.HideShoppingListMenu -> hideShoppingListMenu()

            PurchasesEvent.HideShoppingListsSort -> hideShoppingListsSort()

            PurchasesEvent.HideShoppingListsDisplayCompleted -> hideShoppingListsDisplayCompleted()

            PurchasesEvent.HideShoppingListsDisplayTotal -> hideShoppingListsDisplayTotal()

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

    private fun moveShoppingListToUp(
        event: PurchasesEvent.MoveShoppingListToUp
    ) = viewModelScope.launch {
        val shoppingList = purchasesState.getShoppingListsToUpResult(event.uid).getOrElse {
            purchasesState.hideShoppingListMenu()
            return@launch
        }

        repository.swapShoppingLists(shoppingList.first, shoppingList.second)
    }

    private fun moveShoppingListToDown(
        event: PurchasesEvent.MoveShoppingListToDown
    ) = viewModelScope.launch {
        val shoppingList = purchasesState.getShoppingListsToDownResult(event.uid).getOrElse {
            purchasesState.hideShoppingListMenu()
            return@launch
        }

        repository.swapShoppingLists(shoppingList.first, shoppingList.second)
    }

    private fun selectShoppingListsSort() {
        purchasesState.showSort()
    }

    private fun selectShoppingListsDisplayCompleted() {
        purchasesState.showDisplayCompleted()
    }

    private fun selectShoppingListsDisplayTotal() {
        purchasesState.showDisplayTotal()
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

    private fun sortShoppingLists(event: PurchasesEvent.SortShoppingLists) = viewModelScope.launch {
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
        event: PurchasesEvent.DisplayShoppingListsCompleted
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
        event: PurchasesEvent.DisplayShoppingListsTotal
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

    private fun hideShoppingListsSort() {
        purchasesState.hideSort()
    }

    private fun hideShoppingListsDisplayCompleted() {
        purchasesState.hideDisplayCompleted()
    }

    private fun hideShoppingListsDisplayTotal() {
        purchasesState.hideDisplayTotal()
    }

    private fun finishApp() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.FinishApp)
    }
}