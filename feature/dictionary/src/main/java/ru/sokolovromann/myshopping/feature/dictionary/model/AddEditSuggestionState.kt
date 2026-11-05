package ru.sokolovromann.myshopping.feature.dictionary.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class AddEditSuggestionState(
    val showKeyboard: Boolean = false,
    val name: TextFieldValue = TextFieldValue(),
    val isNameError: Boolean = false,
    val fabrics: Map<UiText, List<FabricItem>> = emptyMap()
)