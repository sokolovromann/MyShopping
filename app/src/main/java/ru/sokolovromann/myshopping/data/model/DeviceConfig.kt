package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.DeviceSize

data class DeviceConfig(
    val screenWidthDp: Int = UNKNOWN_SIZE_DP,
    val screenHeightDp: Int = UNKNOWN_SIZE_DP
) {

    companion object {
        const val UNKNOWN_SIZE_DP = 0
    }

    fun getDeviceSize(): DeviceSize {
        return if (screenWidthDp <= UNKNOWN_SIZE_DP || screenHeightDp <= UNKNOWN_SIZE_DP) {
            val exception = IllegalArgumentException("Sizes must be greater than zero")
            DeviceSize.Error(exception)
        } else {
            if (screenWidthDp < 200) {
                DeviceSize.Small
            } else if (screenWidthDp < 600) {
                DeviceSize.Medium
            } else {
                DeviceSize.Large
            }
        }
    }
}