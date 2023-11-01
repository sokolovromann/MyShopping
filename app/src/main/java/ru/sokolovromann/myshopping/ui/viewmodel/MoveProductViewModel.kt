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
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.MoveProductEvent
import javax.inject.Inject

@HiltViewModel
class MoveProductViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
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
        withContext(AppDispatchers.Main) {
            moveProductState.showLoading()
        }

        shoppingListsRepository.getPurchasesWithConfig().collect {
            shoppingListsLoaded(
                shoppingListsWithConfig = it,
                location = ShoppingLocation.PURCHASES
            )
        }
    }

    private fun getArchive() = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            moveProductState.showLoading()
        }

        shoppingListsRepository.getArchiveWithConfig().collect {
            shoppingListsLoaded(
                shoppingListsWithConfig = it,
                location = ShoppingLocation.ARCHIVE
            )
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        location: ShoppingLocation
    ) = withContext(AppDispatchers.Main) {
        if (shoppingListsWithConfig.shoppingLists.isEmpty()) {
            moveProductState.showNotFound(shoppingListsWithConfig, location)
        } else {
            moveProductState.showShoppingLists(shoppingListsWithConfig, location)
        }
    }

    private fun getProducts() = viewModelScope.launch {
        val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""
        val uids = productUid.split(",")
        val products = shoppingListsRepository.getProducts(uids).firstOrNull()

        if (products == null) {
            cancelMovingProduct()
        } else {
            withContext(AppDispatchers.Main) {
                moveProductState.saveProducts(products)
            }
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        shoppingListsRepository.addShopping()
    }

    private fun moveProduct(event: MoveProductEvent.MoveProduct) = viewModelScope.launch {
        shoppingListsRepository.moveProducts(
            products = moveProductState.products,
            shoppingUid = event.uid
        )

        withContext(AppDispatchers.Main) {
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
            ShoppingLocation.PURCHASES -> getPurchases()
            ShoppingLocation.ARCHIVE -> getArchive()
            else -> {}
        }
    }

    private fun cancelMovingProduct() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(MoveProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        moveProductState.hideLocation()
    }
}