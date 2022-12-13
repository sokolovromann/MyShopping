package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.MoveProductRepository
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListPreferences
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent
import javax.inject.Inject

@HiltViewModel
class MoveProductViewModel @Inject constructor(
    private val repository: MoveProductRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<MoveProductEvent> {

    private val productState: MutableState<Product> = mutableStateOf(Product())

    val shoppingListsState: ListState<ShoppingListItem> = ListState()

    private val preferencesState: MutableState<ShoppingListPreferences> = mutableStateOf(
        ShoppingListPreferences()
    )

    val locationButtonState: MenuButtonState<ShoppingListsLocationMenu> = MenuButtonState()

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _systemUiState: MutableState<SystemUiData> = mutableStateOf(SystemUiData())
    val systemUiState: State<SystemUiData> = _systemUiState

    private val _screenEventFlow: MutableSharedFlow<MoveProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MoveProductScreenEvent> = _screenEventFlow

    private val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""

    init {
        showTopBar()
        getPurchases()
    }

    override fun onEvent(event: MoveProductEvent) {
        when (event) {
            is MoveProductEvent.MoveProduct -> moveProduct(event)

            MoveProductEvent.SelectShoppingListsLocation -> selectShoppingListsLocation()

            MoveProductEvent.DisplayShoppingListsPurchases -> displayShoppingListsPurchases()

            MoveProductEvent.DisplayShoppingListsArchive -> displayShoppingListsArchive()

            MoveProductEvent.DisplayShoppingListsTrash -> displayShoppingListsTrash()

            MoveProductEvent.ShowBackScreen -> showBackScreen()

            MoveProductEvent.HideShoppingListsLocation -> hideShoppingListsLocation()
        }
    }

    private fun getPurchases() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getPurchases().collect {
            showShoppingLists(
                archived = false,
                deleted = false,
                shoppingLists = it
            )
        }
    }

    private fun getArchive() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getArchive().collect {
            showShoppingLists(
                archived = true,
                deleted = false,
                shoppingLists = it
            )
        }
    }

    private fun getTrash() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getTrash().collect {
            showShoppingLists(
                archived = false,
                deleted = true,
                shoppingLists = it
            )
        }
    }

    private fun moveProduct(event: MoveProductEvent.MoveProduct) = viewModelScope.launch(dispatchers.io) {
        repository.moveProduct(
            productUid = productUid,
            shoppingUid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        showBackScreen()
    }

    private fun selectShoppingListsLocation() {
        locationButtonState.showMenu()
    }

    private fun displayShoppingListsPurchases() {
        showLocation(
            archived = false,
            deleted = false,
            preferences = preferencesState.value
        )
        hideShoppingListsLocation()

        getPurchases()
    }

    private fun displayShoppingListsArchive() {
        showLocation(
            archived = true,
            deleted = false,
            preferences = preferencesState.value
        )
        hideShoppingListsLocation()

        getArchive()
    }

    private fun displayShoppingListsTrash() {
        showLocation(
            archived = false,
            deleted = true,
            preferences = preferencesState.value
        )
        hideShoppingListsLocation()

        getTrash()
    }

    private suspend fun showShoppingListsLoading() = withContext(dispatchers.main) {
        shoppingListsState.showLoading()
    }

    private suspend fun showShoppingLists(
        archived: Boolean,
        deleted: Boolean,
        shoppingLists: ShoppingLists
    ) = withContext(dispatchers.main) {
        val items = shoppingLists.sortShoppingLists()
        preferencesState.value = shoppingLists.preferences

        if (items.isEmpty()) {
            showShoppingListNotFound(preferencesState.value)
        } else {
            val list = items.map { mapping.toShoppingListItem(it, preferencesState.value) }
            shoppingListsState.showList(list, preferencesState.value.multiColumns)
        }

        showLocation(
            archived = archived,
            deleted = deleted,
            preferences = preferencesState.value
        )
    }

    private fun showShoppingListNotFound(preferences: ShoppingListPreferences) {
        val data = mapping.toTitle(
            text = UiText.FromResources(R.string.moveProduct_shoppingListsNotFound),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnBackground
        )
        shoppingListsState.showNotFound(data)
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.moveProduct_copyProductName),
                fontSize = FontSize.MEDIUM
            ),
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.Close)
            )
        )
        _topBarState.value = data
    }

    private fun showLocation(
        archived: Boolean,
        deleted: Boolean,
        preferences: ShoppingListPreferences
    ) {
        locationButtonState.showButton(
            text = mapping.toShoppingListsLocationBody(
                archived = archived,
                deleted = deleted,
                fontSize = preferences.fontSize
            ),
            menu = mapping.toShoppingListsLocationMenu(
                archived = archived,
                deleted = deleted,
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MoveProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        locationButtonState.hideMenu()
    }
}