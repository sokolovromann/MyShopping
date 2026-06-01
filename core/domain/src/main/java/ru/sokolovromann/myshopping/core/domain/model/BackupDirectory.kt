package ru.sokolovromann.myshopping.core.domain.model

@JvmInline
value class BackupDirectory(val value: String) {

    init {
        require(value.isNotEmpty()) {
            "The value must not be empty."
        }
    }
}