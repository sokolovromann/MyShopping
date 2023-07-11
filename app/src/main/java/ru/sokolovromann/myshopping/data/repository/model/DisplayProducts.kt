package ru.sokolovromann.myshopping.data.repository.model

enum class DisplayProducts {

    /** Display vertically */
    COLUMNS,

    /** Display horizontally */
    ROW,

    /** Always hide */
    HIDE,

    /** Hide if shopping list has name */
    HIDE_IF_HAS_TITLE;

    companion object {
        val DefaultValue: DisplayProducts = HIDE_IF_HAS_TITLE

        fun valueOfOrDefault(value: String): DisplayProducts = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}