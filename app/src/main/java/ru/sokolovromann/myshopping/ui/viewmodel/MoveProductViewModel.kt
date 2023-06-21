package ru.sokolovromann.myshopping.ui.viewmodel

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
import ru.sokolovromann.myshopping.data.repository.MoveProductRepository
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent
import javax.inject.Inject

@HiltViewModel
class MoveProductViewModel @Inject constructor(
    private val repository: MoveProductRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<MoveProductEvent> {

    val moveProductState: MoveProductState = MoveProductState()

    private val _screenEventFlow: MutableSharedFlow<MoveProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MoveProductScreenEvent> = _screenEventFlow

    init {
        getProducts()
        getPurchases()
    }

    override fun onEvent(event: MoveProductEvent) {
        when (event) {
            MoveProductEvent.AddShoppingList -> addShoppingList()

            is MoveProductEvent.MoveProduct -> moveProduct(event)

            MoveProductEvent.SelectShoppingListLocation -> selectShoppingListLocation()

            MoveProductEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            is MoveProductEvent.ShowShoppingLists -> showShoppingLists(event)

            MoveProductEvent.CancelMovingProduct -> cancelMovingProduct()

            MoveProductEvent.HideShoppingListsLocation -> hideShoppingListsLocation()
        }
    }

    private fun getPurchases() = viewModelScope.launch {
        withContext(dispatchers.main) {
            moveProductState.showLoading()
        }

        repository.getPurchases().collect {
            shoppingListsLoaded(
                shoppingLists = it,
                location = ShoppingListLocation.PURCHASES
            )
        }
    }

    private fun getArchive() = viewModelScope.launch {
        withContext(dispatchers.main) {
            moveProductState.showLoading()
        }

        repository.getArchive().collect {
            shoppingListsLoaded(
                shoppingLists = it,
                location = ShoppingListLocation.ARCHIVE
            )
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingLists: ShoppingLists,
        location: ShoppingListLocation
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            moveProductState.showNotFound(shoppingLists.preferences, location)
        } else {
            moveProductState.showShoppingLists(shoppingLists, location)
        }
    }

    private fun getProducts() = viewModelScope.launch {
        val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""
        val uids = productUid.split(",")
        val products = repository.getProducts(uids).firstOrNull()

        if (products == null) {
            cancelMovingProduct()
        } else {
            withContext(dispatchers.main) {
                moveProductState.saveProducts(products)
            }
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        val shoppingList = moveProductState.getShoppingListResult()
            .getOrElse { return@launch }
        repository.addShoppingList(shoppingList)
    }

    private fun moveProduct(event: MoveProductEvent.MoveProduct) = viewModelScope.launch {
        moveProductState.selectShoppingList(event.uid)
        val products = moveProductState.getProductsResult()
            .getOrElse { return@launch }

        repository.editProducts(products)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(MoveProductScreenEvent.ShowBackScreenAndUpdateProductsWidgets)
        }
    }

    private fun selectShoppingListLocation() {
        moveProductState.showLocation()
    }

    private fun displayHiddenShoppingLists() {
        moveProductState.displayHiddenShoppingLists()
    }

    private fun showShoppingLists(event: MoveProductEvent.ShowShoppingLists) {
        hideShoppingListsLocation()

        when (event.location) {
            ShoppingListLocation.PURCHASES -> getPurchases()
            ShoppingListLocation.ARCHIVE -> getArchive()
            else -> {}
        }
    }

    private fun cancelMovingProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MoveProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        moveProductState.hideLocation()
    }
}