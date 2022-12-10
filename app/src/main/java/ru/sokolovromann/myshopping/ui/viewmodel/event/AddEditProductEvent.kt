package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.repository.model.Discount
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Quantity
import ru.sokolovromann.myshopping.ui.compose.state.UiText

sealed class AddEditProductEvent {

    object SaveProduct : AddEditProductEvent()

    object CancelSavingProduct : AddEditProductEvent()

    data class ProductNameChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductQuantityChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductQuantitySymbolChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductPriceChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductDiscountChanged(val value: TextFieldValue) : AddEditProductEvent()

    object ProductDiscountAsPercentSelected : AddEditProductEvent()

    object ProductDiscountAsMoneySelected : AddEditProductEvent()

    data class ProductTotalChanged(val value: TextFieldValue) : AddEditProductEvent()

    object InvertProductsLockQuantity : AddEditProductEvent()

    data class AutocompleteNameSelected(val text: String) : AddEditProductEvent()

    object AutocompleteMinusOneQuantitySelected : AddEditProductEvent()

    object AutocompletePlusOneQuantitySelected : AddEditProductEvent()

    data class AutocompleteQuantitySelected(val quantity: Quantity) : AddEditProductEvent()

    data class AutocompletePriceSelected(val price: Money) : AddEditProductEvent()

    data class AutocompleteTotalSelected(val total: Money) : AddEditProductEvent()

    data class AutocompleteDiscountSelected(val discount: Discount) : AddEditProductEvent()

    object ShowProductDiscountAsPercentMenu : AddEditProductEvent()

    object HideProductDiscountAsPercentMenu : AddEditProductEvent()
}