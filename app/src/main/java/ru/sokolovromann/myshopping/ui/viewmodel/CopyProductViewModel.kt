package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CopyProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent
import javax.inject.Inject

@HiltViewModel
class CopyProductViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CopyProductEvent> {

    val copyProductState = CopyProductState()

    private val _screenEventFlow: MutableSharedFlow<CopyProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CopyProductScreenEvent> = _screenEventFlow

    init {
        getPurchases()
        getProducts()
    }

    override fun onEvent(event: CopyProductEvent) {
        when (event) {
            CopyProductEvent.AddShoppingList -> addShoppingList()

            is CopyProductEvent.CopyProduct -> copyProduct(event)

            CopyProductEvent.SelectShoppingListLocation -> selectShoppingListLocation()

            CopyProductEvent.DisplayHiddenShoppingLists -> displayHiddenShoppingLists()

            is CopyProductEvent.ShowShoppingLists -> showShoppingLists(event)

            CopyProductEvent.CancelCopingProduct -> cancelCopingProduct()

            CopyProductEvent.HideShoppingListsLocation -> hideShoppingListsLocation()
        }
    }

    private fun getPurchases() = viewModelScope.launch {
        withContext(dispatchers.main) {
            copyProductState.showLoading()
        }

        shoppingListsRepository.getPurchases().collect {
            shoppingListsLoaded(
                shoppingLists = it,
                location = ShoppingListLocation.PURCHASES
            )
        }
    }

    private fun getArchive() = viewModelScope.launch {
        withContext(dispatchers.main) {
            copyProductState.showLoading()
        }

        shoppingListsRepository.getArchive().collect {
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
            copyProductState.showNotFound(shoppingLists.appConfig, location)
        } else {
            copyProductState.showShoppingLists(shoppingLists, location)
        }
    }

    private fun getProducts() = viewModelScope.launch {
        val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""
        val uids = productUid.split(",")
        val products = shoppingListsRepository.getProducts(uids).firstOrNull()

        if (products == null) {
            cancelCopingProduct()
        } else {
            withContext(dispatchers.main) {
                copyProductState.saveProducts(products)
            }
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        copyProductState.getShoppingListResult()
            .onSuccess { shoppingListsRepository.saveShoppingList(it) }
            .onFailure {
                val lastPosition = shoppingListsRepository.getShoppingListsLastPosition().first() ?: 0
                val shoppingList = ShoppingList(position = lastPosition.plus(1))
                shoppingListsRepository.saveShoppingList(shoppingList)
            }
    }

    private fun copyProduct(event: CopyProductEvent.CopyProduct) = viewModelScope.launch {
        copyProductState.selectShoppingList(event.uid)
        val products = copyProductState.getProductsResult()
            .getOrElse { return@launch }

        shoppingListsRepository.saveProducts(products)

        withContext(dispatchers.main) {
            val screenEvent = CopyProductScreenEvent.ShowBackScreenAndUpdateProductsWidgets
            _screenEventFlow.emit(screenEvent)
        }
    }

    private fun selectShoppingListLocation() {
        copyProductState.showLocation()
    }

    private fun displayHiddenShoppingLists() {
        copyProductState.displayHiddenShoppingLists()
    }

    private fun showShoppingLists(event: CopyProductEvent.ShowShoppingLists) {
        hideShoppingListsLocation()

        when (event.location) {
            ShoppingListLocation.PURCHASES -> getPurchases()
            ShoppingListLocation.ARCHIVE -> getArchive()
            else -> {}
        }
    }

    private fun cancelCopingProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(CopyProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        copyProductState.hideLocation()
    }
}