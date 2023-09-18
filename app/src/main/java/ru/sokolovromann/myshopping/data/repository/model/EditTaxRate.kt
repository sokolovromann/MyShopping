package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidMoneyException

data class EditTaxRate(
    private val appConfig: AppConfig = AppConfig()
) {

    private val userPreferences = appConfig.userPreferences

    fun createTaxRate(taxRate: Float?): Result<Money> {
        return if (taxRate == null) {
            val exception = InvalidMoneyException("Tax rate must not be null")
            Result.failure(exception)
        } else {
            val success = userPreferences.taxRate.copy(value = taxRate)
            Result.success(success)
        }
    }

    fun getFieldTaxRate(): String {
        val taxRate = userPreferences.taxRate
        return if (taxRate.isEmpty()) "" else taxRate.getFormattedValueWithoutSeparators()
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }
}