package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.MoveProductScreenEvent
import ru.sokolovromann.myshopping.ui.model.MoveProductState
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

    init { onInit() }

    override fun onEvent(event: MoveProductEvent) {
        when (event) {
            MoveProductEvent.OnClickAddShoppingList -> onClickAddShoppingList()

            MoveProductEvent.OnClickCancel -> onClickCancel()

            is MoveProductEvent.OnClickMoveProducts -> onClickMoveProducts(event)

            is MoveProductEvent.OnLocationSelected -> onLocationSelected(event)

            is MoveProductEvent.OnSelectLocation -> onSelectLocation(event)

            is MoveProductEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        moveProductState.onWaiting()

        savedStateHandle.get<String>(UiRouteKey.ProductUid.key)?.let {  productUid ->
            val uids = productUid.split(",")
            shoppingListsRepository.getProducts(uids).firstOrNull()?.let { products ->
                moveProductState.saveProducts(products)
            }
        }

        shoppingListsRepository.getPurchasesWithConfig().collect {
            moveProductState.populate(it, ShoppingLocation.PURCHASES)
        }
    }

    private fun onClickAddShoppingList() = viewModelScope.launch {
        shoppingListsRepository.addShopping()
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(MoveProductScreenEvent.OnShowBackScreen)
    }

    private fun onClickMoveProducts(
        event: MoveProductEvent.OnClickMoveProducts
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.moveProducts(
            products = moveProductState.savedProducts,
            shoppingUid = event.shoppingUid
        )
        _screenEventFlow.emit(MoveProductScreenEvent.OnShowBackScreen)
    }

    private fun onLocationSelected(
        event: MoveProductEvent.OnLocationSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        moveProductState.onWaiting()

        when (event.location) {
            ShoppingLocation.PURCHASES -> shoppingListsRepository.getPurchasesWithConfig().collect {
                moveProductState.populate(it, ShoppingLocation.PURCHASES)
            }

            ShoppingLocation.ARCHIVE -> shoppingListsRepository.getArchiveWithConfig().collect {
                moveProductState.populate(it, ShoppingLocation.ARCHIVE)
            }

            else -> {}
        }
    }

    private fun onSelectLocation(event: MoveProductEvent.OnSelectLocation) {
        moveProductState.onSelectLocation(event.expanded)
    }

    private fun onShowHiddenShoppingLists(event: MoveProductEvent.OnShowHiddenShoppingLists) {
        moveProductState.onShowHiddenShoppingLists(event.display)
    }
}