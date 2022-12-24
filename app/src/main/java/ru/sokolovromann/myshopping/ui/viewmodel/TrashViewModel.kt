package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.TrashRepository
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListPreferences
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val repository: TrashRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<TrashEvent> {

    val trashState: ListState<ShoppingListItem> = ListState()

    val itemMenuState: ItemMenuState<TrashItemMenu> = ItemMenuState()

    val sortState: MenuButtonState<ShoppingListsSortMenu> = MenuButtonState()

    private val _sortAscendingState: MutableState<IconData> = mutableStateOf(IconData())
    val sortAscendingState: State<IconData> = _sortAscendingState

    val totalState: MenuButtonState<ShoppingListsTotalMenu> = MenuButtonState()

    val completedState: MenuIconButtonState<ShoppingListsCompletedMenu> = MenuIconButtonState()

    private val _clearState: MutableState<TextData> = mutableStateOf(TextData())
    val clearState: State<TextData> = _clearState

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _bottomBarState: MutableState<BottomBarData> = mutableStateOf(BottomBarData())
    val bottomBarState: State<BottomBarData> = _bottomBarState

    private val _screenEventFlow: MutableSharedFlow<TrashScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<TrashScreenEvent> = _screenEventFlow

    init {
        showTopBar()
        showBottomBar()

        getShoppingList()
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

            TrashEvent.SortShoppingListsByCreated -> sortShoppingListsByCreated()

            TrashEvent.SortShoppingListsByLastModified -> sortShoppingListsByLastModified()

            TrashEvent.SortShoppingListsByName -> sortShoppingListsByName()

            TrashEvent.SortShoppingListsByTotal -> sortShoppingListsByTotal()

            TrashEvent.DisplayShoppingListsCompletedFirst -> displayShoppingListsCompletedFirst()

            TrashEvent.DisplayShoppingListsCompletedLast -> displayShoppingListsCompletedLast()

            TrashEvent.DisplayShoppingListsAllTotal -> displayShoppingListsAllTotal()

            TrashEvent.DisplayShoppingListsCompletedTotal -> displayShoppingListsCompletedTotal()

            TrashEvent.DisplayShoppingListsActiveTotal -> displayShoppingListsActiveTotal()

            TrashEvent.InvertShoppingListsSort -> invertShoppingListsSort()

            TrashEvent.ShowBackScreen -> showBackScreen()

            is TrashEvent.ShowProducts -> showProducts(event)

            TrashEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is TrashEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            TrashEvent.HideShoppingListsCompleted -> hideShoppingListsCompleted()

            TrashEvent.HideNavigationDrawer -> hideNavigationDrawer()

            TrashEvent.HideShoppingListMenu -> hideShoppingListMenu()

            TrashEvent.HideShoppingListsSort -> hideShoppingListsSort()

            TrashEvent.HideShoppingListsDisplayCompleted -> hideShoppingListsDisplayCompleted()

            TrashEvent.HideShoppingListsDisplayTotal -> hideShoppingListsDisplayTotal()
        }
    }

    private fun getShoppingList() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getShoppingLists().collect {
            showShoppingLists(it)
        }
    }

    private fun deleteShoppingLists() = viewModelScope.launch(dispatchers.io) {
        repository.deleteShoppingLists()
    }

    private fun deleteShoppingList(
        event: TrashEvent.DeleteShoppingList
    ) = viewModelScope.launch(dispatchers.io) {
        repository.deleteShoppingList(event.uid)

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun moveShoppingListToPurchases(
        event: TrashEvent.MoveShoppingListToPurchases
    ) = viewModelScope.launch(dispatchers.io) {
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
    ) = viewModelScope.launch(dispatchers.io) {
        repository.moveShoppingListToArchive(
            uid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        withContext(dispatchers.main) {
            hideShoppingListMenu()
        }
    }

    private fun selectShoppingListsSort() {
        sortState.showMenu()
    }

    private fun selectShoppingListsDisplayCompleted() {
        completedState.showMenu()
    }

    private fun selectShoppingListsDisplayTotal() {
        totalState.showMenu()
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

    private fun sortShoppingListsByCreated() = viewModelScope.launch(dispatchers.io) {
        repository.sortShoppingListsByCreated()

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun sortShoppingListsByLastModified() = viewModelScope.launch(dispatchers.io) {
        repository.sortShoppingListsByLastModified()

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun sortShoppingListsByName() = viewModelScope.launch(dispatchers.io) {
        repository.sortShoppingListsByName()

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun sortShoppingListsByTotal() = viewModelScope.launch(dispatchers.io) {
        repository.sortShoppingListsByTotal()

        withContext(dispatchers.main) {
            hideShoppingListsSort()
        }
    }

    private fun displayShoppingListsCompletedFirst() = viewModelScope.launch(dispatchers.io) {
        repository.displayShoppingListsCompletedFirst()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayCompleted()
        }
    }

    private fun displayShoppingListsCompletedLast() = viewModelScope.launch(dispatchers.io) {
        repository.displayShoppingListsCompletedLast()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayCompleted()
        }
    }

    private fun displayShoppingListsAllTotal() = viewModelScope.launch(dispatchers.io) {
        repository.displayShoppingListsAllTotal()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayTotal()
        }
    }

    private fun displayShoppingListsCompletedTotal() = viewModelScope.launch(dispatchers.io) {
        repository.displayShoppingListsCompletedTotal()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayTotal()
        }
    }

    private fun displayShoppingListsActiveTotal() = viewModelScope.launch(dispatchers.io) {
        repository.displayShoppingListsActiveTotal()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayTotal()
        }
    }

    private fun invertShoppingListsSort() = viewModelScope.launch(dispatchers.io) {
        repository.invertShoppingListsSort()
    }

    private suspend fun showShoppingListsLoading() = withContext(dispatchers.main) {
        trashState.showLoading()
    }

    private suspend fun showShoppingLists(shoppingLists: ShoppingLists) = withContext(dispatchers.main) {
        val items = shoppingLists.sortShoppingLists()
        val preferences = shoppingLists.preferences

        if (items.isEmpty()) {
            showShoppingListNotFound(preferences)
        } else {
            val list = items.map { mapping.toShoppingListItem(it, preferences) }
            trashState.showList(list, preferences.multiColumns)
        }

        showSort(preferences)
        showSortAscending(preferences)

        showCompleted(preferences)

        if (preferences.displayMoney) {
            showTotal(shoppingLists.calculateTotal(), preferences)
        } else {
            hideTotal()
        }

        showClear(preferences)

        itemMenuState.setMenu(mapping.toTrashItemMenu(preferences.fontSize))
    }

    private fun showSort(preferences: ShoppingListPreferences) {
        sortState.showButton(
            text = mapping.toShoppingListsSortBody(
                sortBy = preferences.sort.sortBy,
                fontSize = preferences.fontSize
            ),
            menu = mapping.toShoppingListsSortMenu(
                sortBy = preferences.sort.sortBy,
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showSortAscending(preferences: ShoppingListPreferences) {
        _sortAscendingState.value = mapping.toSortAscendingIconBody(
            ascending = preferences.sort.ascending,
        )
    }

    private fun showShoppingListNotFound(preferences: ShoppingListPreferences) {
        val data = mapping.toTitle(
            text = UiText.FromResources(R.string.trash_shoppingListsNotFound),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnBackground
        )
        trashState.showNotFound(data)
    }

    private fun showCompleted(preferences: ShoppingListPreferences) {
        completedState.showButton(
            icon = mapping.toDisplayCompletedIconBody(),
            menu = mapping.toShoppingListsCompletedMenu(
                displayCompleted = preferences.displayCompleted,
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showTotal(total: Money, preferences: ShoppingListPreferences) {
        totalState.showButton(
            text = mapping.toShoppingListsTotalTitle(
                total = total,
                displayTotal = preferences.displayTotal,
                fontSize = preferences.fontSize
            ),
            menu = mapping.toShoppingListsTotalMenu(
                displayTotal = preferences.displayTotal,
                fontSize = preferences.fontSize
            )
        )
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
        itemMenuState.showMenu(itemUid = event.uid)
    }

    private fun showClear(preferences: ShoppingListPreferences) {
        val data = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.trash_action_deleteShoppingLists),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnPrimary
        )
        _clearState.value = data
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.trash_header),
                fontSize = FontSize.MEDIUM
            ),
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.Menu)
            )
        )
        _topBarState.value = data
    }

    private fun showBottomBar() {
        val data = BottomBarData()
        _bottomBarState.value = data
    }

    private fun hideShoppingListsCompleted() = viewModelScope.launch(dispatchers.io) {
        repository.hideShoppingListsCompleted()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayCompleted()
        }
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(TrashScreenEvent.HideNavigationDrawer)
    }

    private fun hideShoppingListMenu() {
        itemMenuState.hideMenu()
    }

    private fun hideShoppingListsSort() {
        sortState.hideMenu()
    }

    private fun hideShoppingListsDisplayCompleted() {
        completedState.hideMenu()
    }

    private fun hideShoppingListsDisplayTotal() {
        totalState.hideMenu()
    }

    private fun hideTotal() {
        totalState.hideButton()
    }
}