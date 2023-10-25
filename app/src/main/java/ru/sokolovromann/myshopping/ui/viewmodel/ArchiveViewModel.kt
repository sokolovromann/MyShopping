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
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository,
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

            ArchiveEvent.SelectAllShoppingLists -> selectAllShoppingLists()

            is ArchiveEvent.UnselectShoppingList -> unselectShoppingList(event)

            ArchiveEvent.CancelSelectingShoppingLists -> cancelSelectingShoppingLists()

            is ArchiveEvent.SortShoppingLists -> sortShoppingLists(event)

            ArchiveEvent.ReverseSortShoppingLists -> reverseSortShoppingLists()

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

            ArchiveEvent.InvertShoppingsMultiColumns -> invertShoppingListsMultiColumns()
        }
    }

    private fun getShoppingLists() = viewModelScope.launch {
        withContext(dispatchers.main) {
            archiveState.showLoading()
        }

        shoppingListsRepository.getArchiveWithConfig().collect {
            shoppingListsLoaded(it)
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingListsWithConfig: ShoppingListsWithConfig
    ) = withContext(dispatchers.main) {
        val shoppingLists = ShoppingListsMapper.toShoppingLists(shoppingListsWithConfig)
        if (shoppingListsWithConfig.isEmpty()) {
            archiveState.showNotFound(shoppingLists)
        } else {
            archiveState.showShoppingLists(shoppingLists)
        }
    }

    private fun moveShoppingListsToPurchases() = viewModelScope.launch {
        archiveState.screenData.selectedUids?.let {
            shoppingListsRepository.moveShoppingListsToPurchases(it)

            withContext(dispatchers.main) {
                unselectAllShoppingLists()
            }
        }
    }

    private fun moveShoppingListsToTrash() = viewModelScope.launch {
        archiveState.screenData.selectedUids?.let {
            shoppingListsRepository.moveShoppingListsToTrash(it)

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

    private fun selectAllShoppingLists() {
        archiveState.selectAllShoppingLists()
    }

    private fun unselectShoppingList(event: ArchiveEvent.UnselectShoppingList) {
        archiveState.unselectShoppingList(event.uid)
    }

    private fun unselectAllShoppingLists() {
        archiveState.unselectAllShoppingLists()
    }

    private fun cancelSelectingShoppingLists() {
        unselectAllShoppingLists()
    }

    private fun sortShoppingLists(event: ArchiveEvent.SortShoppingLists) = viewModelScope.launch {
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
        event: ArchiveEvent.DisplayPurchasesTotal
    ) = viewModelScope.launch {
        appConfigRepository.displayTotal(event.displayTotal)

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

    private fun invertShoppingListsMultiColumns() = viewModelScope.launch {
        appConfigRepository.invertShoppingListsMultiColumns()
    }
}