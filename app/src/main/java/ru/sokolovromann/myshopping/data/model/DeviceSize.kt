package ru.sokolovromann.myshopping.data.model

sealed class DeviceSize {

    object Small : DeviceSize()

    object Medium : DeviceSize()

    object Large : DeviceSize()

    data class Error(val exception: Exception) : DeviceSize()

    fun isSmartphoneScreen(): Boolean {
        return this == Medium
    }

    override fun toString(): String = when (this) {
        Small -> "Small"
        Medium -> "Medium"
        Large -> "Large"
        is Error -> "Error: ${exception.message}"
    }
}