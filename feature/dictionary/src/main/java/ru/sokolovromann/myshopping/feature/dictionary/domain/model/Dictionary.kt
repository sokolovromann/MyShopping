package ru.sokolovromann.myshopping.feature.dictionary.domain.model

import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFilteredFabrics
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences

data class Dictionary(
    val suggestionsWithFabrics: Collection<SuggestionWithFilteredFabrics>,
    val suggestionsPreferences: SuggestionsPreferences,
    val generalPreferences: GeneralPreferences
)