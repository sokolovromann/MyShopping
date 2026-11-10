package ru.sokolovromann.myshopping.feature.settings.action

import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart

sealed class CartsSettingsAction {

    data class OnAfterAddingSelected(val afterAdding: AfterAddingCart) : CartsSettingsAction()

    data class OnAfterCompletingSelected(val afterCompleting: AfterCompletingCart) : CartsSettingsAction()

    data class OnAfterArchivingSelected(val afterArchiving: AfterArchivingCart) : CartsSettingsAction()

    data class OnSwipeLeftSelected(val swipe: SwipeCart.Left) : CartsSettingsAction()

    data class OnSwipeRightSelected(val swipe: SwipeCart.Right) : CartsSettingsAction()

    data class OnDeletionFromTrashSelected(val deletionFromTrash: DeletionCartFromTrash) : CartsSettingsAction()
}