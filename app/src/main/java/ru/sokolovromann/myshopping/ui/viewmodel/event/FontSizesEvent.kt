package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.FontSize

sealed class FontSizesEvent {

    object OnClickSave : FontSizesEvent()

    object OnClickCancel : FontSizesEvent()

    data class OnSelectAppFontSize(val expanded: Boolean) : FontSizesEvent()

    data class OnAppFontSizeSelected(val fontSize: FontSize) : FontSizesEvent()

    data class OnSelectWidgetFontSize(val expanded: Boolean) : FontSizesEvent()

    data class OnWidgetFontSizeSelected(val fontSize: FontSize) : FontSizesEvent()
}