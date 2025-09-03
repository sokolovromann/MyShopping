package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CopyMoveProductsScreenEvent
import ru.sokolovromann.myshopping.ui.model.CopyMoveProductsState
import ru.sokolovromann.myshopping.ui.viewmodel.event.CopyMoveProductsEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class CopyMoveProductsViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CopyMoveProductsEvent> {

    val copyMoveProductsState = CopyMoveProductsState()

    private val _screenEventFlow: MutableSharedFlow<CopyMoveProductsScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CopyMoveProductsScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: CopyMoveProductsEvent) {
        when (event) {
            CopyMoveProductsEvent.OnClickAddShoppingList -> onClickAddShoppingList()

            CopyMoveProductsEvent.OnClickCancel -> onClickCancel()

            is CopyMoveProductsEvent.OnClickCopyOrMoveProducts -> onClickCopyOrMoveProducts(event)

            is CopyMoveProductsEvent.OnLocationSelected -> onLocationSelected(event)

            is CopyMoveProductsEvent.OnSelectLocation -> onSelectLocation(event)

            is CopyMoveProductsEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        copyMoveProductsState.onWaiting()

        savedStateHandle.get<String>(UiRouteKey.ProductUid.key)?.let { productUid ->
            val uids = productUid.split(",")
            shoppingListsRepository.getProducts(uids).firstOrNull()?.let { products ->
                copyMoveProductsState.saveProducts(products)
            }
        }

        shoppingListsRepository.getPurchasesWithConfig().collect {
            copyMoveProductsState.populate(it, ShoppingLocation.PURCHASES)
        }
    }

    private fun onClickAddShoppingList() = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.addShopping()
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(CopyMoveProductsScreenEvent.OnShowBackScreen)
    }

    private fun onClickCopyOrMoveProducts(
        event: CopyMoveProductsEvent.OnClickCopyOrMoveProducts
    ) = viewModelScope.launch(dispatcher) {
        savedStateHandle.get<Boolean>(UiRouteKey.IsCopy.key)?.let { isCopyScreen ->
            if (isCopyScreen) {
                shoppingListsRepository.copyProducts(
                    products = copyMoveProductsState.savedProducts,
                    shoppingUid = event.shoppingUid
                )
            } else {
                shoppingListsRepository.moveProducts(
                    products = copyMoveProductsState.savedProducts,
                    shoppingUid = event.shoppingUid
                )
            }
        }

        _screenEventFlow.emit(CopyMoveProductsScreenEvent.OnShowBackScreen)
    }

    private fun onLocationSelected(
        event: CopyMoveProductsEvent.OnLocationSelected
    ) = viewModelScope.launch(dispatcher) {
        copyMoveProductsState.onWaiting()

        when (event.location) {
            ShoppingLocation.PURCHASES -> shoppingListsRepository.getPurchasesWithConfig().collect {
                copyMoveProductsState.populate(it, ShoppingLocation.PURCHASES)
            }

            ShoppingLocation.ARCHIVE -> shoppingListsRepository.getArchiveWithConfig().collect {
                copyMoveProductsState.populate(it, ShoppingLocation.ARCHIVE)
            }

            else -> {}
        }
    }

    private fun onSelectLocation(event: CopyMoveProductsEvent.OnSelectLocation) {
        copyMoveProductsState.onSelectLocation(event.expanded)
    }

    private fun onShowHiddenShoppingLists(event: CopyMoveProductsEvent.OnShowHiddenShoppingLists) {
        copyMoveProductsState.onShowHiddenShoppingLists(event.display)
    }
}