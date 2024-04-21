package ru.sokolovromann.myshopping.ui.model

@Deprecated("Use FontSizeOffset(Int)")
data class UiFontSize(
    val itemTitle: Int,
    val itemBody: Int,
    val itemsHeader: Int,
    val widgetHeader: Int,
    val widgetContent: Int,
    val button: Int,
    val textField: Int
) {

    companion object {
        val Default: UiFontSize = UiFontSize(
            itemTitle = 16,
            itemBody = 14,
            itemsHeader = 20,
            widgetHeader = 18,
            widgetContent = 16,
            button = 14,
            textField = 16
        )
    }
}