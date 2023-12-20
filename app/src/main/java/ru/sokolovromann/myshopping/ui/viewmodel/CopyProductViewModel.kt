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
import ru.sokolovromann.myshopping.ui.compose.event.CopyProductScreenEvent
import ru.sokolovromann.myshopping.ui.model.CopyProductState
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyProductEvent
import javax.inject.Inject

@HiltViewModel
class CopyProductViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
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
        withContext(AppDispatchers.Main) {
            copyProductState.onWaiting()
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
            copyProductState.onWaiting()
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
        copyProductState.populate(shoppingListsWithConfig, location)
    }

    private fun getProducts() = viewModelScope.launch {
        val productUid: String = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) ?: ""
        val uids = productUid.split(",")
        val products = shoppingListsRepository.getProducts(uids).firstOrNull()

        if (products == null) {
            cancelCopingProduct()
        } else {
            withContext(AppDispatchers.Main) {
                copyProductState.saveProducts(products)
            }
        }
    }

    private fun addShoppingList() = viewModelScope.launch {
        shoppingListsRepository.addShopping()
    }

    private fun copyProduct(event: CopyProductEvent.CopyProduct) = viewModelScope.launch {
        shoppingListsRepository.copyProducts(
            products = copyProductState.savedProducts,
            shoppingUid = event.uid
        )

        withContext(AppDispatchers.Main) {
            val screenEvent = CopyProductScreenEvent.ShowBackScreenAndUpdateProductsWidgets
            _screenEventFlow.emit(screenEvent)
        }
    }

    private fun selectShoppingListLocation() {
        copyProductState.onSelectLocation(true)
    }

    private fun displayHiddenShoppingLists() {
        copyProductState.onShowHiddenShoppingLists(true)
    }

    private fun showShoppingLists(event: CopyProductEvent.ShowShoppingLists) {
        hideShoppingListsLocation()

        when (event.location) {
            ShoppingLocation.PURCHASES -> getPurchases()
            ShoppingLocation.ARCHIVE -> getArchive()
            else -> {}
        }
    }

    private fun cancelCopingProduct() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(CopyProductScreenEvent.ShowBackScreen)
    }

    private fun hideShoppingListsLocation() {
        copyProductState.onSelectLocation(false)
    }
}