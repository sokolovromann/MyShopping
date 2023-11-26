package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.ui.model.UiFontSize

object UiAppConfigMapper {

    fun toUiFontSize(fontSize: FontSize): UiFontSize {
        val default: UiFontSize = UiFontSize.Default
        val offset: Int = when (fontSize) {
            FontSize.SMALL -> -2
            FontSize.MEDIUM -> 0
            FontSize.LARGE -> 2
            FontSize.VERY_LARGE -> 4
            FontSize.HUGE -> 6
            FontSize.VERY_HUGE -> 8
        }
        return UiFontSize(
            itemTitle = default.itemTitle + offset,
            itemBody = default.itemBody + offset,
            itemsHeader = default.itemsHeader + offset,
            widgetHeader = default.widgetHeader + offset,
            widgetContent = default.widgetContent + offset,
            button = default.button + offset,
            textField = default.textField + offset
        )
    }
}