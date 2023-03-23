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
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CopyProductEvent> {

    val copyProductState = CopyProductState()

    private val _screenEventFlow: MutableSharedFlow<CopyProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CopyProductScreenEvent> = _screenEventFlow

    init {
        getPurchases()
        getProduct()
    }

    override fun onEvent(event: CopyProductEvent) {
        when (event) {
            is CopyProductEvent.CopyProduct -> copyProduct(event)

            CopyProductEvent.SelectShoppingListLocation -> selectShoppingListLocation()

            is CopyProductEvent.ShowShoppingLists -> showShoppingLists(event)

            CopyProductEvent.CancelCopingProduct -> cancelCopingProduct()

            CopyProductEvent.HideShoppingListsLocation -> hideShoppingListsLocation()
        }
    }

    private fun getPurchases() = viewModelScope.launch {
        withContext(dispatchers.main) {
            copyProductState.showLoading()
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
            copyProductState.showLoading()
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
            copyProductState.showNotFound(shoppingLists.preferences, location)
        } else {
            copyProductState.showShoppingLists(shoppingLists, location)
        }
    }

    private fun getProduct() = viewModelScope.launch {
        val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""
        val product = repository.getProduct(productUid).firstOrNull()

        if (product == null) {
            cancelCopingProduct()
        } else {
            withContext(dispatchers.main) {
                copyProductState.saveProduct(product)
            }
        }
    }

    private fun copyProduct(event: CopyProductEvent.CopyProduct) = viewModelScope.launch {
        copyProductState.selectShoppingList(event.uid)
        val product = copyProductState.getProductResult()
            .getOrElse { return@launch }

        repository.addProduct(product)

        withContext(dispatchers.main) {
            _screenEventFlow.emit(CopyProductScreenEvent.ShowBackScreen)
        }
    }

    private fun selectShoppingListLocation() {
        copyProductState.showLocation()
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