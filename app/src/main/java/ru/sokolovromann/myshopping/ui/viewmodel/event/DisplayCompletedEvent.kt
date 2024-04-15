package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.DisplayCompleted

sealed class DisplayCompletedEvent {

    object OnClickSave : DisplayCompletedEvent()

    object OnClickCancel : DisplayCompletedEvent()

    data class OnSelectAppDisplayCompleted(val expanded: Boolean) : DisplayCompletedEvent()

    data class OnAppDisplayCompletedSelected(val displayCompleted: DisplayCompleted) : DisplayCompletedEvent()

    data class OnSelectWidgetDisplayCompleted(val expanded: Boolean) : DisplayCompletedEvent()

    data class OnWidgetDisplayCompletedSelected(val displayCompleted: DisplayCompleted) : DisplayCompletedEvent()
}