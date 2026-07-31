package ru.sokolovromann.myshopping.old.api15.model

data class SuggestionDetail(
    val uid: String,
    val directory: String,
    val created: String,
    val lastModified: String,
    val type: String,
    val value: String,
    val valueParams: String,
    val used: String
)