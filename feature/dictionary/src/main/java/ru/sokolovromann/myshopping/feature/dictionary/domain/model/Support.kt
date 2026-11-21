package ru.sokolovromann.myshopping.feature.dictionary.domain.model

import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFilteredFabrics

data class Support(
    val suggestionWithFabrics: SuggestionWithFilteredFabrics?,
    val generalPreferences: GeneralPreferences
)