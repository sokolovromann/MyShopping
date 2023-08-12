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
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val repository: TrashRepository,
    private val dispatchers: AppDispatchers,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<TrashEvent> {

    val trashState: TrashState = TrashState()

    private val _screenEventFlow: MutableSharedFlow<TrashScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<TrashScreenEvent> = _screenEventFlow

    init {
        getShoppingLists()
    }

    override fun onEvent(event: TrashEvent) {
        when (event) {
            TrashEvent.MoveShoppingListsToPurchases -> moveShoppingListsToPurchases()

            TrashEvent.MoveShoppingListsToArchive -> moveShoppingListsToArchive()

            TrashEvent.DeleteShoppingLists -> deleteShoppingLists()

            TrashEvent.EmptyTrash -> emptyTrash()

            TrashEvent.SelectDisplayPurchasesTotal -> selectDisplayPurchasesTotal()

            is TrashEvent.SelectNavigationItem -> selectNavigationItem(event)

            is TrashEvent.SelectShoppingList -> selectShoppingList(event)

            TrashEvent.SelectAllShoppingLists -> selectAllShoppingLists()

            is TrashEvent.UnselectShoppingList -> unselectShoppingList(event)

            is TrashEvent.DisplayPurchasesTotal -> displayPurchasesTotal(event)

            TrashEvent.CancelSelectingShoppingLists -> cancelSelectingShoppingLists()

            TrashEvent.ShowBackScreen -> showBackScreen()

            is TrashEvent.ShowProducts -> showProducts(event)

            TrashEvent.ShowNavigationDrawer -> showNavigationDrawer()

            TrashEvent.HideNavigationDrawer -> hideNavigationDrawer()

            TrashEvent.HideDisplayPurchasesTotal -> hideDisplayPurchasesTotal()
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
            trashState.showNotFound(shoppingLists.appConfig)
        } else {
            trashState.showShoppingLists(shoppingLists)
        }
    }

    private fun deleteShoppingLists() = viewModelScope.launch {
        val uids = trashState.screenData.selectedUids
        uids?.let { repository.deleteShoppingLists(it) }

        withContext(dispatchers.main) {
            uids?.forEach { uid -> alarmManager.deleteReminder(uid) }
            unselectAllShoppingLists()
        }
    }

    private fun emptyTrash() = viewModelScope.launch {
        val uids = trashState.screenData.shoppingLists.map { it.uid }
        repository.deleteShoppingLists(uids)

        withContext(dispatchers.main) {
            uids.forEach { alarmManager.deleteReminder(it) }
        }
    }

    private fun moveShoppingListsToPurchases() = viewModelScope.launch {
        trashState.screenData.selectedUids?.forEach {
            repository.moveShoppingListToPurchases(
                uid = it,
                lastModified = System.currentTimeMillis()
            )
        }

        withContext(dispatchers.main) {
            unselectAllShoppingLists()
        }
    }

    private fun moveShoppingListsToArchive() = viewModelScope.launch {
        trashState.screenData.selectedUids?.forEach {
            repository.moveShoppingListToArchive(
                uid = it,
                lastModified = System.currentTimeMillis()
            )
        }

        withContext(dispatchers.main) {
            unselectAllShoppingLists()
        }
    }

    private fun selectDisplayPurchasesTotal() {
        trashState.selectDisplayPurchasesTotal()
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

    private fun selectShoppingList(event: TrashEvent.SelectShoppingList) {
        trashState.selectShoppingList(event.uid)
    }

    private fun selectAllShoppingLists() {
        trashState.selectAllShoppingLists()
    }

    private fun unselectShoppingList(event: TrashEvent.UnselectShoppingList) {
        trashState.unselectShoppingList(event.uid)
    }

    private fun unselectAllShoppingLists() {
        trashState.unselectAllShoppingLists()
    }

    private fun cancelSelectingShoppingLists() {
        unselectAllShoppingLists()
    }

    private fun displayPurchasesTotal(
        event: TrashEvent.DisplayPurchasesTotal
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

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.ShowBackScreen)
    }

    private fun showProducts(event: TrashEvent.ShowProducts) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.ShowProducts(event.uid))
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.ShowNavigationDrawer)
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.HideNavigationDrawer)
    }

    private fun hideDisplayPurchasesTotal() {
        trashState.hideDisplayPurchasesTotal()
    }
}