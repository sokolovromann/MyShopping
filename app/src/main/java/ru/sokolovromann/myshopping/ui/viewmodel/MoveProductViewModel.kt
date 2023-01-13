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
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent
import javax.inject.Inject

@HiltViewModel
class MoveProductViewModel @Inject constructor(
    private val repository: MoveProductRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<MoveProductEvent> {

    val moveProductState: MoveProductState = MoveProductState()

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

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

    private fun moveProduct(event: MoveProductEvent.MoveProduct) = viewModelScope.launch(dispatchers.io) {
        repository.moveProduct(
            productUid = productUid,
            shoppingUid = event.uid,
            lastModified = System.currentTimeMillis()
        )

        showBackScreen()
    }

    private fun selectShoppingListsLocation() {
        moveProductState.showLocation()
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
        moveProductState.showLoading()
    }

    private suspend fun showShoppingLists(
        shoppingLists: ShoppingLists,
        location: ShoppingListLocation
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            moveProductState.showNotFound(shoppingLists.preferences, location)
        } else {
            moveProductState.showShoppingLists(shoppingLists, location)
        }
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.moveProduct_header),
                fontSize = FontSize.MEDIUM
            ),
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.Close)
            )
        )
        _topBarState.value = data
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MoveProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        moveProductState.hideLocation()
    }
}