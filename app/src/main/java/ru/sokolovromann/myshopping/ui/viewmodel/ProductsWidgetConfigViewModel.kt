package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.compose.event.ProductsWidgetConfigScreenEvent
import ru.sokolovromann.myshopping.ui.model.ProductsWidgetConfigState
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsWidgetConfigEvent
import javax.inject.Inject

@HiltViewModel
class ProductsWidgetConfigViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository
) : ViewModel(), ViewModelEvent<ProductsWidgetConfigEvent> {

    val productsWidgetConfigState = ProductsWidgetConfigState()

    private val _screenEventFlow: MutableSharedFlow<ProductsWidgetConfigScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ProductsWidgetConfigScreenEvent> = _screenEventFlow

    override fun onEvent(event: ProductsWidgetConfigEvent) {
        when (event) {
            ProductsWidgetConfigEvent.OnClickCancel -> onClickCancel()

            is ProductsWidgetConfigEvent.OnCreate -> onCreate(event)

            is ProductsWidgetConfigEvent.OnShoppingListSelected -> onShoppingListSelected(event)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.OnFinishApp)
    }

    private fun onCreate(
        event: ProductsWidgetConfigEvent.OnCreate
    ) = viewModelScope.launch(AppDispatchers.Main) {
        if (event.widgetId == null) {
            _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.OnFinishApp)
        } else {
            productsWidgetConfigState.saveWidgetId(event.widgetId)
            shoppingListsRepository.getPurchasesWithConfig().collect {
                productsWidgetConfigState.populate(it)
            }
        }
    }

    private fun onShoppingListSelected(
        event: ProductsWidgetConfigEvent.OnShoppingListSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        val widgetId = productsWidgetConfigState.widgetId
        _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.OnUpdate(widgetId, event.uid))
    }
}