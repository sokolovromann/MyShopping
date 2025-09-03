package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.ShoppingPeriod
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.model.ArchiveState
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<ArchiveEvent> {

    val archiveState: ArchiveState = ArchiveState()

    private val _screenEventFlow: MutableSharedFlow<ArchiveScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ArchiveScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: ArchiveEvent) {
        when (event) {
            is ArchiveEvent.OnClickShoppingList -> onClickShoppingList(event)

            ArchiveEvent.OnClickBack -> onClickBack()

            is ArchiveEvent.OnMoveShoppingListSelected -> onMoveShoppingListSelected(event)

            is ArchiveEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is ArchiveEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            ArchiveEvent.OnClickSearchShoppingLists -> onClickSearchShoppingLists()

            is ArchiveEvent.OnSearchValueChanged -> onSearchValueChanged(event)

            ArchiveEvent.OnInvertSearch -> onInvertSearch()

            is ArchiveEvent.OnDisplayProductsSelected -> onDisplayProductsSelected(event)

            is ArchiveEvent.OnSelectDisplayProducts -> onSelectDisplayProducts(event)

            is ArchiveEvent.OnDisplayTotalSelected -> onDisplayTotalSelected(event)

            is ArchiveEvent.OnSelectDisplayTotal -> onSelectDisplayTotal(event)

            is ArchiveEvent.OnSortSelected -> onSortSelected(event)

            ArchiveEvent.OnReverseSort -> onReverseSort()

            is ArchiveEvent.OnSelectSort -> onSelectSort(event)

            ArchiveEvent.OnInvertSortFormatted -> onInvertSortFormatted()

            is ArchiveEvent.OnShowArchiveMenu -> onShowArchiveMenu(event)

            is ArchiveEvent.OnAllShoppingListsSelected -> onAllShoppingListsSelected(event)

            is ArchiveEvent.OnShoppingListSelected -> onShoppingListSelected(event)

            is ArchiveEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)

            is ArchiveEvent.OnSelectArchivePeriod -> onSelectArchivePeriod(event)

            is ArchiveEvent.OnArchivePeriodSelected -> onArchivePeriodSelected(event)

            is ArchiveEvent.OnSelectView -> onSelectView(event)

            is ArchiveEvent.OnViewSelected -> onViewSelected(event)

            is ArchiveEvent.OnSwipeShoppingLeft -> onSwipeShoppingLeft(event)

            is ArchiveEvent.OnSwipeShoppingRight -> onSwipeShoppingRight(event)
        }
    }

    private fun onInit() {
        getArchiveAndPopulate(ShoppingPeriod.DefaultValue)
    }

    private fun getArchiveAndPopulate(period: ShoppingPeriod) = viewModelScope.launch(dispatcher) {
        archiveState.onWaiting()

        shoppingListsRepository.getArchiveWithConfig(period).collectLatest {
            archiveState.populate(it, period)
        }
    }

    private fun onClickShoppingList(
        event: ArchiveEvent.OnClickShoppingList
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnShowProductsScreen(event.uid))
    }

    private fun onClickBack() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnShowBackScreen)
    }

    private fun onMoveShoppingListSelected(
        event: ArchiveEvent.OnMoveShoppingListSelected
    ) = viewModelScope.launch(dispatcher) {
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
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: ArchiveEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(ArchiveScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onClickSearchShoppingLists() {
        archiveState.onSearch()
    }

    private fun onSearchValueChanged(event: ArchiveEvent.OnSearchValueChanged) {
        archiveState.onSearchValueChanged(event.value)
    }

    private fun onInvertSearch() = viewModelScope.launch(dispatcher) {
        val display = !archiveState.displaySearch
        archiveState.onShowSearch(display)

        if (!display) {
            _screenEventFlow.emit(ArchiveScreenEvent.OnHideKeyboard)
        }
    }

    private fun onDisplayProductsSelected(
        event: ArchiveEvent.OnDisplayProductsSelected
    ) = viewModelScope.launch(dispatcher) {
        appConfigRepository.displayShoppingsProducts(event.displayProducts)
        archiveState.onSelectDisplayProducts(expanded = false)
    }

    private fun onSelectDisplayProducts(event: ArchiveEvent.OnSelectDisplayProducts) {
        archiveState.onSelectDisplayProducts(event.expanded)
    }

    private fun onDisplayTotalSelected(
        event: ArchiveEvent.OnDisplayTotalSelected
    ) = viewModelScope.launch(dispatcher) {
        appConfigRepository.displayTotal(event.displayTotal)
        archiveState.onSelectDisplayTotal(expanded = false)
    }

    private fun onSelectDisplayTotal(event: ArchiveEvent.OnSelectDisplayTotal) {
        archiveState.onSelectDisplayTotal(event.expanded)
    }

    private fun onSortSelected(
        event: ArchiveEvent.OnSortSelected
    ) = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.sortShoppingLists(
            sort = Sort(event.sortBy),
            automaticSort = archiveState.sortFormatted
        )
        archiveState.onSelectSort(expanded = false)
    }

    private fun onReverseSort() = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.reverseShoppingLists(
            automaticSort = archiveState.sortFormatted
        )
        archiveState.onSelectSort(expanded = false)
    }

    private fun onSelectSort(event: ArchiveEvent.OnSelectSort) {
        archiveState.onSelectSort(event.expanded)
    }

    private fun onInvertSortFormatted() = viewModelScope.launch(dispatcher) {
        val sort = if (archiveState.sortFormatted) {
            archiveState.sortValue.selected
        } else {
            Sort(SortBy.CREATED)
        }
        shoppingListsRepository.sortShoppingLists(
            sort = sort,
            automaticSort = !archiveState.sortFormatted
        )
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

    private fun onSelectArchivePeriod(event: ArchiveEvent.OnSelectArchivePeriod) {
        archiveState.onSelectArchivePeriod(event.expanded)
    }

    private fun onArchivePeriodSelected(event: ArchiveEvent.OnArchivePeriodSelected) {
        getArchiveAndPopulate(event.period)
    }

    private fun onSelectView(event: ArchiveEvent.OnSelectView) {
        archiveState.onSelectView(event.expanded)
    }

    private fun onViewSelected(
        event: ArchiveEvent.OnViewSelected
    ) = viewModelScope.launch(dispatcher) {
        if (event.multiColumns != archiveState.multiColumnsValue.selected) {
            appConfigRepository.invertShoppingListsMultiColumns()
        }
        archiveState.onSelectView(expanded = false)
    }

    private fun onSwipeShoppingLeft(
        event: ArchiveEvent.OnSwipeShoppingLeft
    ) = viewModelScope.launch(dispatcher) {
        doAfterSwipeShopping(event.uid, archiveState.swipeShoppingLeft)
    }

    private fun onSwipeShoppingRight(
        event: ArchiveEvent.OnSwipeShoppingRight
    ) = viewModelScope.launch(dispatcher) {
        doAfterSwipeShopping(event.uid, archiveState.swipeShoppingRight)
    }

    private suspend fun doAfterSwipeShopping(
        uid: String,
        swipeShopping: SwipeShopping
    ) = withContext(dispatcher) {
        when (swipeShopping) {
            SwipeShopping.DISABLED -> {}
            SwipeShopping.ARCHIVE -> {
                shoppingListsRepository.moveShoppingListToPurchases(uid)
            }
            SwipeShopping.DELETE -> {
                shoppingListsRepository.moveShoppingListToTrash(uid)
            }
            SwipeShopping.DELETE_PRODUCTS -> {
                shoppingListsRepository.deleteProductsByShoppingUid(uid)
            }
            SwipeShopping.COMPLETE -> {
                archiveState.isShoppingListCompleted(uid)?.let { completed ->
                    invertShoppingStatus(uid, completed).let {
                        doAfterShoppingCompleted(uid)
                    }
                }
            }
        }
    }

    private suspend fun invertShoppingStatus(
        uid: String,
        completed: Boolean
    ) = withContext(dispatcher) {
        if (completed) {
            shoppingListsRepository.activeProducts(uid)
        } else {
            shoppingListsRepository.completeProducts(uid)
        }
    }

    private suspend fun doAfterShoppingCompleted(uid: String) = withContext(dispatcher) {
        when (archiveState.getAfterShoppingCompleted()) {
            AfterShoppingCompleted.NOTHING -> {}
            AfterShoppingCompleted.ARCHIVE -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.moveShoppingListToPurchases(uid)
                }
            }
            AfterShoppingCompleted.DELETE -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.moveShoppingListToTrash(uid)
                }
            }
            AfterShoppingCompleted.DELETE_PRODUCTS -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.deleteProductsByStatus(uid, DisplayTotal.ALL)
                }
            }
            AfterShoppingCompleted.DELETE_LIST_AND_PRODUCTS -> {
                if (shoppingListsRepository.isShoppingListCompleted(uid)) {
                    shoppingListsRepository.moveShoppingListToTrash(uid)
                    shoppingListsRepository.deleteProductsByStatus(uid, DisplayTotal.ALL)
                }
            }
        }
    }
}