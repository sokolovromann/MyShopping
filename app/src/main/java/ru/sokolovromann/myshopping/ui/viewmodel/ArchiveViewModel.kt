package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.model.ArchiveState
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<ArchiveEvent> {

    val archiveState: ArchiveState = ArchiveState()

    private val _screenEventFlow: MutableSharedFlow<ArchiveScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ArchiveScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: ArchiveEvent) {
        when (event) {
            is ArchiveEvent.OnClickShoppingList -> onClickShoppingList(event)

            ArchiveEvent.OnClickBack -> onClickBack()

            is ArchiveEvent.OnMoveShoppingListSelected -> onMoveShoppingListSelected(event)

            is ArchiveEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is ArchiveEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            is ArchiveEvent.OnDisplayTotalSelected -> onDisplayTotalSelected(event)

            is ArchiveEvent.OnSelectDisplayTotal -> onSelectDisplayTotal(event)

            is ArchiveEvent.OnSortSelected -> onSortSelected(event)

            ArchiveEvent.OnReverseSort -> onReverseSort()

            is ArchiveEvent.OnSelectSort -> onSelectSort(event)

            is ArchiveEvent.OnShowArchiveMenu -> onShowArchiveMenu(event)

            is ArchiveEvent.OnAllShoppingListsSelected -> onAllShoppingListsSelected(event)

            is ArchiveEvent.OnShoppingListSelected -> onShoppingListSelected(event)

            is ArchiveEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)

            ArchiveEvent.OnInvertMultiColumns -> onInvertMultiColumns()
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        archiveState.onWaiting()

        shoppingListsRepository.getArchiveWithConfig().collect {
            archiveState.populate(it)
        }
    }

    private fun onClickShoppingList(
        event: ArchiveEvent.OnClickShoppingList
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnShowProductsScreen(event.uid))
    }

    private fun onClickBack() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnShowBackScreen)
    }

    private fun onMoveShoppingListSelected(
        event: ArchiveEvent.OnMoveShoppingListSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        archiveState.selectedUids?.let {
            when (event.location) {
                ShoppingLocation.PURCHASES -> shoppingListsRepository.moveShoppingListsToPurchases(it)
                ShoppingLocation.ARCHIVE -> shoppingListsRepository.moveShoppingListsToArchive(it)
                ShoppingLocation.TRASH -> shoppingListsRepository.moveShoppingListsToTrash(it)
            }
            archiveState.onAllShoppingListsSelected(selected = false)
        }
    }

    private fun onDrawerScreenSelected(
        event: ArchiveEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: ArchiveEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onDisplayTotalSelected(
        event: ArchiveEvent.OnDisplayTotalSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.displayTotal(event.displayTotal)
        archiveState.onSelectDisplayTotal(expanded = false)
    }

    private fun onSelectDisplayTotal(event: ArchiveEvent.OnSelectDisplayTotal) {
        archiveState.onSelectDisplayTotal(event.expanded)
    }

    private fun onSortSelected(
        event: ArchiveEvent.OnSortSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.sortShoppingLists(sort = Sort(event.sortBy))
        archiveState.onSelectSort(expanded = false)
    }

    private fun onReverseSort() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.reverseShoppingLists()
        archiveState.onSelectSort(expanded = false)
    }

    private fun onSelectSort(event: ArchiveEvent.OnSelectSort) {
        archiveState.onSelectSort(event.expanded)
    }

    private fun onShowArchiveMenu(event: ArchiveEvent.OnShowArchiveMenu) {
        archiveState.onShowArchiveMenu(event.expanded)
    }

    private fun onAllShoppingListsSelected(event: ArchiveEvent.OnAllShoppingListsSelected) {
        archiveState.onAllShoppingListsSelected(event.selected)
    }

    private fun onShoppingListSelected(event: ArchiveEvent.OnShoppingListSelected) {
        archiveState.onShoppingListSelected(event.selected, event.uid)
    }

    private fun onShowHiddenShoppingLists(event: ArchiveEvent.OnShowHiddenShoppingLists) {
        archiveState.onShowHiddenShoppingLists(event.display)
    }

    private fun onInvertMultiColumns() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.invertShoppingListsMultiColumns()
    }
}