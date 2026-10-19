package ru.sokolovromann.myshopping.feature.settings.action

import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.Tax

sealed class AddEditProductSettingsAction {

    data class OnAfterTappingEnterSelected(val afterTappingEnter: AfterTappingByProductEnter) : AddEditProductSettingsAction()

    data class OnAfterAddingSelected(val afterAdding: AfterAddingProduct) : AddEditProductSettingsAction()

    data class OnAfterEditingSelected(val afterEditing: AfterEditingProduct) : AddEditProductSettingsAction()

    data class OnTaxSaved(val tax: Tax) : AddEditProductSettingsAction()
}