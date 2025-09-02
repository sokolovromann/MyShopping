package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.utils.uppercaseFirst
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.SelectFromAutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.SelectFromAutocompletesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.SelectFromAutocompletesEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class SelectFromAutocompletesViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<SelectFromAutocompletesEvent> {

    val selectFromAutocompletesState: SelectFromAutocompletesState = SelectFromAutocompletesState()

    private val _screenEventFlow: MutableSharedFlow<SelectFromAutocompletesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<SelectFromAutocompletesScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: SelectFromAutocompletesEvent) {
        when (event) {
            SelectFromAutocompletesEvent.OnClickSave -> onClickSave()

            SelectFromAutocompletesEvent.OnClickCancel -> onClickCancel()

            is SelectFromAutocompletesEvent.OnAutocompleteSelected -> onAutocompleteSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        selectFromAutocompletesState.onWaiting()

        autocompletesRepository.getAllAutocompletes().firstOrNull()?.let {
            selectFromAutocompletesState.populate(it)
        }

        val shoppingUid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(shoppingUid).firstOrNull()?.let {
            selectFromAutocompletesState.saveShoppingList(it)
            it.getSortedProducts()
                .map { product -> product.name.uppercaseFirst() }
                .forEach { name -> selectFromAutocompletesState.onAutocompleteSelected(true, name) }
        }
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        listOf(
            async {
                selectFromAutocompletesState.getAddedProducts().forEach {
                    shoppingListsRepository.saveProduct(it)
                }
            },
            async {
                selectFromAutocompletesState.getDeletedProducts().forEach {
                    shoppingListsRepository.deleteProductsByProductUids(
                        shoppingUid = it.shoppingUid,
                        productsUids = listOf(it.productUid)
                    )
                }
            }
        ).awaitAll()
        _screenEventFlow.emit(SelectFromAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(SelectFromAutocompletesScreenEvent.OnShowBackScreen)
    }

    private fun onAutocompleteSelected(event: SelectFromAutocompletesEvent.OnAutocompleteSelected) {
        selectFromAutocompletesState.onAutocompleteSelected(event.selected, event.name)
    }
}