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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.CopyProductRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CopyProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent
import javax.inject.Inject

@HiltViewModel
class CopyProductViewModel @Inject constructor(
    private val repository: CopyProductRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CopyProductEvent> {

    private val productState: MutableState<Product> = mutableStateOf(Product())

    val shoppingListsState: ListState<ShoppingListItem> = ListState()

    private val preferencesState: MutableState<ShoppingListPreferences> = mutableStateOf(
        ShoppingListPreferences()
    )

    val locationButtonState: MenuButtonState<ShoppingListsLocationMenu> = MenuButtonState()

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _screenEventFlow: MutableSharedFlow<CopyProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CopyProductScreenEvent> = _screenEventFlow

    private val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""

    init {
        showTopBar()

        getPurchases()
        getProduct()
    }

    override fun onEvent(event: CopyProductEvent) {
        when (event) {
            is CopyProductEvent.CopyProduct -> copyProduct(event)

            CopyProductEvent.SelectShoppingListsLocation -> selectShoppingListsLocation()

            CopyProductEvent.DisplayShoppingListsPurchases -> displayShoppingListsPurchases()

            CopyProductEvent.DisplayShoppingListsArchive -> displayShoppingListsArchive()

            CopyProductEvent.DisplayShoppingListsTrash -> displayShoppingListsTrash()

            CopyProductEvent.ShowBackScreen -> showBackScreen()

            CopyProductEvent.HideShoppingListsLocation -> hideShoppingListsLocation()
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

    private fun getProduct() = viewModelScope.launch(dispatchers.io) {
        val product = repository.getProduct(productUid).firstOrNull()

        if (product == null) {
            showBackScreen()
        } else {
            withContext(dispatchers.main) { productState.value = product }
        }
    }

    private fun copyProduct(
        event: CopyProductEvent.CopyProduct
    ) = viewModelScope.launch(dispatchers.io) {
        val product = Product(
            shoppingUid = event.uid,
            name = productState.value.name,
            quantity = productState.value.quantity,
            price = productState.value.price,
            discount = productState.value.discount,
            taxRate = productState.value.taxRate,
            completed = productState.value.completed
        )
        repository.addProduct(product)

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
            val list = shoppingLists.getShoppingListItems()
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
            text = UiText.FromResources(R.string.copyProduct_shoppingListsNotFound),
            fontSize = preferences.fontSize,
            appColor = AppColor.OnBackground
        )
        shoppingListsState.showNotFound(data)
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.copyProduct_header),
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
        _screenEventFlow.emit(CopyProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        locationButtonState.hideMenu()
    }
}