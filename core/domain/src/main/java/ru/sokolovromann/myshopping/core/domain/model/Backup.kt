package ru.sokolovromann.myshopping.core.domain.model

data class Backup(
    val directory: BackupDirectory,
    val currentCarts: Collection<CartWithProducts>,
    val archivedCarts: Collection<CartWithProducts>,
    val suggestions: Collection<SuggestionWithFabrics>
)