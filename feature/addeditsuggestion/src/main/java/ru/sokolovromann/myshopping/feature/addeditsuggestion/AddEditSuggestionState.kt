package ru.sokolovromann.myshopping.feature.addeditsuggestion

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.core.domain.model.UID

data class AddEditSuggestionState(
    val uid: UID? = null,
    val name: TextFieldValue = TextFieldValue(),
    val isNameError: Boolean = false
) {

    val isNewSuggestion: Boolean get() = uid == null
}