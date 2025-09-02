package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListTotalScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditShoppingListTotalState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListTotalEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class EditShoppingListTotalViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelEvent<EditShoppingListTotalEvent> {

    val editShoppingListTotalState: EditShoppingListTotalState = EditShoppingListTotalState()

    private val _screenEventFlow: MutableSharedFlow<EditShoppingListTotalScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditShoppingListTotalScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: EditShoppingListTotalEvent) {
        when (event) {
            EditShoppingListTotalEvent.OnClickSave -> onClickSave()

            EditShoppingListTotalEvent.OnClickCancel -> onClickCancel()

            is EditShoppingListTotalEvent.OnTotalChanged -> onTotalChanged(event)

            is EditShoppingListTotalEvent.OnDiscountChanged -> onDiscountChanged(event)

            is EditShoppingListTotalEvent.OnDiscountAsPercentSelected -> onDiscountAsPercentSelected(event)

            is EditShoppingListTotalEvent.OnSelectDiscountAsPercent -> onSelectDiscountAsPercent(event)

            is EditShoppingListTotalEvent.OnBudgetChanged -> onBudgetChanged(event)

            is EditShoppingListTotalEvent.OnBudgetProductsSelected -> onBudgetProductsSelected(event)

            is EditShoppingListTotalEvent.OnSelectBudgetProducts -> onSelectBudgetProducts(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            editShoppingListTotalState.populate(it)
            _screenEventFlow.emit(EditShoppingListTotalScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        editShoppingListTotalState.onWaiting()

        val shopping = editShoppingListTotalState.getCurrentShopping()

        shoppingListsRepository.saveShoppingListBudget(
            shoppingUid = shopping.uid,
            budget = shopping.budget,
            budgetProducts = shopping.budgetProducts
        )

        if (shopping.totalFormatted) {
            shoppingListsRepository.saveShoppingListTotal(
                shoppingUid = shopping.uid,
                total = shopping.total,
                discount = shopping.discount
            )
        } else {
            shoppingListsRepository.deleteShoppingListTotal(shopping.uid)
        }

        val event = EditShoppingListTotalScreenEvent.OnShowBackScreen(shopping.uid)
        _screenEventFlow.emit(event)
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        val uid = editShoppingListTotalState.getCurrentShopping().uid
        _screenEventFlow.emit(EditShoppingListTotalScreenEvent.OnShowBackScreen(uid))
    }

    private fun onTotalChanged(event: EditShoppingListTotalEvent.OnTotalChanged) {
        editShoppingListTotalState.onTotalValueChanged(event.value)
    }

    private fun onDiscountChanged(event: EditShoppingListTotalEvent.OnDiscountChanged) {
        editShoppingListTotalState.onDiscountValueChanged(event.value)
    }

    private fun onDiscountAsPercentSelected(event: EditShoppingListTotalEvent.OnDiscountAsPercentSelected) {
        editShoppingListTotalState.onDiscountAsPercentSelected(event.asPercent)
    }

    private fun onSelectDiscountAsPercent(event: EditShoppingListTotalEvent.OnSelectDiscountAsPercent) {
        editShoppingListTotalState.onSelectDiscountAsPercent(event.expanded)
    }

    private fun onBudgetChanged(event: EditShoppingListTotalEvent.OnBudgetChanged) {
        editShoppingListTotalState.onBudgetValueChanged(event.value)
    }

    private fun onBudgetProductsSelected(event: EditShoppingListTotalEvent.OnBudgetProductsSelected) {
        editShoppingListTotalState.onBudgetProductsSelected(event.budgetProducts)
    }

    private fun onSelectBudgetProducts(event: EditShoppingListTotalEvent.OnSelectBudgetProducts) {
        editShoppingListTotalState.onSelectBudgetProducts(event.expanded)
    }
}