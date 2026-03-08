package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.utils.math.DiscountType

sealed class AddEditProductEvent {

    object OnClickSave : AddEditProductEvent()

    object OnClickCancel : AddEditProductEvent()

    data class OnNameValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnNameSelected(val name: String) : AddEditProductEvent()

    object OnInvertNameOtherFields : AddEditProductEvent()

    data class OnUidValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnBrandValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnBrandSelected(val brand: String) : AddEditProductEvent()

    data class OnSizeValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnSizeSelected(val size: String) : AddEditProductEvent()

    data class OnColorValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnColorSelected(val color: String) : AddEditProductEvent()

    data class OnManufacturerValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnManufacturerSelected(val manufacturer: String) : AddEditProductEvent()

    data class OnQuantityValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnQuantitySelected(val quantity: String, val symbol: String) : AddEditProductEvent()

    object OnClickMinusOneQuantity : AddEditProductEvent()

    object OnClickPlusOneQuantity : AddEditProductEvent()

    data class OnQuantitySymbolValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnQuantitySymbolSelected(val symbol: String) : AddEditProductEvent()

    data class OnPriceValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnPriceSelected(val price: String) : AddEditProductEvent()

    object OnInvertPriceOtherFields : AddEditProductEvent()

    data class OnDiscountValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnDiscountAsPercentSelected(val asPercent: Boolean) : AddEditProductEvent()

    data class OnDiscountSelected(val discount: String, val type: DiscountType) : AddEditProductEvent()

    data class OnSelectDiscountAsPercent(val expanded: Boolean) : AddEditProductEvent()

    data class OnTotalValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnTotalSelected(val total: String) : AddEditProductEvent()

    data class OnNoteValueChanged(val value: TextFieldValue) : AddEditProductEvent()

    data class OnLockProductElementSelected(val lockProductElement: LockProductElement) : AddEditProductEvent()

    data class OnSelectLockProductElement(val expanded: Boolean) : AddEditProductEvent()
}