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

    init { onInit() }

    override fun onEvent(event: CopyProductEvent) {
        when (event) {
            CopyProductEvent.OnClickAdd -> onClickAdd()

            CopyProductEvent.OnClickCancel -> onClickCancel()

            is CopyProductEvent.OnClickCopy -> onClickCopy(event)

            is CopyProductEvent.OnLocationSelected -> onLocationSelected(event)

            is CopyProductEvent.OnSelectLocation -> onSelectLocation(event)

            is CopyProductEvent.OnShowHiddenShoppingLists -> onShowHiddenShoppingLists(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        copyProductState.onWaiting()

        savedStateHandle.get<String>(UiRouteKey.ProductUid.key)?.let { productUid ->
            val uids = productUid.split(",")
            shoppingListsRepository.getProducts(uids).firstOrNull()?.let { products ->
                copyProductState.saveProducts(products)
            }
        }

        shoppingListsRepository.getPurchasesWithConfig().collect {
            copyProductState.populate(it, ShoppingLocation.PURCHASES)
        }
    }

    private fun onClickAdd() = viewModelScope.launch {
        shoppingListsRepository.addShopping()
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(CopyProductScreenEvent.OnShowBackScreen)
    }

    private fun onClickCopy(
        event: CopyProductEvent.OnClickCopy
    ) = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.copyProducts(
            products = copyProductState.savedProducts,
            shoppingUid = event.uid
        )
        _screenEventFlow.emit(CopyProductScreenEvent.OnShowBackScreen)
    }

    private fun onLocationSelected(
        event: CopyProductEvent.OnLocationSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        copyProductState.onWaiting()

        when (event.location) {
            ShoppingLocation.PURCHASES -> shoppingListsRepository.getPurchasesWithConfig().collect {
                copyProductState.populate(it, ShoppingLocation.PURCHASES)
            }

            ShoppingLocation.ARCHIVE -> shoppingListsRepository.getArchiveWithConfig().collect {
                copyProductState.populate(it, ShoppingLocation.ARCHIVE)
            }

            else -> {}
        }
    }


    private fun onSelectLocation(event: CopyProductEvent.OnSelectLocation) {
        copyProductState.onSelectLocation(event.expanded)
    }

    private fun onShowHiddenShoppingLists(event: CopyProductEvent.OnShowHiddenShoppingLists) {
        copyProductState.onShowHiddenShoppingLists(event.display)
    }
}