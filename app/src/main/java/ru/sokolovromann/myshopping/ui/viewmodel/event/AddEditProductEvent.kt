package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.repository.model.*

sealed class AddEditProductEvent {

    object SaveProduct : AddEditProductEvent()

    object CancelSavingProduct : AddEditProductEvent()

    data class ProductNameChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductNameFocusChanged(val focused: Boolean) : AddEditProductEvent()

    data class ProductUidChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductBrandChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductSizeChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductColorChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductManufacturerChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductQuantityChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductQuantitySymbolChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductPriceChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class ProductDiscountChanged(val value: TextFieldValue) : AddEditProductEvent()

    object ProductDiscountAsPercentSelected : AddEditProductEvent()

    object ProductDiscountAsMoneySelected : AddEditProductEvent()

    data class ProductTotalChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class LockProductElementSelected(val lockProductElement: LockProductElement) : AddEditProductEvent()

    data class AutocompleteNameSelected(val autocomplete: Autocomplete) : AddEditProductEvent()

    data class AutocompleteBrandSelected(val brand: String) : AddEditProductEvent()

    data class AutocompleteSizeSelected(val size: String) : AddEditProductEvent()

    data class AutocompleteColorSelected(val color: String) : AddEditProductEvent()

    data class AutocompleteManufacturerSelected(val manufacturer: String) : AddEditProductEvent()

    object AutocompleteMinusOneQuantitySelected : AddEditProductEvent()

    object AutocompletePlusOneQuantitySelected : AddEditProductEvent()

    data class AutocompleteQuantitySelected(val quantity: Quantity) : AddEditProductEvent()

    data class AutocompleteQuantitySymbolSelected(val quantity: Quantity) : AddEditProductEvent()

    data class AutocompletePriceSelected(val price: Money) : AddEditProductEvent()

    data class AutocompleteTotalSelected(val total: Money) : AddEditProductEvent()

    data class AutocompleteDiscountSelected(val discount: Money) : AddEditProductEvent()

    data class ProductNoteChanged(val value: TextFieldValue) : AddEditProductEvent()

    object ShowProductDiscountAsPercentMenu : AddEditProductEvent()

    object InvertNameOtherFields : AddEditProductEvent()

    object InvertPriceOtherFields : AddEditProductEvent()

    object SelectLockProductElement : AddEditProductEvent()

    object HideProductDiscountAsPercentMenu : AddEditProductEvent()

    object HideLockProductElement : AddEditProductEvent()
}