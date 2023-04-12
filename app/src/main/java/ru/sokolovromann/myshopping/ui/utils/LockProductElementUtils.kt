package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.LockProductElement
import ru.sokolovromann.myshopping.ui.compose.state.UiIcon

fun LockProductElement.toButtonIcon(): UiIcon = if (this == LockProductElement.TOTAL) {
    UiIcon.FromResources(R.drawable.ic_all_unlock)
} else {
    UiIcon.FromResources(R.drawable.ic_all_lock)
}