package ru.sokolovromann.myshopping.feature.dictionary

import androidx.compose.runtime.Immutable

@Immutable
data class DictionaryState(
    val isLoading: Boolean = true,
    val suggestionsState: SuggestionsState = SuggestionsState()
)