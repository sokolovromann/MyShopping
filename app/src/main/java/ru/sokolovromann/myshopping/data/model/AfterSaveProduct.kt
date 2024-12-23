package ru.sokolovromann.myshopping.data.model

enum class AfterSaveProduct {

    CLOSE, OPEN_NEW_SCREEN, NOTHING;

    companion object {
        val DefaultValue: AfterSaveProduct = CLOSE

        fun valueOfOrDefault(name: String?): AfterSaveProduct = try {
            val value = name ?: throw NullPointerException()
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }

    override fun toString(): String {
        return name
    }
}