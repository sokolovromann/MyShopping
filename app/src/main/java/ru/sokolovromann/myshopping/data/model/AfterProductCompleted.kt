package ru.sokolovromann.myshopping.data.model

enum class AfterProductCompleted {

    NOTHING, EDIT, DELETE;

    companion object {
        val DefaultValue: AfterProductCompleted = NOTHING

        fun valueOfOrDefault(name: String?, editAfterCompleted: Boolean?): AfterProductCompleted = try {
            if (editAfterCompleted == true) {
                EDIT
            } else {
                val value = name ?: throw NullPointerException()
                valueOf(value)
            }
        } catch (e: Exception) {
            DefaultValue
        }
    }

    override fun toString(): String {
        return name
    }
}