package ru.sokolovromann.myshopping.data.repository.model

sealed class DeviceSize {

    object Small : DeviceSize()

    object Medium : DeviceSize()

    object Large : DeviceSize()

    data class Error(val exception: Exception) : DeviceSize()

    override fun toString(): String = when (this) {
        Small -> "Small"
        Medium -> "Medium"
        Large -> "Large"
        is Error -> "Error: ${exception.message}"
    }
}