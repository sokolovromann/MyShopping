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
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent
import javax.inject.Inject

@HiltViewModel
class CopyProductViewModel @Inject constructor(
    private val repository: CopyProductRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CopyProductEvent> {

    val copyProductState = CopyProductState()

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
                shoppingLists = it,
                location = ShoppingListLocation.PURCHASES
            )
        }
    }

    private fun getArchive() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getArchive().collect {
            showShoppingLists(
                shoppingLists = it,
                location = ShoppingListLocation.ARCHIVE
            )
        }
    }

    private fun getTrash() = viewModelScope.launch(dispatchers.io) {
        showShoppingListsLoading()

        repository.getTrash().collect {
            showShoppingLists(
                shoppingLists = it,
                location = ShoppingListLocation.TRASH
            )
        }
    }

    private fun getProduct() = viewModelScope.launch(dispatchers.io) {
        val product = repository.getProduct(productUid).firstOrNull()

        if (product == null) {
            showBackScreen()
        } else {
            withContext(dispatchers.main) { copyProductState.saveProduct(product) }
        }
    }

    private fun copyProduct(
        event: CopyProductEvent.CopyProduct
    ) = viewModelScope.launch(dispatchers.io) {
        copyProductState.selectShoppingList(event.uid)
        val product = copyProductState.getProductResult()
            .getOrElse { return@launch }

        repository.addProduct(product)

        showBackScreen()
    }

    private fun selectShoppingListsLocation() {
        copyProductState.showLocation()
    }

    private fun displayShoppingListsPurchases() {
        hideShoppingListsLocation()
        getPurchases()
    }

    private fun displayShoppingListsArchive() {
        hideShoppingListsLocation()
        getArchive()
    }

    private fun displayShoppingListsTrash() {
        hideShoppingListsLocation()
        getTrash()
    }

    private suspend fun showShoppingListsLoading() = withContext(dispatchers.main) {
        copyProductState.showLoading()
    }

    private suspend fun showShoppingLists(
        shoppingLists: ShoppingLists,
        location: ShoppingListLocation
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            copyProductState.showNotFound(shoppingLists.preferences, location)
        } else {
            copyProductState.showShoppingLists(shoppingLists, location)
        }
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

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(CopyProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        copyProductState.hideLocation()
    }
}