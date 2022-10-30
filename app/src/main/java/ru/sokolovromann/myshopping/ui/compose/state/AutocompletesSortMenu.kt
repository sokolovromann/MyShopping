package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.data.repository.model.Sort

data class AutocompletesSortMenu(
    val title: TextData = TextData.Title,
    val byCreatedBody: TextData = TextData.Body,
    val byNameBody: TextData = TextData.Body,
    val selected: Sort = Sort()
)