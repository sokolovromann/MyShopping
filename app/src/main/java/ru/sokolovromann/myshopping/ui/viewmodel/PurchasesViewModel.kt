package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import ru.sokolovromann.myshopping.data.repository.PurchasesRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: PurchasesRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<PurchasesEvent> {

    val purchasesState: ListState<ShoppingListItem> = ListState()

    val itemMenuState: ItemMenuState<PurchasesItemMenu> = ItemMenuState()

    val sortState: MenuButtonState<ShoppingListsSortMenu> = MenuButtonState()

    private val _sortAscendingState: MutableState<IconData> = mutableStateOf(IconData())
    val sortAscendingState: State<IconData> = _sortAscendingState

    val totalState: MenuButtonState<ShoppingListsTotalMenu> = MenuButtonState()

    val completedState: MenuIconButtonState<ShoppingListsCompletedMenu> = MenuIconButtonState()

    private val _floatingActionButtonState: MutableState<FloatingActionButtonData> = mutableStateOf(
        FloatingActionButtonData()
    )
    val floatingActionButtonState: State<FloatingActionButtonData> = _floatingActionButtonState

    private val _navigationDrawerState: MutableState<NavigationDrawerData> = mutableStateOf(
        NavigationDrawerData()
    )
    val navigationDrawerState: State<NavigationDrawerData> = _navigationDrawerState

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _bottomBarState: MutableState<BottomBarData> = mutableStateOf(BottomBarData())
    val bottomBarState: State<BottomBarData> = _bottomBarState

    private val _systemUiState: MutableState<SystemUiData> = mutableStateOf(SystemUiData())
    val systemUiState: State<SystemUiData> = _systemUiState

    private val _screenEventFlow: MutableSharedFlow<PurchasesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<PurchasesScreenEvent> = _screenEventFlow

    init {
        showFloatingActionButton()
        showTopBar()
        showBottomBar()

        getShoppingList()
    }

    override fun onEvent(event: PurchasesEvent) {
        when (event) {
            PurchasesEvent.AddShoppingList -> addShoppingList()

            is PurchasesEvent.MoveShoppingListToArchive -> moveShoppingListToArchive(event)

            is PurchasesEvent.MoveShoppingListToTrash -> moveShoppingListToTrash(event)

            PurchasesEvent.SelectShoppingListsSort -> selectShoppingListsSort()

            PurchasesEvent.SelectShoppingListsDisplayCompleted -> selectShoppingListsDisplayCompleted()

            PurchasesEvent.SelectShoppingListsDisplayTotal -> selectShoppingListsDisplayTotal()

            is PurchasesEvent.SelectNavigationItem -> selectNavigationItem(event)

            PurchasesEvent.SortShoppingListsByCreated -> sortShoppingListsByCreated()

            PurchasesEvent.SortShoppingListsByLastModified -> sortShoppingListsByLastModified()

            PurchasesEvent.SortShoppingListsByName -> sortShoppingListsByName()

            PurchasesEvent.SortShoppingListsByTotal -> sortShoppingListsByTotal()

            PurchasesEvent.DisplayShoppingListsCompletedFirst -> displayShoppingListsCompletedFirst()

            PurchasesEvent.DisplayShoppingListsCompletedLast -> displayShoppingListsCompletedLast()

            PurchasesEvent.DisplayShoppingListsAllTotal -> displayShoppingListsAllTotal()

            PurchasesEvent.DisplayShoppingListsCompletedTotal -> displayShoppingListsCompletedTotal()

            PurchasesEvent.DisplayShoppingListsActiveTotal -> displayShoppingListsActiveTotal()

            PurchasesEvent.InvertShoppingListsSort -> invertShoppingListsSort()

            is PurchasesEvent.ShowProducts -> showProducts(event)

            PurchasesEvent.ShowNavigationDrawer -> showNavigationDrawer()

            is PurchasesEvent.ShowShoppingListMenu -> showShoppingListMenu(event)

            PurchasesEvent.HideShoppingListsCompleted -> hideShoppingListsCompleted()

            PurchasesEvent.HideNavigationDrawer -> hideNavigationDrawer()

            PurchasesEvent.HideShoppingListMenu -> hideShoppingListMenu()

            PurchasesEvent.HideShoppingListsSort -> hideShoppingListsSort()

            PurchasesEvent.HideShoppingListsDisplayCompleted -> hideShoppingListsDisplayCompleted()

            PurchasesEvent.HideShoppingListsDisplayTotal -> hideShoppingListsDisplayTotal()

            PurchasesEvent.FinishApp -> finishApp()
        }
    }

    private fun getShoppingList() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getShoppingLists().collect {
            showShoppingLists(it)
        }
    }

    private fun addShoppingList() = viewModelScope.launch(dispatchers.io) {
        val shoppingList = ShoppingList()
        repository.addShoppingList(shoppingList)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(PurchasesScreenEvent.ShowProducts(shoppingList.uid))
        }
    }

    private fun moveShoppingListToArchive(
        event: PurchasesEvent.MoveShoppingListToArchive
    ) = viewModelScope.launch(dispatchers.io) {
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
    ) = viewModelScope.launch(dispatchers.io) {
        repository.moveShoppingListToTrash(
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
        purchasesState.showLoading()
    }

    private suspend fun showShoppingLists(shoppingLists: ShoppingLists) = withContext(dispatchers.main) {
        val items = shoppingLists.sortShoppingLists()
        val preferences = shoppingLists.preferences

        if (items.isEmpty()) {
            showShoppingListNotFound(preferences)
        } else {
            val list = items.map { mapping.toShoppingListItem(it, preferences) }
            purchasesState.showList(list, preferences.multiColumns)
        }

        showSort(preferences)
        showSortAscending(preferences)

        showCompleted(preferences)

        if (preferences.displayMoney) {
            showTotal(shoppingLists.calculateTotal(), preferences)
        } else {
            hideTotal()
        }

        showNavigationDrawerData(preferences)

        itemMenuState.setMenu(mapping.toPurchasesItemMenu(preferences.fontSize))
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
            text = UiText.FromResources(R.string.purchases_shoppingListsNotFound),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnBackground
        )
        purchasesState.showNotFound(data)
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

    private fun showProducts(event: PurchasesEvent.ShowProducts) = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.ShowProducts(event.uid))
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.ShowNavigationDrawer)
    }

    private fun showShoppingListMenu(event: PurchasesEvent.ShowShoppingListMenu) {
        itemMenuState.showMenu(itemUid = event.uid)
    }

    private fun showNavigationDrawerData(preferences: ShoppingListPreferences) {
        val data = NavigationDrawerData(
            header = mapping.toOnNavigationDrawerHeader(
                text = mapping.toResourcesUiText(R.string.route_header),
                fontSize = preferences.fontSize
            ),
            items = mapping.toNavigationDrawerItems(
                checked = UiRoute.Purchases
            )
        )
        _navigationDrawerState.value = data
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.purchases_purchasesName),
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

    private fun showFloatingActionButton() {
        val data = FloatingActionButtonData(
            icon = IconData(
                icon = UiIcon.FromVector(Icons.Default.Add),
                tint = ColorData(appColor = AppColor.OnSecondary)
            )
        )
        _floatingActionButtonState.value = data
    }

    private fun hideShoppingListsCompleted() = viewModelScope.launch(dispatchers.io) {
        repository.hideShoppingListsCompleted()

        withContext(dispatchers.main) {
            hideShoppingListsDisplayCompleted()
        }
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.HideNavigationDrawer)
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

    private fun finishApp() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(PurchasesScreenEvent.FinishApp)
    }
}