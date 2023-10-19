package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.UserPreferencesDefaults
import java.text.DecimalFormat

@Deprecated("Use /SettingsWithConfig")
data class Settings(
    val developerName: String = "",
    val developerEmail: String = "",
    val appVersion: String = "",
    val appGithubLink: String = "",
    val privacyPolicyLink: String = "",
    val termsAndConditionsLink: String = "",
    private val appConfig: AppConfig = AppConfig()
) {

    private val userPreferences = appConfig.userPreferences

    fun createMoneyDecimalFormat(displayZeros: Boolean?): Result<DecimalFormat> {
        return if (displayZeros == null) {
            val exception = NullPointerException("Display zeros must not be null")
            Result.failure(exception)
        } else {
            val success = UserPreferencesDefaults.getMoneyDecimalFormat().apply {
                if (displayZeros == true) {
                    minimumFractionDigits = 0
                }
            }
            return Result.success(success)
        }
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun getDisplayCompleted(): DisplayCompleted {
        return userPreferences.displayCompleted
    }

    fun getShoppingsProducts(): DisplayProducts {
        return userPreferences.displayShoppingsProducts
    }

    fun getCurrency(): Currency {
        return userPreferences.currency
    }

    fun getMoneyDecimalFormat(): DecimalFormat {
        return userPreferences.moneyDecimalFormat
    }

    fun getTaxRate(): Money {
        return userPreferences.taxRate
    }

    fun displayMoney(): Boolean {
        return userPreferences.displayMoney
    }

    fun displayOtherFields(): Boolean {
        return userPreferences.displayOtherFields
    }

    fun displayDefaultAutocompletes(): Boolean {
        return userPreferences.displayDefaultAutocompletes
    }

    fun editProductAfterCompleted(): Boolean {
        return userPreferences.editProductAfterCompleted
    }

    fun saveProductToAutocompletes(): Boolean {
        return userPreferences.saveProductToAutocompletes
    }

    fun isNightTheme(): Boolean {
        return userPreferences.nightTheme
    }

    fun isCompletedWithCheckbox(): Boolean {
        return userPreferences.completedWithCheckbox
    }

    fun isColoredCheckbox(): Boolean {
        return userPreferences.coloredCheckbox
    }

    fun isEnterToSaveProduct(): Boolean {
        return userPreferences.enterToSaveProduct
    }

    fun isSmartphoneScreen(): Boolean {
        return appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium
    }

    fun isMultiColumns(): Boolean {
        return !isSmartphoneScreen()
    }
}