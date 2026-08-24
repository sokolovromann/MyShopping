package ru.sokolovromann.myshopping.feature.migration

sealed class MigrationUiState {

    data object Initial : MigrationUiState()

    data object Migrating : MigrationUiState()

    data object Finish : MigrationUiState()
}