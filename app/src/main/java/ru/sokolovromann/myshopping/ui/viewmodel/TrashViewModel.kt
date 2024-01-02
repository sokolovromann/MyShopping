package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.model.TrashState
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<TrashEvent> {

    val trashState: TrashState = TrashState()

    private val _screenEventFlow: MutableSharedFlow<TrashScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<TrashScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: TrashEvent) {
        when (event) {
            is TrashEvent.OnClickShoppingList -> onClickShoppingList(event)

            TrashEvent.OnClickBack -> onClickBack()

            is TrashEvent.OnMoveShoppingListSelected -> onMoveShoppingListSelected(event)

            TrashEvent.OnClickDeleteShoppingLists -> onClickDeleteShoppingLists()

            TrashEvent.OnClickEmptyTrash -> onClickEmptyTrash()

            is TrashEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is TrashEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            is TrashEvent.OnAllShoppingListsSelected -> onAllShoppingListsSelected(event)

            is TrashEvent.OnShoppingListSelected -> onShoppingListSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        trashState.onWaiting()

        shoppingListsRepository.getTrashWithConfig().collect {
            trashState.populate(it)
        }
    }

    private fun onClickShoppingList(
        event: TrashEvent.OnClickShoppingList
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(TrashScreenEvent.OnShowProductsScreen(event.uid))
    }

    private fun onClickBack() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(TrashScreenEvent.OnShowBackScreen)
    }

    private fun onMoveShoppingListSelected(
        event: TrashEvent.OnMoveShoppingListSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        trashState.selectedUids?.let {
            when (event.location) {
                ShoppingLocation.PURCHASES -> shoppingListsRepository.moveShoppingListsToPurchases(it)
                ShoppingLocation.ARCHIVE -> shoppingListsRepository.moveShoppingListsToArchive(it)
                ShoppingLocation.TRASH -> shoppingListsRepository.moveShoppingListsToTrash(it)
            }
            trashState.onAllShoppingListsSelected(selected = false)
        }
    }

    private fun onClickDeleteShoppingLists() = viewModelScope.launch(AppDispatchers.Main) {
        trashState.selectedUids?.let {
            shoppingListsRepository.deleteShoppingLists(it)
            alarmManager.deleteReminders(it)
            trashState.onAllShoppingListsSelected(selected = false)
        }
    }

    private fun onClickEmptyTrash() = viewModelScope.launch(AppDispatchers.Main) {
        val uids = trashState.shoppingLists.map { it.uid }
        shoppingListsRepository.deleteShoppingLists(uids)
        alarmManager.deleteReminders(uids)
        trashState.onAllShoppingListsSelected(selected = false)
    }

    private fun onDrawerScreenSelected(
        event: TrashEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(TrashScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: TrashEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(TrashScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onAllShoppingListsSelected(event: TrashEvent.OnAllShoppingListsSelected) {
        trashState.onAllShoppingListsSelected(event.selected)
    }

    private fun onShoppingListSelected(event: TrashEvent.OnShoppingListSelected) {
        trashState.onShoppingListSelected(event.selected, event.uid)
    }
}