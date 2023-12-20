package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
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
            is ProductsWidgetConfigEvent.OnCreate -> onCreate(event)

            is ProductsWidgetConfigEvent.SelectShoppingList -> selectShoppingList(event)

            ProductsWidgetConfigEvent.CancelSelectingShoppingList -> cancelSelectingShoppingList()
        }
    }

    private fun onCreate(event: ProductsWidgetConfigEvent.OnCreate) = viewModelScope.launch {
        if (event.widgetId == null) {
            _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.FinishApp)
        } else {
            withContext(AppDispatchers.Main) {
                productsWidgetConfigState.saveWidgetId(event.widgetId)
            }

            shoppingListsRepository.getPurchasesWithConfig().collect {
                shoppingListsLoaded(it)
            }
        }
    }

    private suspend fun shoppingListsLoaded(
        shoppingListsWithConfig: ShoppingListsWithConfig
    ) = withContext(AppDispatchers.Main) {
        productsWidgetConfigState.populate(shoppingListsWithConfig)
    }

    private fun selectShoppingList(
        event: ProductsWidgetConfigEvent.SelectShoppingList
    ) = viewModelScope.launch {
        val widgetId = productsWidgetConfigState.widgetId
        _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.UpdateWidget(widgetId, event.uid))
    }

    private fun cancelSelectingShoppingList() = viewModelScope.launch {
        _screenEventFlow.emit(ProductsWidgetConfigScreenEvent.FinishApp)
    }
}