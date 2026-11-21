package ru.sokolovromann.myshopping.feature.dictionary.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.UID

@Immutable
data class UiFabric(
    val uid: UID,
    val text: String
)