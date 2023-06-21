package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.ProductsWidgetRepository
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.compose.event.ProductsWidgetConfigScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.ProductsWidgetConfigState
import ru.sokolovromann.myshopping.ui.viewmodel.event.ProductsWidgetConfigEvent
import javax.inject.Inject

@HiltViewModel
class ProductsWidgetConfigViewModel @Inject constructor(
    private val repository: ProductsWidgetRepository,
    val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<ProductsWidgetConfigEvent> {

    val productsWidgetConfigState = ProductsWidgetConfigState()

    private val _screenEventFlow: MutableSharedFlow<ProductsWidgetConfigScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<ProductsWidgetConfigScreenEvent> = _screenEventFlow

    override fun onEvent(event: ProductsWidgetConfigEvent) {
        when (event) {
            is ProductsWidgetConfigEvent.OnCreate -> onCreate(event)

            is ProductsWidgetConfigEvent.SelectShoppingList -> selectShoppingList(event)

            ProductsWidgetConfigEvent.CancelSelectingShoppingList -> cancelSelectingShoppingList()
        }
    }

    private fun onCreate(event: ProductsWidgetConfigEvent.OnCreate) = viewModelScope.launch {
        if (event.widgetId == null) {
            _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.FinishApp)
        } else {
            withContext(dispatchers.main) {
                productsWidgetConfigState.onCreate(event.widgetId)
            }

            repository.getShoppingLists().collect {
                shoppingListsLoaded(it)
            }
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingLists: ShoppingLists
    ) = withContext(dispatchers.main) {
        if (shoppingLists.shoppingLists.isEmpty()) {
            productsWidgetConfigState.showNotFound(shoppingLists.preferences)
        } else {
            productsWidgetConfigState.showShoppingLists(shoppingLists)
        }
    }

    private fun selectShoppingList(
        event: ProductsWidgetConfigEvent.SelectShoppingList
    ) = viewModelScope.launch {
        val widgetId = productsWidgetConfigState.widgetId
        val shoppingList = productsWidgetConfigState.getShoppingListResult(event.uid)
            .getOrElse { return@launch }

        _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.UpdateWidget(widgetId, shoppingList.uid))
    }

    private fun cancelSelectingShoppingList() = viewModelScope.launch {
        _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.FinishApp)
    }
}